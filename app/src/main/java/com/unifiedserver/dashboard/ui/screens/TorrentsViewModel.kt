package com.unifiedserver.dashboard.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unifiedserver.dashboard.data.models.SearchResult
import com.unifiedserver.dashboard.data.models.Torrent
import com.unifiedserver.dashboard.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

enum class TorrentSortOption { NAME, SIZE, SEEDS, DATE_ADDED, PROGRESS, RATIO, SPEED_DOWN, SPEED_UP }
enum class SortOrder { ASCENDING, DESCENDING }
enum class DownloadStage { SEARCHING, SELECTING, ADDING, SUCCEEDED, FAILED, CANCELLED }

data class DownloadOperation(
    val request: DownloadRequest,
    val stage: DownloadStage,
    val message: String,
    val source: SearchResult? = null,
    val canRetry: Boolean = true
)

data class TorrentsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val lastUpdated: Long? = null,
    val torrents: List<Torrent> = emptyList(),
    val snackbarMessage: String? = null,
    val isMutating: Boolean = false,
    val operation: DownloadOperation? = null,
    val torrentSortOption: TorrentSortOption = TorrentSortOption.DATE_ADDED,
    val torrentSortOrder: SortOrder = SortOrder.DESCENDING,
    val isSearching: Boolean = false,
    val searchQuery: String = "",
    val searchId: Int? = null,
    val searchStatus: String = "",
    val searchResults: List<SearchResult> = emptyList(),
    val searchTotal: Int = 0,
    val searchOffset: Int = 0,
    val isLoadingMore: Boolean = false,
    val searchError: String? = null,
    val searchResultsSortOption: TorrentSortOption = TorrentSortOption.SEEDS,
    val searchResultsSortOrder: SortOrder = SortOrder.DESCENDING
)

