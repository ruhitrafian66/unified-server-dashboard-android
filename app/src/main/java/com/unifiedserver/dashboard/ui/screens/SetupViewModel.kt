package com.unifiedserver.dashboard.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unifiedserver.dashboard.data.PreferencesManager
import com.unifiedserver.dashboard.data.repository.DashboardRepository
import com.unifiedserver.dashboard.data.repository.normalizeServerUrl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SetupUiState(
    val serverUrl: String = "",
    val isTesting: Boolean = false,
    val isConfigured: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SetupUiState())
    val uiState: StateFlow<SetupUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val url = preferencesManager.serverUrl.first()
            _uiState.value = _uiState.value.copy(serverUrl = url)
        }
    }

    fun testAndSave(url: String) {
        if (_uiState.value.isTesting) return
        val normalized = try {
            normalizeServerUrl(url)
        } catch (e: IllegalArgumentException) {
            _uiState.value = _uiState.value.copy(error = e.message)
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isTesting = true, error = null)
            dashboardRepository.testServer(normalized)
                .onSuccess {
                    preferencesManager.configureServer(normalized)
                    _uiState.value = _uiState.value.copy(
                        isTesting = false,
                        isConfigured = true,
                        serverUrl = normalized
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isTesting = false,
                        error = e.message ?: "Could not connect to server"
                    )
                }
        }
    }
}
