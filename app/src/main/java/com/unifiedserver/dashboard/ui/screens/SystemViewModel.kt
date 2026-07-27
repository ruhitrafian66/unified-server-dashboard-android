package com.unifiedserver.dashboard.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unifiedserver.dashboard.data.models.*
import com.unifiedserver.dashboard.data.repository.SystemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SystemUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val systemInfo: SystemInfo? = null,
    val services: List<ServiceStatus> = emptyList(),
    val disks: List<DiskDevice> = emptyList(),
    val dockerContainers: List<DockerContainer> = emptyList(),
    val snackbarMessage: String? = null
)

@HiltViewModel
class SystemViewModel @Inject constructor(
    private val repository: SystemRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SystemUiState())
    val uiState: StateFlow<SystemUiState> = _uiState.asStateFlow()

    init {
        loadAll()
        startAutoRefresh()
    }

    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(15_000) // Refresh every 15 seconds
                if (!_uiState.value.isLoading) {
                    loadAll(silent = true)
                }
            }
        }
    }

    fun loadAll(silent: Boolean = false) {
        viewModelScope.launch {
            if (!silent) {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            }

            val systemDeferred = async { repository.getSystemInfo() }
            val servicesDeferred = async { repository.getServices() }
            val disksDeferred = async { repository.getDisks() }
            val dockerDeferred = async { repository.getDockerContainers() }

            val systemResult = systemDeferred.await()
            val servicesResult = servicesDeferred.await()
            val disksResult = disksDeferred.await()
            val dockerResult = dockerDeferred.await()

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                systemInfo = systemResult.getOrNull(),
                services = servicesResult.getOrNull() ?: emptyList(),
                disks = disksResult.getOrNull() ?: emptyList(),
                dockerContainers = dockerResult.getOrNull() ?: emptyList(),
                error = systemResult.exceptionOrNull()?.message
            )
        }
    }

    fun reboot() {
        viewModelScope.launch {
            repository.reboot()
                .onSuccess {
                    _uiState.value = _uiState.value.copy(snackbarMessage = "Reboot command sent")
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(snackbarMessage = "Error: ${e.message}")
                }
        }
    }

    fun shutdown() {
        viewModelScope.launch {
            repository.shutdown()
                .onSuccess {
                    _uiState.value = _uiState.value.copy(snackbarMessage = "Shutdown command sent")
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(snackbarMessage = "Error: ${e.message}")
                }
        }
    }

    fun clearSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }

    fun refresh() = loadAll()
}
