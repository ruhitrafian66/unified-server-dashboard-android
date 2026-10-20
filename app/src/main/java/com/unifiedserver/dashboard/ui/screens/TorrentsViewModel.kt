package com.unifiedserver.dashboard.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unifiedserver.dashboard.data.models.SearchResult
import com.unifiedserver.dashboard.data.models.Torrent
import com.unifiedserver.dashboard.data.repository.TorrentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class TorrentSortOption {
    NAME, SIZE, SEEDS, DATE_ADDED, PROGRESS, RATIO, SPEED_DOWN, SPEED_UP
}

enum class SortOrder {
    ASCENDING, DESCENDING
}

data class TorrentsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val torrents: List<Torrent> = emptyList(),
    val snackbarMessage: String? = null,
    // Sorting
    val torrentSortOption: TorrentSortOption = TorrentSortOption.DATE_ADDED,
    val torrentSortOrder: SortOrder = SortOrder.DESCENDING,
    // Search
    val isSearching: Boolean = false,
    val searchQuery: String = "",
    val searchId: Int? = null,
    val searchStatus: String = "",
    val searchResults: List<SearchResult> = emptyList(),
    val searchError: String? = null,
    val searchResultsSortOption: TorrentSortOption = TorrentSortOption.SEEDS,
    val searchResultsSortOrder: SortOrder = SortOrder.DESCENDING
)

