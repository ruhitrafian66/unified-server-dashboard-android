package com.unifiedserver.dashboard.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unifiedserver.dashboard.data.PreferencesManager
import com.unifiedserver.dashboard.data.models.AutoStopConfig
import com.unifiedserver.dashboard.data.repository.ShowsRepository
import com.unifiedserver.dashboard.data.repository.TorrentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val serverUrl: String = "",
    val tmdbApiKey: String = "",
    val autoStopEnabled: Boolean = false,
    val autoStopDelayMinutes: Int = 0,
    val autoStopRatio: Float = 0f,
    val autoCheckEnabled: Boolean = false,
    val isSaving: Boolean = false,
    val snackbarMessage: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val torrentRepository: TorrentRepository,
    private val showsRepository: ShowsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val prefsFlow = combine(
        preferencesManager.serverUrl,
        preferencesManager.tmdbApiKey,
        preferencesManager.autoStopEnabled,
        preferencesManager.autoStopSeedingTime,
        preferencesManager.autoStopRatio
    ) { serverUrl, tmdbKey, autoStop, seedTime, ratio ->
        SettingsUiState(
            serverUrl = serverUrl,
            tmdbApiKey = tmdbKey,
            autoStopEnabled = autoStop,
            autoStopDelayMinutes = seedTime,
            autoStopRatio = ratio
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    init {
        viewModelScope.launch {
            prefsFlow.collect { prefs ->
                _uiState.value = prefs.copy(
                    snackbarMessage = _uiState.value.snackbarMessage,
                    isSaving = _uiState.value.isSaving
                )
            }
        }
        loadAutoStopConfig()
        loadAutoCheckConfig()
    }

    fun updateServerUrl(url: String) {
        viewModelScope.launch {
            preferencesManager.updateServerUrl(url)
            _uiState.value = _uiState.value.copy(snackbarMessage = "Server URL saved")
        }
    }

    fun saveTmdbApiKey(key: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            preferencesManager.updateTmdbApiKey(key)
            showsRepository.setTmdbApiKey(key)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        snackbarMessage = "TMDB API key saved"
                    )
                }
                .onFailure { e ->
                    // Key saved locally even if server-side validation fails
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        snackbarMessage = "Saved locally. Server: ${e.message}"
                    )
                }
        }
    }

    fun saveAutoStopConfig(enabled: Boolean, delayMinutes: Int, ratio: Float) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            preferencesManager.updateAutoStopEnabled(enabled)
            preferencesManager.updateAutoStopSeedingTime(delayMinutes)
            preferencesManager.updateAutoStopRatio(ratio)

            val config = AutoStopConfig(
                enabled = enabled,
                seedingTimeLimit = delayMinutes,
                ratioLimit = if (ratio > 0) ratio else null
            )
            torrentRepository.setAutoStopConfig(config)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        snackbarMessage = "Auto-stop config saved"
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        snackbarMessage = "Error: ${e.message}"
                    )
                }
        }
    }

    private fun loadAutoStopConfig() {
        viewModelScope.launch {
            torrentRepository.getAutoStopConfig()
                .onSuccess { config ->
                    preferencesManager.updateAutoStopEnabled(config.enabled)
                    preferencesManager.updateAutoStopSeedingTime(config.seedingTimeLimit)
                    preferencesManager.updateAutoStopRatio(config.ratioLimit ?: 0f)
                }
        }
    }

    private fun loadAutoCheckConfig() {
        viewModelScope.launch {
            showsRepository.getAutoCheckConfig()
                .onSuccess { config ->
                    _uiState.value = _uiState.value.copy(autoCheckEnabled = config.enabled)
                }
        }
    }

    fun saveAutoCheckEnabled(enabled: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            showsRepository.setAutoCheckEnabled(enabled)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        autoCheckEnabled = enabled,
                        snackbarMessage = if (enabled) "Auto-check enabled" else "Auto-check disabled"
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        snackbarMessage = "Error: ${e.message}"
                    )
                }
        }
    }

    fun clearSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }
}
