package com.unifiedserver.dashboard.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unifiedserver.dashboard.data.models.*
import com.unifiedserver.dashboard.data.repository.MediaRepository
import com.unifiedserver.dashboard.data.repository.DownloadHandoff
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
    val trendingMovies: List<TmdbMediaItem> = emptyList(),
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
    private var searchType = "all"

    init { loadTrending() }

    fun loadTrending() {
        trendingJob?.cancel()
        trendingJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingTrending = true, trendingError = null)
            val tv = async { mediaRepository.getTrendingTv() }
            val movies = async { mediaRepository.getTrendingMovies() }
            val tvResult = tv.await()
            val movieResult = movies.await()
            _uiState.value = _uiState.value.copy(
                isLoadingTrending = false,
                trendingTv = tvResult.getOrElse { _uiState.value.trendingTv },
                trendingMovies = movieResult.getOrElse { _uiState.value.trendingMovies },
                trendingError = tvResult.exceptionOrNull()?.message ?: movieResult.exceptionOrNull()?.message
            )
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
            tvDetail = null, imdbRating = null, isLoadingDetail = true, detailError = null)
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
            tvDetail = null, imdbRating = null, isLoadingDetail = false, detailError = null)
    }

    fun downloadFromDetail() {
        val state = _uiState.value
        val item = state.selectedItem ?: return
        val isMovie = (item.mediaType ?: "movie") == "movie"

        if (isMovie) {
            // Movies: auto-pick best 1080p+ source
            val title = state.movieDetail?.title ?: item.displayTitle
            val year = state.movieDetail?.year ?: item.year
            handoff.request(listOf(title, year).filter { it.isNotBlank() }.joinToString(" "), autoPick = true)
            closeDetail()
        } else {
            // TV shows: show season picker first
            _uiState.value = _uiState.value.copy(showSeasonPicker = true)
        }
    }

    fun downloadWithSeason(season: Int) {
        val state = _uiState.value
        val item = state.selectedItem ?: return
        val title = state.tvDetail?.name ?: item.displayTitle
        val year = state.tvDetail?.year ?: item.year
        val seasonStr = "S%02d".format(season)   // e.g. S01, S02
        // Query: "Breaking Bad S02 2008" or just "Breaking Bad S02"
        val query = listOf(title, seasonStr, year).filter { it.isNotBlank() }.joinToString(" ")
        _uiState.value = _uiState.value.copy(showSeasonPicker = false)
        handoff.request(query, autoPick = true)
        closeDetail()
    }

    fun dismissSeasonPicker() {
        _uiState.value = _uiState.value.copy(showSeasonPicker = false)
    }

    fun clearSnackbar() { _uiState.value = _uiState.value.copy(snackbarMessage = null) }
}
