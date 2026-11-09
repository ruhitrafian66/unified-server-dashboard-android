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
    val overview: SystemOverview? = null,
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

            val overviewDeferred = async { repository.getOverview() }
            val cpuDeferred = async { repository.getCpuMetrics(hours = 1) }
            val overviewResult = overviewDeferred.await()
            val cpuResult = cpuDeferred.await()

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                overview = overviewResult.getOrNull() ?: _uiState.value.overview,
                cpuMetrics = cpuResult.getOrNull() ?: _uiState.value.cpuMetrics,
                lastUpdated = if (overviewResult.isSuccess && cpuResult.isSuccess)
                    System.currentTimeMillis() else _uiState.value.lastUpdated,
                error = listOf("Server status" to overviewResult, "CPU history" to cpuResult)
                    .filter { it.second.isFailure }
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
