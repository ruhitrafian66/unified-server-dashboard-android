package com.unifiedserver.dashboard.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unifiedserver.dashboard.data.models.*
import com.unifiedserver.dashboard.data.repository.HistoryMetric
import com.unifiedserver.dashboard.data.repository.SystemRepository
import com.unifiedserver.dashboard.ui.components.HistoryRange
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
    val histories: Map<HistoryMetric, MetricHistory> = HistoryMetric.entries.associateWith { MetricHistory() },
    val snackbarMessage: String? = null
)

/**
 * One history card: its selected range and the samples last loaded for it (null until
 * loaded). [error] is why the latest load failed; older [samples] stay on screen with it.
 */
data class MetricHistory(
    val range: HistoryRange = HistoryRange.H6,
    val samples: List<HistorySample>? = null,
    val loading: Boolean = false,
    val error: String? = null
)

/** The server adds one history point per minute, so polling histories faster only costs data. */
internal const val HISTORY_REFRESH_MS = 60_000L

@HiltViewModel
class SystemViewModel @Inject constructor(
    private val repository: SystemRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SystemUiState())
    val uiState: StateFlow<SystemUiState> = _uiState.asStateFlow()

    private var refreshJob: Job? = null

    private var historiesLoadedAt = 0L

    fun loadAll(silent: Boolean = false) {
        if (refreshJob?.isActive == true) return
        refreshJob = viewModelScope.launch {
            if (!silent) {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            }
            val withHistories = !silent || System.currentTimeMillis() - historiesLoadedAt >= HISTORY_REFRESH_MS
            val overviewDeferred = async { repository.getOverview() }
            val historyDeferred = if (!withHistories) emptyMap() else HistoryMetric.entries.associateWith { metric ->
                val range = _uiState.value.histories.getValue(metric).range
                async { range to repository.getHistory(metric, range.hours) }
            }
            val overviewResult = overviewDeferred.await()
            val historyResults = historyDeferred.mapValues { it.value.await() }
            if (historyResults.values.all { it.second.isSuccess }) historiesLoadedAt = System.currentTimeMillis()

            val overview = overviewResult.getOrNull() ?: _uiState.value.overview
            // Temperature history only matters on boards that report a temperature.
            val failed = buildList<Pair<String, Result<*>>> {
                if (overviewResult.isFailure) add("Server status" to overviewResult)
                historyResults[HistoryMetric.CPU]?.second?.takeIf { it.isFailure }?.let { add("CPU history" to it) }
                if (overview?.temperature != null)
                    historyResults[HistoryMetric.TEMPERATURE]?.second?.takeIf { it.isFailure }?.let { add("Temperature history" to it) }
            }
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                overview = overview,
                histories = historyResults.entries.fold(_uiState.value.histories) { acc, (metric, result) ->
                    acc.withResult(metric, result.first, result.second)
                },
                lastUpdated = if (failed.isEmpty()) System.currentTimeMillis() else _uiState.value.lastUpdated,
                error = failed.takeIf { it.isNotEmpty() }?.let { failures ->
                    // One cause is enough: when several parts fail together it is the same one.
                    val cause = failures.firstNotNullOfOrNull { it.second.exceptionOrNull()?.message }
                    failures.joinToString { it.first } + " unavailable" + (cause?.let { " ($it)" } ?: "") +
                        ". Previous data retained."
                }
            )
        }
    }

    /** Switches a card's range and loads it right away. */
    fun selectRange(metric: HistoryMetric, range: HistoryRange) {
        val histories = _uiState.value.histories
        if (histories.getValue(metric).range == range) return
        _uiState.value = _uiState.value.copy(histories = histories + (metric to MetricHistory(range, loading = true)))
        viewModelScope.launch {
            val result = repository.getHistory(metric, range.hours)
            _uiState.value = _uiState.value.copy(histories = _uiState.value.histories.withResult(metric, range, result))
        }
    }

    /**
     * Applies a load's outcome unless the user picked another range while it ran. A
     * failure keeps the samples already shown and records why.
     */
    private fun Map<HistoryMetric, MetricHistory>.withResult(
        metric: HistoryMetric, range: HistoryRange, result: Result<List<HistorySample>>
    ): Map<HistoryMetric, MetricHistory> {
        val current = getValue(metric)
        if (current.range != range) return this
        return this + (metric to current.copy(
            samples = result.getOrNull() ?: current.samples,
            loading = false,
            error = result.exceptionOrNull()?.let { it.message ?: "Request failed." }))
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
