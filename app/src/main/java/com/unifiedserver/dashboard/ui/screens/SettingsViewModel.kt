package com.unifiedserver.dashboard.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unifiedserver.dashboard.data.PreferencesManager
import com.unifiedserver.dashboard.data.models.AutoStopConfig
import com.unifiedserver.dashboard.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val serverUrl: String = "",
    val tmdbApiKey: String = "",
    val omdbApiKey: String = "",
    val autoStopEnabled: Boolean = false,
    val autoStopDelayMinutes: Int = 0,
    val autoStopRatio: Float = 0f,
    val autoCheckEnabled: Boolean = false,
    val isLoading: Boolean = true,
    val configurationError: String? = null,
    val savingSection: String? = null,
    val snackbarMessage: String? = null
) {
    val isSaving: Boolean get() = savingSection != null
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferences: PreferencesManager,
    private val torrentRepository: TorrentRepository,
    private val showsRepository: ShowsRepository,
    private val dashboardRepository: DashboardRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(preferences.serverUrl, preferences.tmdbApiKey, preferences.omdbApiKey) { url, tmdb, omdb ->
                Triple(url, tmdb, omdb)
            }.collect { (url, tmdb, omdb) ->
                // Never replace unrelated server-confirmed settings with defaults.
                _uiState.value = _uiState.value.copy(serverUrl = url, tmdbApiKey = tmdb, omdbApiKey = omdb)
            }
        }
        viewModelScope.launch {
            val stop = torrentRepository.getAutoStopConfig()
            val check = showsRepository.getAutoCheckConfig()
            _uiState.value = _uiState.value.copy(isLoading = false,
                autoStopEnabled = stop.getOrNull()?.enabled ?: false,
                autoStopDelayMinutes = stop.getOrNull()?.seedingTimeLimit ?: 0,
                autoStopRatio = stop.getOrNull()?.ratioLimit ?: 0f,
                autoCheckEnabled = check.getOrNull()?.enabled ?: false,
                configurationError = if (stop.isFailure || check.isFailure)
                    "Some server settings could not be loaded. Reopen Settings to retry before editing automation." else null)
        }
    }

    private fun save(section: String, message: String, action: suspend () -> Unit) {
        if (_uiState.value.isSaving) return
        _uiState.value = _uiState.value.copy(savingSection = section)
        viewModelScope.launch {
            apiResult { action() }.onSuccess {
                _uiState.value = _uiState.value.copy(snackbarMessage = message)
            }.onFailure {
                _uiState.value = _uiState.value.copy(snackbarMessage = it.message ?: "Could not save. Try again.")
            }
            _uiState.value = _uiState.value.copy(savingSection = null)
        }
    }

    fun updateServerUrl(url: String) = save("server", "Connection verified and server saved") {
        val normalized = normalizeServerUrl(url)
        dashboardRepository.testServer(normalized).getOrThrow()
        preferences.configureServer(normalized)
    }

    fun saveTmdbApiKey(key: String) = save("tmdb", "TMDB key saved on server and device") {
        require(key.isNotBlank()) { "Enter a TMDB API key." }
        showsRepository.setTmdbApiKey(key).getOrThrow()
        preferences.updateTmdbApiKey(key)
    }

    fun saveOmdbApiKey(key: String) = save("omdb", "Optional OMDb key saved on this device") {
        preferences.updateOmdbApiKey(key.trim())
    }

    fun saveAutoStopConfig(enabled: Boolean, delayText: String, ratioText: String) =
        save("autoStop", "Auto-stop settings saved") {
            val (minutes, ratio) = parseAutoStopLimits(delayText, ratioText)
            torrentRepository.setAutoStopConfig(AutoStopConfig(enabled, minutes, ratio.takeIf { it > 0 })).getOrThrow()
            preferences.updateAutoStopConfig(enabled, minutes, ratio)
            _uiState.value = _uiState.value.copy(autoStopEnabled = enabled,
                autoStopDelayMinutes = minutes, autoStopRatio = ratio)
        }

    fun saveAutoCheckEnabled(enabled: Boolean) = save("autoCheck", "Auto-check setting saved") {
        showsRepository.setAutoCheckEnabled(enabled).getOrThrow()
        _uiState.value = _uiState.value.copy(autoCheckEnabled = enabled)
    }

    fun clearSnackbar() { _uiState.value = _uiState.value.copy(snackbarMessage = null) }
}

internal fun parseAutoStopLimits(delayText: String, ratioText: String): Pair<Int, Float> {
    val minutes = delayText.trim().toIntOrNull()
    require(minutes != null && minutes >= 0) { "Enter a non-negative whole number of minutes." }
    val ratio = ratioText.trim().replace(',', '.').toFloatOrNull()
    require(ratio != null && ratio.isFinite() && ratio >= 0f) { "Enter a non-negative ratio; use 0 for no ratio limit." }
    return minutes to ratio
}
