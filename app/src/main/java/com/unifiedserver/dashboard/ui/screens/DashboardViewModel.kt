package com.unifiedserver.dashboard.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unifiedserver.dashboard.data.models.SystemOverview
import com.unifiedserver.dashboard.data.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = false,
    /** Why the last refresh failed; [overview] then holds the previous reading, if any. */
    val error: String? = null,
    val lastUpdated: Long? = null,
    val overview: SystemOverview? = null
)

/** Home, as on the web dashboard: one overview request per refresh. */
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
            repository.getOverview()
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false, overview = it, error = null,
                        lastUpdated = System.currentTimeMillis())
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(isLoading = false,
                        error = it.message ?: "Could not reach the server.")
                }
        }
    }

    fun refresh() = loadData()
}
