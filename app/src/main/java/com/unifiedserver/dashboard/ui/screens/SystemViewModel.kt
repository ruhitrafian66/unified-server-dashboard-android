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
import kotlinx.coroutines.Job
import javax.inject.Inject

data class SystemUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val lastUpdated: Long? = null,
    val powerPending: Boolean = false,
    val systemInfo: SystemInfo? = null,
    val services: List<ServiceStatus> = emptyList(),
    val disks: List<DiskDevice> = emptyList(),
    val dockerContainers: List<DockerContainer> = emptyList(),
    val cpuMetrics: CpuMetrics? = null,
    val snackbarMessage: String? = null
)

@HiltViewModel
class SystemViewModel @Inject constructor(
    private val repository: SystemRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SystemUiState())
    val uiState: StateFlow<SystemUiState> = _uiState.asStateFlow()

    private var refreshJob: Job? = null

    fun loadAll(silent: Boolean = false) {
        if (refreshJob?.isActive == true) return
        refreshJob = viewModelScope.launch {
            if (!silent) {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            }

            val systemDeferred = async { repository.getSystemInfo() }
            val servicesDeferred = async { repository.getServices() }
            val disksDeferred = async { repository.getDisks() }
            val dockerDeferred = async { repository.getDockerContainers() }
            val cpuDeferred = async { repository.getCpuMetrics(hours = 1) }

            val systemResult = systemDeferred.await()
            val servicesResult = servicesDeferred.await()
            val disksResult = disksDeferred.await()
            val dockerResult = dockerDeferred.await()
            val cpuResult = cpuDeferred.await()

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                systemInfo = systemResult.getOrNull() ?: _uiState.value.systemInfo,
                services = servicesResult.getOrElse { _uiState.value.services },
                disks = disksResult.getOrElse { _uiState.value.disks },
                dockerContainers = dockerResult.getOrElse { _uiState.value.dockerContainers },
                cpuMetrics = cpuResult.getOrNull() ?: _uiState.value.cpuMetrics,
                lastUpdated = if (listOf(systemResult, servicesResult, disksResult, dockerResult, cpuResult).all { it.isSuccess })
                    System.currentTimeMillis() else _uiState.value.lastUpdated,
                error = listOf("Resources" to systemResult, "Services" to servicesResult, "Storage" to disksResult,
                    "Containers" to dockerResult, "CPU history" to cpuResult).filter { it.second.isFailure }
                    .joinToString { it.first }.takeIf { it.isNotBlank() }?.let { "$it unavailable. Previous data retained." }
            )
        }
    }

    fun reboot() {
        if (_uiState.value.powerPending) return
        _uiState.value = _uiState.value.copy(powerPending = true)
        viewModelScope.launch {
            repository.reboot()
                .onSuccess {
                    _uiState.value = _uiState.value.copy(snackbarMessage = "Reboot command sent")
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(snackbarMessage = "Error: ${e.message}")
                }
            _uiState.value = _uiState.value.copy(powerPending = false)
        }
    }

    fun shutdown() {
        if (_uiState.value.powerPending) return
        _uiState.value = _uiState.value.copy(powerPending = true)
        viewModelScope.launch {
            repository.shutdown()
                .onSuccess {
                    _uiState.value = _uiState.value.copy(snackbarMessage = "Shutdown command sent")
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(snackbarMessage = "Error: ${e.message}")
                }
            _uiState.value = _uiState.value.copy(powerPending = false)
        }
    }

    fun clearSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }

    fun refresh() = loadAll()
}
