package com.unifiedserver.dashboard.data.repository

import com.unifiedserver.dashboard.data.PreferencesManager
import com.unifiedserver.dashboard.data.models.*
import javax.inject.Inject

open class SystemRepository @Inject constructor(
    private val preferencesManager: PreferencesManager
) {

    /** Everything the Server screen shows except metric history, in one request. */
    open suspend fun getOverview(): Result<SystemOverview> = apiResult {
        buildApiService(preferencesManager).getOverview().bodyOr { SystemOverview() }
    }

    suspend fun reboot(): Result<Unit> = apiResult {
        buildApiService(preferencesManager).powerAction("reboot").requireSuccess()
    }

    suspend fun shutdown(): Result<Unit> = apiResult {
        buildApiService(preferencesManager).powerAction("shutdown").requireSuccess()
    }

    /** Up to [hours] of one-per-minute samples, oldest first (the server keeps 4 days). */
    open suspend fun getHistory(metric: HistoryMetric, hours: Int): Result<List<HistorySample>> = apiResult {
        buildApiService(preferencesManager).getHistory(metric.path, hours).bodyOr { HistoryResponse() }.samples
    }
}

/** Metrics the server records history for; [path] is the `{path}-history` endpoint. */
enum class HistoryMetric(val path: String) { CPU("cpu"), TEMPERATURE("temp") }