@HiltViewModel
class TorrentsViewModel @Inject constructor(private val repository: TorrentRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(TorrentsUiState())
    val uiState = _uiState.asStateFlow()
    private var searchJob: Job? = null
    private var moreJob: Job? = null
    private var refreshJob: Job? = null
    private var generation = 0
    private val mutations = Mutex()

    fun loadTorrents(silent: Boolean = false) {
        if (refreshJob?.isActive == true) return
        refreshJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = !silent)
            repository.getTorrents().onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false, torrents = it,
                    error = null, lastUpdated = System.currentTimeMillis())
            }.onFailure {
                _uiState.value = _uiState.value.copy(isLoading = false, error = it.message ?: "Could not refresh downloads.")
            }
        }
    }

    fun addTorrent(url: String, onAdded: () -> Unit = {}) = mutate("Download added", onAdded) { repository.addTorrent(url) }
    fun pauseTorrent(hash: String) = mutate("Download paused") { repository.pauseTorrents(hash) }
    fun resumeTorrent(hash: String) = mutate("Download resumed") { repository.resumeTorrents(hash) }
    fun deleteTorrent(hash: String, deleteFiles: Boolean = false) = mutate("Download removed") {
        repository.deleteTorrents(hash, deleteFiles)
    }

    private fun mutate(message: String, onSuccess: () -> Unit = {}, block: suspend () -> Result<Unit>) {
        if (_uiState.value.isMutating) return
        _uiState.value = _uiState.value.copy(isMutating = true)
        viewModelScope.launch {
            mutations.withLock {
                _uiState.value = _uiState.value.copy(isMutating = true)
                try {
                    block().onSuccess {
                        _uiState.value = _uiState.value.copy(snackbarMessage = message)
                        onSuccess()
                        loadTorrents(silent = true)
                    }.onFailure {
                        _uiState.value = _uiState.value.copy(snackbarMessage = it.message ?: "Action could not be confirmed. Refresh before retrying.")
                    }
                } finally {
                    _uiState.value = _uiState.value.copy(isMutating = false)
                }
            }
        }
    }

    fun startSearch(query: String) = beginSearch(DownloadRequest(query.trim()))
    fun autoPickAndAdd(request: DownloadRequest) = beginSearch(request.copy(autoPick = true))

    private fun beginSearch(request: DownloadRequest) {
        if (request.query.isBlank()) return
        if (_uiState.value.isMutating) {
            _uiState.value = _uiState.value.copy(snackbarMessage = "Wait for the current action to finish.")
            return
        }
        generation++
        val token = generation
        searchJob?.cancel()
        moreJob?.cancel()
        _uiState.value = _uiState.value.copy(searchQuery = request.query, isSearching = true,
            searchResults = emptyList(), searchTotal = 0, searchOffset = 0, searchError = null,
            searchId = null, isLoadingMore = false, searchStatus = "Starting search…",
            operation = if (request.autoPick) DownloadOperation(request, DownloadStage.SEARCHING, "Searching for matching sources…") else null)
        searchJob = viewModelScope.launch {
            var id: Int? = null
            try {
                withTimeout(300_000) {
                    id = repository.startSearch(request.query).getOrThrow()
                    require(id!! >= 0) { "Invalid search response." }
                    update(token) { it.copy(searchId = id) }
                    var complete = false
                    while (!complete) {
                        val response = repository.getSearchResults(id!!).getOrThrow()
                        update(token) { it.copy(searchResults = response.results, searchTotal = response.total,
                            searchOffset = response.results.size, searchStatus = response.status,
                            operation = it.operation?.copy(message = "Searching… ${response.total} sources reported")) }
                        complete = response.status.equals("Stopped", true) || response.status.equals("Complete", true)
                        if (!complete) delay(2_000)
                    }
                    if (request.autoPick) {
                        update(token) { it.copy(operation = it.operation?.copy(stage = DownloadStage.SELECTING,
                            message = "Checking all results against your rules…")) }
                        val results = readAllResults(id!!, token)
                        val source = selectBestSource(request, results)
                            ?: error("No source meets the title, year/season, quality, seeder and size rules. Review sources manually or change your rules.")
                        mutations.withLock {
                            ensureActive()
                            update(token) { it.copy(isSearching = false, isMutating = true,
                                operation = DownloadOperation(request, DownloadStage.ADDING,
                                    "Adding the selected source. Cancellation is no longer available.", source, canRetry = false)) }
                            try {
                                repository.addTorrent(source.fileUrl).getOrThrow()
                                update(token) { it.copy(operation = DownloadOperation(request, DownloadStage.SUCCEEDED,
                                    "Download added", source, canRetry = false), snackbarMessage = "Download added") }
                                loadTorrents(silent = true)
                            } finally {
                                update(token) { it.copy(isMutating = false) }
                            }
                        }
                    }
                    update(token) { it.copy(isSearching = false, searchStatus = "Complete") }
                }
            } catch (timeout: TimeoutCancellationException) {
                failSearch(token, request, "Search timed out. No automatic fallback was selected.")
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                failSearch(token, request, error.message ?: "Search failed. Try again.")
            } finally {
                // The server owns its search too: stop that exact ID on completion/cancellation.
                id?.let { searchId ->
                    withContext(NonCancellable) {
                        withTimeoutOrNull(5_000) { repository.stopSearch(searchId) }
                    }
                }
            }
        }
    }

    private suspend fun readAllResults(id: Int, token: Int): List<SearchResult> {
        val state = _uiState.value
        require(state.searchTotal <= 2_000) { "Too many results to safely auto-select. Narrow the search or select manually." }
        val all = state.searchResults.toMutableList()
        var offset = state.searchOffset
        val total = state.searchTotal
        check(total >= offset) { "Result counts are inconsistent. Please select a source manually." }
        while (offset < total) {
            val page = repository.getSearchResults(id, offset).getOrThrow()
            check(page.results.isNotEmpty()) { "Some results are unavailable. Please select a source manually." }
            check(page.status.equals("Stopped", true) || page.status.equals("Complete", true)) {
                "Search results changed while selecting. Please retry."
            }
            all += page.results
            offset += page.results.size
            check(page.total == total) { "Search results changed while paging. Please retry." }
            require(total <= 2_000 && offset <= 2_000) { "Too many results. Please select manually." }
            update(token) { it.copy(searchResults = all.distinctBy { source -> source.fileUrl },
                searchOffset = offset, searchTotal = total,
                operation = it.operation?.copy(message = "Checked $offset of $total results")) }
        }
        return all
    }

    private fun update(token: Int, transform: (TorrentsUiState) -> TorrentsUiState) {
        if (token == generation) _uiState.value = transform(_uiState.value)
    }

    private fun failSearch(token: Int, request: DownloadRequest, message: String) {
        update(token) { state ->
            val attemptedAdd = state.operation?.stage == DownloadStage.ADDING
            val explanation = if (attemptedAdd) "Could not confirm whether the download was added. Refresh Downloads before trying again." else message
            state.copy(isSearching = false, isMutating = false, searchError = explanation, searchStatus = "Failed",
                operation = if (request.autoPick) DownloadOperation(request, DownloadStage.FAILED,
                    explanation, state.operation?.source, canRetry = !attemptedAdd) else null)
        }
    }

    fun stopSearch() {
        if (_uiState.value.operation?.stage == DownloadStage.ADDING) return
        generation++
        searchJob?.cancel()
        moreJob?.cancel()
        _uiState.value = _uiState.value.copy(isSearching = false, isLoadingMore = false, searchStatus = "Stopped",
            operation = _uiState.value.operation?.let {
                if (it.stage in listOf(DownloadStage.SEARCHING, DownloadStage.SELECTING))
                    it.copy(stage = DownloadStage.CANCELLED, message = "Search cancelled. No download was added.", canRetry = true)
                else it
            })
    }

    fun clearSearch() {
        if (_uiState.value.operation?.stage == DownloadStage.ADDING) return
        stopSearch()
        _uiState.value = _uiState.value.copy(searchQuery = "", searchId = null, searchStatus = "",
            searchResults = emptyList(), searchTotal = 0, searchOffset = 0, searchError = null, operation = null)
    }

    fun retryAutomaticDownload() {
        val operation = _uiState.value.operation ?: return
        if (operation.canRetry && operation.stage in listOf(DownloadStage.FAILED, DownloadStage.CANCELLED)) beginSearch(operation.request)
    }

    fun dismissOperation() {
        if (_uiState.value.operation?.stage in listOf(DownloadStage.SUCCEEDED, DownloadStage.FAILED, DownloadStage.CANCELLED))
            _uiState.value = _uiState.value.copy(operation = null)
    }

    fun loadMoreResults() {
        val state = _uiState.value
        val id = state.searchId ?: return
        if (state.isSearching || state.isLoadingMore || state.searchOffset >= state.searchTotal) return
        val token = generation
        _uiState.value = state.copy(isLoadingMore = true, searchError = null)
        moreJob = viewModelScope.launch {
            repository.getSearchResults(id, state.searchOffset).onSuccess { page ->
                update(token) { it.copy(searchResults = (it.searchResults + page.results).distinctBy { source -> source.fileUrl },
                    searchTotal = page.total, searchOffset = if (page.results.isEmpty()) page.total else state.searchOffset + page.results.size,
                    isLoadingMore = false) }
            }.onFailure { error ->
                update(token) { it.copy(isLoadingMore = false, searchError = error.message) }
            }
        }
    }

    fun clearSnackbar() { _uiState.value = _uiState.value.copy(snackbarMessage = null) }
    fun refresh() = loadTorrents()
    fun setTorrentSort(option: TorrentSortOption) {
        _uiState.value = _uiState.value.copy(torrentSortOption = option,
            torrentSortOrder = nextOrder(option == _uiState.value.torrentSortOption, _uiState.value.torrentSortOrder))
    }
    fun setSearchResultsSort(option: TorrentSortOption) {
        _uiState.value = _uiState.value.copy(searchResultsSortOption = option,
            searchResultsSortOrder = nextOrder(option == _uiState.value.searchResultsSortOption, _uiState.value.searchResultsSortOrder))
    }
    private fun nextOrder(same: Boolean, old: SortOrder) =
        if (same && old == SortOrder.DESCENDING) SortOrder.ASCENDING else SortOrder.DESCENDING
    fun getSortedTorrents() = sortTorrents(_uiState.value.torrents, _uiState.value.torrentSortOption, _uiState.value.torrentSortOrder)
    fun getSortedSearchResults(): List<SearchResult> {
        val state = _uiState.value
        val sorted = when (state.searchResultsSortOption) {
            TorrentSortOption.NAME -> state.searchResults.sortedBy { it.fileName.lowercase() }
            TorrentSortOption.SIZE -> state.searchResults.sortedBy { it.fileSize }
            else -> state.searchResults.sortedBy { it.nbSeeders }
        }
        return if (state.searchResultsSortOrder == SortOrder.DESCENDING) sorted.reversed() else sorted
    }
}

internal fun sortTorrents(items: List<Torrent>, option: TorrentSortOption, order: SortOrder): List<Torrent> {
    val sorted = when (option) {
        TorrentSortOption.NAME -> items.sortedBy { it.name.lowercase() }
        TorrentSortOption.SIZE -> items.sortedBy { it.size }
        TorrentSortOption.SEEDS -> items.sortedBy { it.num_seeds }
        TorrentSortOption.DATE_ADDED -> items.sortedBy { it.addedOn }
        TorrentSortOption.PROGRESS -> items.sortedBy { it.progress }
        TorrentSortOption.RATIO -> items.sortedBy { it.ratio }
        TorrentSortOption.SPEED_DOWN -> items.sortedBy { it.dlspeed }
        TorrentSortOption.SPEED_UP -> items.sortedBy { it.upspeed }
    }
    return if (order == SortOrder.DESCENDING) sorted.reversed() else sorted
}
