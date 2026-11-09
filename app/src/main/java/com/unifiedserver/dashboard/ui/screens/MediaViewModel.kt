package com.unifiedserver.dashboard.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unifiedserver.dashboard.data.models.*
import com.unifiedserver.dashboard.data.repository.MediaRepository
import com.unifiedserver.dashboard.data.repository.DownloadHandoff
import com.unifiedserver.dashboard.data.repository.DownloadRequest
import com.unifiedserver.dashboard.data.repository.AutoDownloadPolicy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MediaUiState(
    val trendingTv: List<TmdbMediaItem> = emptyList(),
    /** Movies newly out on digital/streaming (not trending, which includes cinema-only films). */
    val trendingMovies: List<TmdbMediaItem> = emptyList(),
    val moviesPage: Int = 0,
    val moviesTotalPages: Int = 0,
    val isLoadingMoreMovies: Boolean = false,
    val moviesLoadMoreError: String? = null,
    val isLoadingTrending: Boolean = false,
    val trendingError: String? = null,
    val searchQuery: String = "",
    val searchResults: List<TmdbMediaItem> = emptyList(),
    val searchPage: Int = 0,
    val totalPages: Int = 0,
    val totalResults: Int = 0,
    val isSearching: Boolean = false,
    val searchError: String? = null,
    val selectedItem: TmdbMediaItem? = null,
    val movieDetail: TmdbMovieDetail? = null,
    val tvDetail: TmdbTvDetail? = null,
    val imdbRating: ImdbRatingResponse? = null,
    val isLoadingDetail: Boolean = false,
    val detailError: String? = null,
    val isDownloading: Boolean = false,
    val showSeasonPicker: Boolean = false,   // TV-only: show season selector before download
    val showAutomaticOptions: Boolean = false,
    val automaticRequested: Boolean = false,
    val downloadSeason: Int? = null,
    val snackbarMessage: String? = null
)

