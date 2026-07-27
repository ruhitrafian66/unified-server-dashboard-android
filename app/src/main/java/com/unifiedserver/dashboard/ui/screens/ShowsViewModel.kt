package com.unifiedserver.dashboard.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unifiedserver.dashboard.data.models.*
import com.unifiedserver.dashboard.data.repository.ShowsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ShowsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val shows: List<Show> = emptyList(),
    val snackbarMessage: String? = null,
    val isCheckingEpisodes: Boolean = false,
    // TMDB search
    val isSearchingTmdb: Boolean = false,
    val tmdbSearchResults: List<TmdbSearchResult> = emptyList(),
    val tmdbSearchError: String? = null,
    val tmdbConfigured: Boolean = false,
    // Queue
    val queueStatus: QueueStatusResponse? = null,
    val autoCheckEnabled: Boolean = false,
    // Season download (past shows)
    val isDownloadingSeason: Boolean = false,
    val downloadProgress: DownloadProgressResponse? = null,
    val downloadSessionId: String? = null
)

@HiltViewModel
class ShowsViewModel @Inject constructor(
    private val repository: ShowsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShowsUiState())
    val uiState: StateFlow<ShowsUiState> = _uiState.asStateFlow()

    init {
        loadShows()
        checkTmdbConfig()
        loadQueueStatus()
        loadAutoCheckConfig()
        startAutoCheckPolling()
    }

    private fun startAutoCheckPolling() {
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(45_000) // Poll every 45 seconds
                if (_uiState.value.autoCheckEnabled) {
                    loadQueueStatus()
                }
            }
        }
    }

    fun loadQueueStatus() {
        viewModelScope.launch {
            repository.getQueueStatus()
                .onSuccess { status ->
                    _uiState.value = _uiState.value.copy(queueStatus = status)
                }
        }
    }

    fun clearQueue() {
        viewModelScope.launch {
            repository.clearQueue()
                .onSuccess { response ->
                    _uiState.value = _uiState.value.copy(
                        snackbarMessage = response.message ?: "Queue cleared (${response.clearedCount} items)"
                    )
                    loadQueueStatus()
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(snackbarMessage = "Error: ${e.message}")
                }
        }
    }

    private fun loadAutoCheckConfig() {
        viewModelScope.launch {
            repository.getAutoCheckConfig()
                .onSuccess { config ->
                    _uiState.value = _uiState.value.copy(autoCheckEnabled = config.enabled)
                }
        }
    }

    fun setAutoCheckEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setAutoCheckEnabled(enabled)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        autoCheckEnabled = enabled,
                        snackbarMessage = if (enabled) "Auto-check enabled" else "Auto-check disabled"
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(snackbarMessage = "Error: ${e.message}")
                }
        }
    }

    fun loadShows() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            repository.getShows()
                .onSuccess { shows ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        shows = shows
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load shows"
                    )
                }
        }
    }

    fun addShow(name: String, tmdbId: Int? = null, season: Int = 1, episode: Int = 0) {
        viewModelScope.launch {
            repository.addShow(
                AddShowRequest(
                    tmdbId = tmdbId,
                    name = name,
                    currentSeason = season,
                    currentEpisode = episode
                )
            )
                .onSuccess { response ->
                    val message = response.message ?: "Show added: $name"
                    _uiState.value = _uiState.value.copy(snackbarMessage = message)
                    loadShows()
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        snackbarMessage = "Error: ${e.message}"
                    )
                }
        }
    }

    fun toggleShowStatus(show: Show) {
        val newStatus = if (show.status.equals("active", ignoreCase = true)) "paused" else "active"
        viewModelScope.launch {
            repository.updateShow(show.id, UpdateShowRequest(status = newStatus))
                .onSuccess { loadShows() }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        snackbarMessage = "Error: ${e.message}"
                    )
                }
        }
    }

    fun updateProgress(showId: String, season: Int, episode: Int) {
        viewModelScope.launch {
            repository.updateShow(showId, UpdateShowRequest(currentSeason = season, currentEpisode = episode))
                .onSuccess { loadShows() }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(snackbarMessage = "Error: ${e.message}")
                }
        }
    }

    fun deleteShow(id: String) {
        viewModelScope.launch {
            repository.deleteShow(id)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(snackbarMessage = "Show deleted")
                    loadShows()
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(snackbarMessage = "Error: ${e.message}")
                }
        }
    }

    fun checkEpisodes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCheckingEpisodes = true)
            repository.checkEpisodes()
                .onSuccess { result ->
                    val msg = result.message.ifBlank {
                        "Checked shows, found ${result.downloads.size} episode(s)"
                    }
                    _uiState.value = _uiState.value.copy(
                        isCheckingEpisodes = false,
                        snackbarMessage = msg
                    )
                    loadShows()
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isCheckingEpisodes = false,
                        snackbarMessage = "Error: ${e.message}"
                    )
                }
        }
    }

    fun downloadSeason(show: Show) {
        viewModelScope.launch {
            repository.downloadSeason(show.tmdbId, show.currentSeason, show.name)
                .onSuccess { response ->
                    val msg = response.message
                        ?: "Downloading Season ${show.currentSeason} of ${show.name}"
                    _uiState.value = _uiState.value.copy(snackbarMessage = msg)
                    response.sessionId?.let { pollDownloadProgress(it) }
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(snackbarMessage = "Error: ${e.message}")
                }
        }
    }

    fun downloadSeasonPack(tmdbId: Int, showName: String, season: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isDownloadingSeason = true,
                downloadProgress = null,
                downloadSessionId = null
            )
            repository.downloadSeason(tmdbId, season, showName)
                .onSuccess { response ->
                    if (response.sessionId != null) {
                        pollDownloadProgress(response.sessionId)
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isDownloadingSeason = false,
                            snackbarMessage = response.message ?: "Download started"
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isDownloadingSeason = false,
                        snackbarMessage = "Error: ${e.message}"
                    )
                }
        }
    }

    private fun pollDownloadProgress(sessionId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isDownloadingSeason = true,
                downloadSessionId = sessionId
            )
            var complete = false
            while (!complete) {
                repository.getDownloadProgress(sessionId)
                    .onSuccess { progress ->
                        _uiState.value = _uiState.value.copy(downloadProgress = progress)
                        complete = progress.success || progress.error ||
                            progress.status.equals("complete", ignoreCase = true) ||
                            progress.status.equals("not_found", ignoreCase = true)
                    }
                    .onFailure { complete = true }
                if (!complete) kotlinx.coroutines.delay(2000)
            }
            val finalProgress = _uiState.value.downloadProgress
            _uiState.value = _uiState.value.copy(
                isDownloadingSeason = false,
                snackbarMessage = when {
                    finalProgress?.success == true -> "Season download complete"
                    finalProgress?.error == true -> finalProgress.message ?: "Download failed"
                    else -> finalProgress?.message ?: "Download finished"
                }
            )
        }
    }

    fun clearDownloadProgress() {
        _uiState.value = _uiState.value.copy(
            downloadProgress = null,
            downloadSessionId = null,
            isDownloadingSeason = false
        )
    }

    fun searchTmdb(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSearchingTmdb = true,
                tmdbSearchResults = emptyList(),
                tmdbSearchError = null
            )
            repository.searchShows(query)
                .onSuccess { results ->
                    _uiState.value = _uiState.value.copy(
                        isSearchingTmdb = false,
                        tmdbSearchResults = results
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isSearchingTmdb = false,
                        tmdbSearchError = e.message
                    )
                }
        }
    }

    fun clearTmdbSearch() {
        _uiState.value = _uiState.value.copy(
            tmdbSearchResults = emptyList(),
            tmdbSearchError = null
        )
    }

    private fun checkTmdbConfig() {
        viewModelScope.launch {
            repository.getTmdbConfig()
                .onSuccess { config ->
                    _uiState.value = _uiState.value.copy(tmdbConfigured = config.configured)
                }
        }
    }

    fun clearSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }

    fun refresh() {
        loadShows()
        loadQueueStatus()
    }
}
