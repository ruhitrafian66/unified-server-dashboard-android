package com.unifiedserver.dashboard.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unifiedserver.dashboard.data.models.*
import com.unifiedserver.dashboard.data.repository.MediaRepository
import com.unifiedserver.dashboard.data.repository.TorrentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MediaUiState(
    // Trending
    val trendingTv: List<TmdbMediaItem> = emptyList(),
    val trendingMovies: List<TmdbMediaItem> = emptyList(),
    val isLoadingTrending: Boolean = false,
    val trendingError: String? = null,

    // Search
    val searchQuery: String = "",
    val searchResults: List<TmdbMediaItem> = emptyList(),
    val isSearching: Boolean = false,
    val searchError: String? = null,

    // Detail sheet
    val selectedItem: TmdbMediaItem? = null,
    val movieDetail: TmdbMovieDetail? = null,
    val tvDetail: TmdbTvDetail? = null,
    val imdbRating: ImdbRatingResponse? = null,
    val isLoadingDetail: Boolean = false,
    val detailError: String? = null,

    // Download
    val isDownloading: Boolean = false,
    val downloadQuery: String? = null,
    val snackbarMessage: String? = null
)

@HiltViewModel
class MediaViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val torrentRepository: TorrentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MediaUiState())
    val uiState: StateFlow<MediaUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadTrending()
    }

    fun loadTrending() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingTrending = true, trendingError = null)
            val tvResult = mediaRepository.getTrendingTv()
            val moviesResult = mediaRepository.getTrendingMovies()
            _uiState.value = _uiState.value.copy(
                isLoadingTrending = false,
                trendingTv = tvResult.getOrElse { emptyList() },
                trendingMovies = moviesResult.getOrElse { emptyList() },
                trendingError = when {
                    tvResult.isFailure && moviesResult.isFailure ->
                        tvResult.exceptionOrNull()?.message
                    else -> null
                }
            )
        }
    }

    // ─── Live search with debounce ────────────────────────────────────────────

    fun onSearchQueryChange(query: String, mediaTypeFilter: String = "all") {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        searchJob?.cancel()
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(
                searchResults = emptyList(),
                isSearching = false,
                searchError = null
            )
            return
        }
        searchJob = viewModelScope.launch {
            delay(400) // debounce 400ms
            performSearch(query, mediaTypeFilter)
        }
    }

    fun performSearch(query: String, mediaTypeFilter: String = "all") {
        if (query.isBlank()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearching = true, searchError = null)
            val result = when (mediaTypeFilter) {
                "tv" -> mediaRepository.searchTv(query)
                "movie" -> mediaRepository.searchMovies(query)
                else -> mediaRepository.searchAll(query)
            }
            result
                .onSuccess { items ->
                    _uiState.value = _uiState.value.copy(
                        isSearching = false,
                        searchResults = items
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isSearching = false,
                        searchError = e.message
                    )
                }
        }
    }

    fun clearSearch() {
        searchJob?.cancel()
        _uiState.value = _uiState.value.copy(
            searchQuery = "",
            searchResults = emptyList(),
            isSearching = false,
            searchError = null
        )
    }

    // ─── Detail loading ───────────────────────────────────────────────────────

    fun openDetail(item: TmdbMediaItem) {
        _uiState.value = _uiState.value.copy(
            selectedItem = item,
            movieDetail = null,
            tvDetail = null,
            imdbRating = null,
            isLoadingDetail = true,
            detailError = null
        )
        val isMovie = (item.mediaType ?: "movie") == "movie"
        viewModelScope.launch {
            if (isMovie) {
                mediaRepository.getMovieDetail(item.id)
                    .onSuccess { detail ->
                        _uiState.value = _uiState.value.copy(
                            movieDetail = detail,
                            isLoadingDetail = false
                        )
                        // Fetch IMDB rating if we have an imdb_id
                        val imdbId = detail.imdbId ?: detail.externalIds?.imdbId
                        if (!imdbId.isNullOrBlank()) {
                            fetchImdbRating(imdbId)
                        }
                    }
                    .onFailure { e ->
                        _uiState.value = _uiState.value.copy(
                            isLoadingDetail = false,
                            detailError = e.message
                        )
                    }
            } else {
                mediaRepository.getTvDetail(item.id)
                    .onSuccess { detail ->
                        _uiState.value = _uiState.value.copy(
                            tvDetail = detail,
                            isLoadingDetail = false
                        )
                        val imdbId = detail.externalIds?.imdbId
                        if (!imdbId.isNullOrBlank()) {
                            fetchImdbRating(imdbId)
                        }
                    }
                    .onFailure { e ->
                        _uiState.value = _uiState.value.copy(
                            isLoadingDetail = false,
                            detailError = e.message
                        )
                    }
            }
        }
    }

    private fun fetchImdbRating(imdbId: String) {
        viewModelScope.launch {
            mediaRepository.getImdbRating(imdbId)
                .onSuccess { rating ->
                    _uiState.value = _uiState.value.copy(imdbRating = rating)
                }
                // Silently ignore rating failures — not critical
        }
    }

    fun closeDetail() {
        _uiState.value = _uiState.value.copy(
            selectedItem = null,
            movieDetail = null,
            tvDetail = null,
            imdbRating = null,
            detailError = null
        )
    }

    // ─── Download via qBittorrent search ─────────────────────────────────────

    fun downloadItem(item: TmdbMediaItem) {
        val title = item.displayTitle
        val year = item.year
        // Format query as "Title Year" e.g. "Dune 2021"
        val query = if (year.isNotBlank()) "$title $year" else title

        _uiState.value = _uiState.value.copy(
            isDownloading = true,
            downloadQuery = query
        )
        viewModelScope.launch {
            torrentRepository.startSearch(query)
                .onSuccess { searchId ->
                    _uiState.value = _uiState.value.copy(
                        isDownloading = false,
                        snackbarMessage = "Search started for \"$query\" in Torrents"
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isDownloading = false,
                        snackbarMessage = "Failed to start download search: ${e.message}"
                    )
                }
        }
    }

    fun downloadFromDetail() {
        val state = _uiState.value
        val item = state.selectedItem ?: return

        // Use richer detail title if available
        val title = state.movieDetail?.title ?: state.tvDetail?.name ?: item.displayTitle
        val year = state.movieDetail?.year ?: state.tvDetail?.year ?: item.year
        val query = if (year.isNotBlank()) "$title $year" else title

        _uiState.value = _uiState.value.copy(isDownloading = true, downloadQuery = query)
        viewModelScope.launch {
            torrentRepository.startSearch(query)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isDownloading = false,
                        snackbarMessage = "Torrent search started for \"$query\""
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isDownloading = false,
                        snackbarMessage = "Failed: ${e.message}"
                    )
                }
        }
    }

    fun clearSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }
}