@HiltViewModel
class MediaViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val handoff: DownloadHandoff
) : ViewModel() {
    private val _uiState = MutableStateFlow(MediaUiState())
    val uiState = _uiState.asStateFlow()
    private var searchJob: Job? = null
    private var detailJob: Job? = null
    private var trendingJob: Job? = null
    private var moreMoviesJob: Job? = null
    private var searchType = "all"

    init { loadTrending() }

    fun loadTrending() {
        trendingJob?.cancel()
        moreMoviesJob?.cancel()
        trendingJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingTrending = true, trendingError = null,
                isLoadingMoreMovies = false, moviesLoadMoreError = null)
            val tv = async { mediaRepository.getTrendingTv() }
            val movies = async { mediaRepository.getNewOnStreamingMovies(1) }
            val tvResult = tv.await()
            val movieResult = movies.await()
            val moviePage = movieResult.getOrNull()
            _uiState.value = _uiState.value.copy(
                isLoadingTrending = false,
                trendingTv = tvResult.getOrElse { _uiState.value.trendingTv },
                trendingMovies = moviePage?.results ?: _uiState.value.trendingMovies,
                moviesPage = moviePage?.page ?: _uiState.value.moviesPage,
                moviesTotalPages = moviePage?.totalPages ?: _uiState.value.moviesTotalPages,
                trendingError = tvResult.exceptionOrNull()?.message ?: movieResult.exceptionOrNull()?.message
            )
        }
    }

    fun loadMoreMovies() {
        val state = _uiState.value
        if (state.isLoadingTrending || state.isLoadingMoreMovies || state.moviesPage >= state.moviesTotalPages) return
        _uiState.value = state.copy(isLoadingMoreMovies = true, moviesLoadMoreError = null)
        moreMoviesJob = viewModelScope.launch {
            mediaRepository.getNewOnStreamingMovies(state.moviesPage + 1).onSuccess { page ->
                _uiState.value = _uiState.value.copy(isLoadingMoreMovies = false,
                    trendingMovies = (_uiState.value.trendingMovies + page.results).distinctBy { it.id },
                    moviesPage = page.page, moviesTotalPages = page.totalPages)
            }.onFailure {
                _uiState.value = _uiState.value.copy(isLoadingMoreMovies = false,
                    moviesLoadMoreError = "Could not load more: ${it.message}")
            }
        }
    }

    fun onSearchQueryChange(query: String, mediaTypeFilter: String = "all") {
        searchJob?.cancel()
        searchType = mediaTypeFilter
        _uiState.value = _uiState.value.copy(searchQuery = query, searchResults = emptyList(),
            searchPage = 0, totalPages = 0, totalResults = 0, searchError = null, isSearching = query.isNotBlank())
        if (query.isBlank()) return
        searchJob = viewModelScope.launch {
            delay(400)
            fetchPage(query, 1)
        }
    }

    private suspend fun fetchPage(query: String, page: Int) {
        val result = when (searchType) {
            "tv" -> mediaRepository.searchTv(query, page)
            "movie" -> mediaRepository.searchMovies(query, page)
            else -> mediaRepository.searchAll(query, page)
        }
        if (query != _uiState.value.searchQuery) return
        result.onSuccess { response ->
            val items = if (page == 1) response.results else _uiState.value.searchResults + response.results
            _uiState.value = _uiState.value.copy(isSearching = false,
                searchResults = items.distinctBy { it.mediaType to it.id }, searchPage = response.page,
                totalPages = response.totalPages, totalResults = response.totalResults, searchError = null)
        }.onFailure {
            _uiState.value = _uiState.value.copy(isSearching = false, searchError = it.message)
        }
    }

    fun loadMore() {
        val state = _uiState.value
        if (state.isSearching || state.searchPage >= state.totalPages) return
        _uiState.value = state.copy(isSearching = true, searchError = null)
        searchJob = viewModelScope.launch { fetchPage(state.searchQuery, state.searchPage + 1) }
    }

    fun clearSearch() = onSearchQueryChange("", searchType)

    fun openDetail(item: TmdbMediaItem) {
        detailJob?.cancel()
        _uiState.value = _uiState.value.copy(selectedItem = item, movieDetail = null,
            tvDetail = null, imdbRating = null, isLoadingDetail = true, detailError = null,
            showSeasonPicker = false, showAutomaticOptions = false, automaticRequested = false, downloadSeason = null)
        detailJob = viewModelScope.launch {
            val imdbId = if (item.mediaType == "tv") {
                val result = mediaRepository.getTvDetail(item.id)
                _uiState.value = _uiState.value.copy(tvDetail = result.getOrNull(),
                    isLoadingDetail = false, detailError = result.exceptionOrNull()?.message)
                result.getOrNull()?.externalIds?.imdbId
            } else {
                val result = mediaRepository.getMovieDetail(item.id)
                _uiState.value = _uiState.value.copy(movieDetail = result.getOrNull(),
                    isLoadingDetail = false, detailError = result.exceptionOrNull()?.message)
                result.getOrNull()?.let { it.imdbId ?: it.externalIds?.imdbId }
            }
            if (!imdbId.isNullOrBlank()) {
                mediaRepository.getImdbRating(imdbId).onSuccess {
                    _uiState.value = _uiState.value.copy(imdbRating = it)
                }
            }
        }
    }

    fun closeDetail() {
        detailJob?.cancel()
        _uiState.value = _uiState.value.copy(selectedItem = null, movieDetail = null,
            tvDetail = null, imdbRating = null, isLoadingDetail = false, detailError = null,
            showSeasonPicker = false, showAutomaticOptions = false, automaticRequested = false, downloadSeason = null)
    }

    fun downloadFromDetail(autoPick: Boolean = false) {
        val state = _uiState.value
        val item = state.selectedItem ?: return
        val isMovie = (item.mediaType ?: "movie") == "movie"

        if (isMovie) {
            if (autoPick) {
                _uiState.value = state.copy(showAutomaticOptions = true, automaticRequested = true, downloadSeason = null)
            } else {
                submitDownload(false)
            }
        } else {
            _uiState.value = state.copy(showSeasonPicker = true, automaticRequested = autoPick)
        }
    }

    fun downloadWithSeason(season: Int) {
        val state = _uiState.value
        if (state.selectedItem?.mediaType != "tv" || season < 1 ||
            season > (state.tvDetail?.numberOfSeasons ?: 0)) return
        _uiState.value = state.copy(showSeasonPicker = false, downloadSeason = season,
            showAutomaticOptions = state.automaticRequested)
        if (!state.automaticRequested) submitDownload(false)
    }

    fun dismissSeasonPicker() {
        _uiState.value = _uiState.value.copy(showSeasonPicker = false, automaticRequested = false, downloadSeason = null)
    }

    fun dismissAutomaticOptions() {
        _uiState.value = _uiState.value.copy(showAutomaticOptions = false, automaticRequested = false, downloadSeason = null)
    }

    fun confirmAutomaticDownload(policy: AutoDownloadPolicy) {
        if (!_uiState.value.showAutomaticOptions) return
        submitDownload(true, policy)
    }

    private fun submitDownload(autoPick: Boolean, policy: AutoDownloadPolicy = AutoDownloadPolicy()) {
        val state = _uiState.value
        val item = state.selectedItem ?: return
        val title = state.movieDetail?.title ?: state.tvDetail?.name ?: item.displayTitle
        val season = state.downloadSeason
        val year = if (item.mediaType == "tv") null else state.movieDetail?.year ?: item.year
        val query = if (season == null) listOfNotNull(title, year).filter { it.isNotBlank() }.joinToString(" ")
            else "$title S${season.toString().padStart(2, '0')}"
        handoff.request(DownloadRequest(query, autoPick, title, year, season, policy))
        closeDetail()
    }

    fun clearSnackbar() { _uiState.value = _uiState.value.copy(snackbarMessage = null) }
}