@HiltViewModel
class TorrentsViewModel @Inject constructor(
    private val repository: TorrentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TorrentsUiState())
    val uiState: StateFlow<TorrentsUiState> = _uiState.asStateFlow()

    init {
        loadTorrents()
    }

    fun loadTorrents() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            repository.getTorrents()
                .onSuccess { torrents ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        torrents = torrents
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load torrents"
                    )
                }
        }
    }

    fun addTorrent(url: String) {
        viewModelScope.launch {
            repository.addTorrent(url)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(snackbarMessage = "Torrent added")
                    loadTorrents()
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        snackbarMessage = "Error: ${e.message}"
                    )
                }
        }
    }

    fun pauseTorrent(hash: String) {
        viewModelScope.launch {
            repository.pauseTorrents(hash)
                .onSuccess { loadTorrents() }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        snackbarMessage = "Error: ${e.message}"
                    )
                }
        }
    }

    fun resumeTorrent(hash: String) {
        viewModelScope.launch {
            repository.resumeTorrents(hash)
                .onSuccess { loadTorrents() }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        snackbarMessage = "Error: ${e.message}"
                    )
                }
        }
    }

    fun deleteTorrent(hash: String, deleteFiles: Boolean = false) {
        viewModelScope.launch {
            repository.deleteTorrents(hash, deleteFiles)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(snackbarMessage = "Torrent deleted")
                    loadTorrents()
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        snackbarMessage = "Error: ${e.message}"
                    )
                }
        }
    }

    fun startSearch(query: String) {
        viewModelScope.launch {
            // Validate search query
            if (query.isBlank()) {
                _uiState.value = _uiState.value.copy(
                    searchError = "Search query cannot be empty"
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(
                searchQuery = query,
                isSearching = true,
                searchResults = emptyList(),
                searchError = null,
                searchStatus = "Initializing..."
            )
            repository.startSearch(query)
                .onSuccess { id ->
                    // Validate search ID
                    if (id <= 0) {
                        _uiState.value = _uiState.value.copy(
                            isSearching = false,
                            searchError = "Invalid search ID returned from server"
                        )
                        return@onSuccess
                    }
                    _uiState.value = _uiState.value.copy(searchId = id)
                    // Poll for results with progressive fetching
                    pollForResults(id)
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isSearching = false,
                        searchError = "Failed to start search: ${e.message}"
                    )
                }
        }
    }

    private suspend fun pollForResults(id: Int) {
        val maxAttempts = 150 // 5 minutes with 2-second intervals
        var attempts = 0
        var lastResultCount = 0
        var noChangeCount = 0
        
        // Wait 3 seconds for backend to start gathering results
        kotlinx.coroutines.delay(3000)
        
        while (attempts < maxAttempts) {
            attempts++
            repository.getSearchStatus(id)
                .onSuccess { statusItem ->
                    _uiState.value = _uiState.value.copy(
                        searchStatus = "${statusItem.status} (${statusItem.total} found)"
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        searchStatus = "Fetching results..."
                    )
                }
            
            // Fetch results regardless of status
            repository.getSearchResults(id)
                .onSuccess { results ->
                    _uiState.value = _uiState.value.copy(
                        searchResults = results.results,
                        searchStatus = "${results.status} (${results.total} found)"
                    )
                    
                    // Check if we've stopped getting new results
                    if (results.results.size == lastResultCount && results.results.isNotEmpty()) {
                        noChangeCount++
                        // If no new results after 3 checks (6 seconds), consider search complete
                        if (noChangeCount >= 3 || results.status.equals("Stopped", ignoreCase = true)) {
                            _uiState.value = _uiState.value.copy(isSearching = false)
                            return@onSuccess
                        }
                    } else {
                        noChangeCount = 0
                    }
                    lastResultCount = results.results.size
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        searchStatus = "Error: ${e.message}"
                    )
                }
            
            if (attempts < maxAttempts) {
                kotlinx.coroutines.delay(2000)
            }
        }
        
        // Timeout fallback
        _uiState.value = _uiState.value.copy(
            isSearching = false,
            searchStatus = "Search completed or timed out"
        )
    }

    fun refreshSearchResults() {
        val id = _uiState.value.searchId ?: return
        viewModelScope.launch {
            repository.getSearchResults(id)
                .onSuccess { results ->
                    _uiState.value = _uiState.value.copy(
                        searchResults = results.results,
                        searchStatus = "${results.status} (${results.total} found)"
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        searchError = "Failed to refresh results: ${e.message}"
                    )
                }
        }
    }

    fun stopSearch() {
        val id = _uiState.value.searchId ?: return
        viewModelScope.launch {
            repository.stopSearch(id)
            _uiState.value = _uiState.value.copy(isSearching = false)
        }
    }

    fun clearSearch() {
        _uiState.value = _uiState.value.copy(
            searchQuery = "",
            searchId = null,
            searchStatus = "",
            searchResults = emptyList(),
            searchError = null,
            isSearching = false
        )
    }

    fun clearSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }

    fun refresh() = loadTorrents()

    // Sorting functions
    fun setTorrentSort(sortOption: TorrentSortOption) {
        val newOrder = if (_uiState.value.torrentSortOption == sortOption) {
            if (_uiState.value.torrentSortOrder == SortOrder.ASCENDING) SortOrder.DESCENDING
            else SortOrder.ASCENDING
        } else {
            SortOrder.DESCENDING
        }
        _uiState.value = _uiState.value.copy(
            torrentSortOption = sortOption,
            torrentSortOrder = newOrder
        )
    }

    fun setSearchResultsSort(sortOption: TorrentSortOption) {
        val newOrder = if (_uiState.value.searchResultsSortOption == sortOption) {
            if (_uiState.value.searchResultsSortOrder == SortOrder.ASCENDING) SortOrder.DESCENDING
            else SortOrder.ASCENDING
        } else {
            SortOrder.DESCENDING
        }
        _uiState.value = _uiState.value.copy(
            searchResultsSortOption = sortOption,
            searchResultsSortOrder = newOrder
        )
    }

    private fun sortTorrents(torrents: List<Torrent>, sortOption: TorrentSortOption, sortOrder: SortOrder): List<Torrent> {
        val sorted = when (sortOption) {
            TorrentSortOption.NAME -> torrents.sortedBy { it.name }
            TorrentSortOption.SIZE -> torrents.sortedBy { it.size }
            TorrentSortOption.SEEDS -> torrents.sortedBy { it.num_seeds }
            TorrentSortOption.DATE_ADDED -> torrents.sortedBy { it.seedingTime }
            TorrentSortOption.PROGRESS -> torrents.sortedBy { it.progress }
            TorrentSortOption.RATIO -> torrents.sortedBy { it.ratio }
            TorrentSortOption.SPEED_DOWN -> torrents.sortedBy { it.dlspeed }
            TorrentSortOption.SPEED_UP -> torrents.sortedBy { it.upspeed }
        }
        return if (sortOrder == SortOrder.DESCENDING) sorted.reversed() else sorted
    }

    private fun sortSearchResults(results: List<SearchResult>, sortOption: TorrentSortOption, sortOrder: SortOrder): List<SearchResult> {
        val sorted = when (sortOption) {
            TorrentSortOption.NAME -> results.sortedBy { it.fileName }
            TorrentSortOption.SIZE -> results.sortedBy { it.fileSize }
            TorrentSortOption.SEEDS -> results.sortedBy { it.nbSeeders }
            else -> results // Other sorts don't apply to search results
        }
        return if (sortOrder == SortOrder.DESCENDING) sorted.reversed() else sorted
    }

    // Get sorted torrents based on current sort option
    fun getSortedTorrents(): List<Torrent> {
        return sortTorrents(_uiState.value.torrents, _uiState.value.torrentSortOption, _uiState.value.torrentSortOrder)
    }

    // Get sorted search results based on current sort option
    fun getSortedSearchResults(): List<SearchResult> {
        return sortSearchResults(_uiState.value.searchResults, _uiState.value.searchResultsSortOption, _uiState.value.searchResultsSortOrder)
    }
}
