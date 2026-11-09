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

/** One history card: its selected range and the samples last loaded for it (null until loaded). */
data class MetricHistory(
    val range: HistoryRange = HistoryRange.H6,
    val samples: List<HistorySample>? = null,
    val loading: Boolean = false
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
            val failed = buildList {
                if (overviewResult.isFailure) add("Server status")
                if (historyResults[HistoryMetric.CPU]?.second?.isFailure == true) add("CPU history")
                if (overview?.temperature != null && historyResults[HistoryMetric.TEMPERATURE]?.second?.isFailure == true)
                    add("Temperature history")
            }
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                overview = overview,
                histories = historyResults.entries.fold(_uiState.value.histories) { acc, (metric, result) ->
                    acc.withSamples(metric, result.first, result.second.getOrNull())
                },
                lastUpdated = if (failed.isEmpty()) System.currentTimeMillis() else _uiState.value.lastUpdated,
                error = failed.takeIf { it.isNotEmpty() }?.joinToString(postfix = " unavailable. Previous data retained.")
            )
        }
    }

    /** Switches a card's range and loads it right away. */
    fun selectRange(metric: HistoryMetric, range: HistoryRange) {
        val histories = _uiState.value.histories
        if (histories.getValue(metric).range == range) return
        _uiState.value = _uiState.value.copy(histories = histories + (metric to MetricHistory(range, loading = true)))
        viewModelScope.launch {
            val samples = repository.getHistory(metric, range.hours).getOrNull()
            _uiState.value = _uiState.value.copy(histories = _uiState.value.histories.withSamples(metric, range, samples ?: emptyList()))
        }
    }

    /** Applies [samples] unless the user picked another range while they loaded; null keeps the old data. */
    private fun Map<HistoryMetric, MetricHistory>.withSamples(
        metric: HistoryMetric, range: HistoryRange, samples: List<HistorySample>?
    ): Map<HistoryMetric, MetricHistory> {
        val current = getValue(metric)
        if (current.range != range) return this
        return this + (metric to current.copy(samples = samples ?: current.samples, loading = false))
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
