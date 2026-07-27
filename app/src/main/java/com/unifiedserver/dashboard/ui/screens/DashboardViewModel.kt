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
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
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

    init {
        loadData()
        startAutoRefresh()
    }

    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(10_000)
                if (!_uiState.value.isLoading) {
                    loadData(silent = true)
                }
            }
        }
    }

    fun loadData(silent: Boolean = false) {
        viewModelScope.launch {
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
                    health = healthResult.getOrNull(),
                    systemInfo = systemResult.getOrNull(),
                    torrents = torrentsResult.getOrNull() ?: emptyList(),
                    disks = disksResult.getOrNull() ?: emptyList(),
                    error = healthResult.exceptionOrNull()?.message
                )
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
