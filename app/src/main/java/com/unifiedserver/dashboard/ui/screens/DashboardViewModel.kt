package com.unifiedserver.dashboard.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unifiedserver.dashboard.data.models.HealthResponse
import com.unifiedserver.dashboard.data.models.SystemOverview
import com.unifiedserver.dashboard.data.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val lastUpdated: Long? = null,
    val health: HealthResponse? = null,
    val overview: SystemOverview? = null
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
            if (!silent) _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val health = async { repository.getHealth() }
            val overview = async { repository.getOverview() }
            val healthResult = health.await()
            val overviewResult = overview.await()
            val results = listOf("Connection" to healthResult, "Server status" to overviewResult)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                health = healthResult.getOrNull() ?: _uiState.value.health,
                overview = overviewResult.getOrNull() ?: _uiState.value.overview,
                lastUpdated = if (results.all { it.second.isSuccess }) System.currentTimeMillis()
                    else _uiState.value.lastUpdated,
                error = results.filter { it.second.isFailure }.joinToString { it.first }.takeIf { it.isNotBlank() }
                    ?.let { "$it unavailable. Showing previously loaded data where available." }
            )
        }
    }

    fun refresh() = loadData()
}
