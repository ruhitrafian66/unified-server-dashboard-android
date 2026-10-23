package com.unifiedserver.dashboard.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unifiedserver.dashboard.data.models.HealthResponse
import com.unifiedserver.dashboard.data.models.SystemInfo
import com.unifiedserver.dashboard.data.models.Torrent
import com.unifiedserver.dashboard.data.models.DiskDevice
import com.unifiedserver.dashboard.data.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val lastUpdated: Long? = null,
    val health: HealthResponse? = null,
    val systemInfo: SystemInfo? = null,
    val torrents: List<Torrent> = emptyList(),
    val disks: List<DiskDevice> = emptyList()
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: DashboardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private var refreshJob: Job? = null

    fun loadData(silent: Boolean = false) {
        if (refreshJob?.isActive == true) return
        refreshJob = viewModelScope.launch {
            if (!silent) {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            }
            try {
                val healthDeferred = async { repository.getHealth() }
                val systemDeferred = async { repository.getSystemInfo() }
                val torrentsDeferred = async { repository.getTorrents() }
                val disksDeferred = async { repository.getDisks() }

                val healthResult = healthDeferred.await()
                val systemResult = systemDeferred.await()
                val torrentsResult = torrentsDeferred.await()
                val disksResult = disksDeferred.await()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    health = healthResult.getOrNull() ?: _uiState.value.health,
                    systemInfo = systemResult.getOrNull() ?: _uiState.value.systemInfo,
                    torrents = torrentsResult.getOrElse { _uiState.value.torrents },
                    disks = disksResult.getOrElse { _uiState.value.disks },
                    lastUpdated = if (listOf(healthResult, systemResult, torrentsResult, disksResult).all { it.isSuccess })
                        System.currentTimeMillis() else _uiState.value.lastUpdated,
                    error = listOf("Connection" to healthResult, "Resources" to systemResult,
                        "Downloads" to torrentsResult, "Storage" to disksResult)
                        .filter { it.second.isFailure }.joinToString { it.first }.takeIf { it.isNotBlank() }
                        ?.let { "$it unavailable. Showing previously loaded data where available." }
                )
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }

    fun refresh() = loadData()
}
