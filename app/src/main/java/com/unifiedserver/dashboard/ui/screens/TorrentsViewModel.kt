package com.unifiedserver.dashboard.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unifiedserver.dashboard.data.models.SearchResult
import com.unifiedserver.dashboard.data.models.Torrent
import com.unifiedserver.dashboard.data.repository.TorrentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class TorrentSortOption { NAME, SIZE, SEEDS, DATE_ADDED, PROGRESS, RATIO, SPEED_DOWN, SPEED_UP }
enum class SortOrder { ASCENDING, DESCENDING }

data class TorrentsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val lastUpdated: Long? = null,
    val torrents: List<Torrent> = emptyList(),
    val snackbarMessage: String? = null,
    val isMutating: Boolean = false,
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

    fun addTorrent(url: String, onAdded: () -> Unit = {}) = mutate("Download added", onAdded) {
        repository.addTorrent(url)
    }
    fun pauseTorrent(hash: String) = mutate("Download paused") { repository.pauseTorrents(hash) }
    fun resumeTorrent(hash: String) = mutate("Download resumed") { repository.resumeTorrents(hash) }
    fun deleteTorrent(hash: String, deleteFiles: Boolean = false) = mutate("Download removed") {
        repository.deleteTorrents(hash, deleteFiles)
    }

    private fun mutate(message: String, onSuccess: () -> Unit = {}, block: suspend () -> Result<Unit>) {
        if (_uiState.value.isMutating) return
        _uiState.value = _uiState.value.copy(isMutating = true)
        viewModelScope.launch {
            block().onSuccess {
                _uiState.value = _uiState.value.copy(snackbarMessage = message)
                onSuccess()
                loadTorrents(silent = true)
            }.onFailure {
                _uiState.value = _uiState.value.copy(snackbarMessage = it.message ?: "Action failed. Try again.")
            }
            _uiState.value = _uiState.value.copy(isMutating = false)
        }
    }

    fun startSearch(query: String) {
        if (query.isBlank()) return
        cancelSearch()
        _uiState.value = _uiState.value.copy(searchQuery = query.trim(), isSearching = true,
            searchResults = emptyList(), searchTotal = 0, searchOffset = 0, searchError = null, searchId = null,
            searchStatus = "Starting search…")
        searchJob = viewModelScope.launch {
            val started = repository.startSearch(query.trim())
            val id = started.getOrNull()
            if (id == null || id < 0) {
                _uiState.value = _uiState.value.copy(isSearching = false,
                    searchError = started.exceptionOrNull()?.message ?: "Invalid search response.")
                return@launch
            }
            _uiState.value = _uiState.value.copy(searchId = id)
            repeat(150) {
                val result = repository.getSearchResults(id)
                if (_uiState.value.searchId != id) return@launch
                val response = result.getOrNull()
                if (response == null) {
                    _uiState.value = _uiState.value.copy(isSearching = false,
                        searchError = result.exceptionOrNull()?.message ?: "Search failed.")
                    repository.stopSearch(id)
                    return@launch
                }
                _uiState.value = _uiState.value.copy(searchResults = response.results,
                    searchTotal = response.total, searchOffset = response.results.size, searchStatus = response.status)
                if (response.status.equals("Stopped", true) || response.status.equals("Complete", true)) {
                    _uiState.value = _uiState.value.copy(isSearching = false)
                    return@launch
                }
                delay(2_000)
            }
            repository.stopSearch(id)
            _uiState.value = _uiState.value.copy(isSearching = false, searchStatus = "Timed out",
                searchError = "Search timed out. You can use the results found or search again.")
        }
    }

    private fun cancelSearch() {
        searchJob?.cancel()
        moreJob?.cancel()
        _uiState.value.searchId?.let { id -> viewModelScope.launch { repository.stopSearch(id) } }
        _uiState.value = _uiState.value.copy(isLoadingMore = false)
    }

    fun stopSearch() {
        val id = _uiState.value.searchId
        searchJob?.cancel()
        moreJob?.cancel()
        _uiState.value = _uiState.value.copy(isSearching = false, isLoadingMore = false, searchStatus = "Stopped")
        if (id != null) viewModelScope.launch {
            repository.stopSearch(id).onFailure {
                _uiState.value = _uiState.value.copy(searchError = "Local polling stopped; server stop failed. ${it.message}")
            }
        }
    }

    fun clearSearch() {
        cancelSearch()
        _uiState.value = _uiState.value.copy(searchQuery = "", searchId = null, searchStatus = "",
            searchResults = emptyList(), searchTotal = 0, searchOffset = 0, searchError = null, isSearching = false)
    }

    fun loadMoreResults() {
        val state = _uiState.value
        val id = state.searchId ?: return
        if (state.isSearching || state.isLoadingMore || state.searchOffset >= state.searchTotal) return
        _uiState.value = state.copy(isLoadingMore = true, searchError = null)
        moreJob = viewModelScope.launch {
            repository.getSearchResults(id, state.searchOffset).onSuccess {
                if (_uiState.value.searchId == id) _uiState.value = _uiState.value.copy(
                    searchResults = (_uiState.value.searchResults + it.results).distinctBy { item -> item.fileUrl },
                    searchTotal = it.total,
                    searchOffset = if (it.results.isEmpty()) it.total else state.searchOffset + it.results.size,
                    isLoadingMore = false)
            }.onFailure {
                _uiState.value = _uiState.value.copy(isLoadingMore = false, searchError = it.message)
            }
        }
    }

    fun refreshSearchResults() = loadMoreResults()
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

    fun getSortedTorrents(): List<Torrent> = sortTorrents(_uiState.value.torrents,
        _uiState.value.torrentSortOption, _uiState.value.torrentSortOrder)

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
