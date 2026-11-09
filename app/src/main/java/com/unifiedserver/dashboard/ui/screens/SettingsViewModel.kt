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
    /** Whether the server has a TMDB key; null until known. */
    val tmdbConfigured: Boolean? = null,
    val omdbApiKey: String = "",
    val autoStopEnabled: Boolean = false,
    /** Seed for this many minutes after completion; null = no time limit. */
    val autoStopMinutes: Int? = null,
    /** Or until this upload ratio; null = no ratio limit. */
    val autoStopRatio: Float? = null,
    val autoCheckEnabled: Boolean = false,
    val isLoading: Boolean = true,
    val configurationError: String? = null,
    val autoStopReady: Boolean = false,
    val autoCheckReady: Boolean = false,
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
    private val dashboardRepository: DashboardRepository,
    private val automation: AutomationSettingsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(preferences.serverUrl, preferences.omdbApiKey) { url, omdb -> url to omdb }
                .collect { (url, omdb) ->
                    // Never replace unrelated server-confirmed settings with defaults.
                    _uiState.value = _uiState.value.copy(serverUrl = url, omdbApiKey = omdb)
                }
        }
        viewModelScope.launch {
            automation.state.collect { settings ->
                _uiState.value = _uiState.value.copy(isLoading = settings.refreshing,
                    autoStopEnabled = settings.autoStop?.enabled ?: false,
                    autoStopMinutes = settings.autoStop?.seedTimeLimit,
                    autoStopRatio = settings.autoStop?.ratioLimit,
                    autoCheckEnabled = settings.autoCheck ?: false,
                    autoStopReady = settings.autoStop != null && settings.autoStopError == null,
                    autoCheckReady = settings.autoCheck != null && settings.autoCheckError == null,
                    configurationError = listOfNotNull(
                        settings.autoStopError?.let { "Auto-stop: $it" },
                        settings.autoCheckError?.let { "Auto-check: $it" }
                    ).joinToString("\n").takeIf { it.isNotBlank() })
            }
        }
        refreshConfiguration()
    }

    fun refreshConfiguration() {
        viewModelScope.launch { automation.refresh() }
        viewModelScope.launch {
            showsRepository.getTmdbConfig().onSuccess {
                _uiState.value = _uiState.value.copy(tmdbConfigured = it.configured)
            }
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
        automation.invalidate()
    }

    /** The server verifies the key with TMDB before storing it; every device then uses it. */
    fun saveTmdbApiKey(key: String, onSaved: () -> Unit = {}) =
        save("tmdb", "TMDB key verified and saved on the server") {
            require(key.isNotBlank()) { "Enter a TMDB API key." }
            showsRepository.setTmdbApiKey(key).getOrThrow()
            _uiState.value = _uiState.value.copy(tmdbConfigured = true)
            onSaved()
        }

    fun saveOmdbApiKey(key: String) = save("omdb", "Optional OMDb key saved on this device") {
        preferences.updateOmdbApiKey(key.trim())
    }

    fun saveAutoStopConfig(enabled: Boolean, minutesText: String, ratioText: String) =
        save("autoStop", if (enabled) "Seeding limits saved in qBittorrent" else "Seeding limits disabled") {
            val (minutes, ratio) = parseAutoStopLimits(minutesText, ratioText)
            automation.saveAutoStop(AutoStopConfig(enabled, minutes, ratio)).getOrThrow()
        }

    fun saveAutoCheckEnabled(enabled: Boolean) = save("autoCheck", "Auto-check setting saved") {
        automation.saveAutoCheck(enabled).getOrThrow()
    }

    fun clearSnackbar() { _uiState.value = _uiState.value.copy(snackbarMessage = null) }
}

/**
 * Both limits are optional; blank means "no limit of that kind". With both blank,
 * qBittorrent stops seeding as soon as a download completes.
 */
internal fun parseAutoStopLimits(minutesText: String, ratioText: String): Pair<Int?, Float?> {
    val minutes = minutesText.trim().takeIf { it.isNotEmpty() }?.let { text ->
        text.toIntOrNull()?.takeIf { it >= 0 } ?: throw IllegalArgumentException("Enter a non-negative whole number of minutes, or leave it empty.")
    }
    val ratio = ratioText.trim().replace(',', '.').takeIf { it.isNotEmpty() }?.let { text ->
        text.toFloatOrNull()?.takeIf { it.isFinite() && it >= 0f }
            ?: throw IllegalArgumentException("Enter a non-negative ratio, or leave it empty.")
    }
    return minutes to ratio
}

internal fun formatRatio(ratio: Float?): String =
    ratio?.let { if (it % 1f == 0f) "%.1f".format(java.util.Locale.ROOT, it) else it.toString() } ?: ""
