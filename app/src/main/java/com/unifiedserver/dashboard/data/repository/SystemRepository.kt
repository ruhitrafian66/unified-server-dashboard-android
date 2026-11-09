package com.unifiedserver.dashboard.data.repository

import com.unifiedserver.dashboard.data.PreferencesManager
import com.unifiedserver.dashboard.data.models.*
import javax.inject.Inject

class SystemRepository @Inject constructor(
    private val preferencesManager: PreferencesManager
) {

    /** Everything the Server screen shows except CPU history, in one request. */
    suspend fun getOverview(): Result<SystemOverview> = apiResult {
        buildApiService(preferencesManager).getOverview().bodyOr { SystemOverview() }
    }

    suspend fun reboot(): Result<Unit> = apiResult {
        buildApiService(preferencesManager).powerAction("reboot").requireSuccess()
    }

    suspend fun shutdown(): Result<Unit> = apiResult {
        buildApiService(preferencesManager).powerAction("shutdown").requireSuccess()
    }

    suspend fun getCpuMetrics(hours: Int = 1): Result<CpuMetrics> = apiResult {
        transformCpuHistory(buildApiService(preferencesManager).getCpuHistory(hours).bodyOr { CpuHistoryResponse() })
    }

    private fun transformCpuHistory(history: CpuHistoryResponse): CpuMetrics {
        if (history.samples.isEmpty()) return CpuMetrics()
        val usageValues = history.samples.map { it.v }
        return CpuMetrics(
            currentUsage = usageValues.last(),
            averageUsage = usageValues.average().toFloat(),
            maxUsage = usageValues.max(),
            minUsage = usageValues.min(),
            dataPoints = history.samples.map { CpuDataPoint(timestamp = it.t * 1000L, usage = it.v) },
            lastUpdated = history.samples.last().t * 1000L,
            cpuCount = 0
        )
    }
}
