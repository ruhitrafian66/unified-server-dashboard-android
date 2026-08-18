package com.unifiedserver.dashboard.data.repository

import com.unifiedserver.dashboard.data.ApiService
import com.unifiedserver.dashboard.data.PreferencesManager
import com.unifiedserver.dashboard.data.models.*
import javax.inject.Inject

class SystemRepository @Inject constructor(
    private val apiService: ApiService,
    private val preferencesManager: PreferencesManager
) {

    suspend fun getSystemInfo(): Result<SystemInfo> = runCatching {
        val service = buildApiService(preferencesManager)
        val response = service.getSystemInfo()
        if (response.isSuccessful) {
            response.body() ?: SystemInfo()
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun getServices(): Result<List<ServiceStatus>> = runCatching {
        val service = buildApiService(preferencesManager)
        val response = service.getServices()
        if (response.isSuccessful) {
            response.body()?.services ?: emptyList()
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun getDisks(): Result<List<DiskDevice>> = runCatching {
        val service = buildApiService(preferencesManager)
        val response = service.getDisks()
        if (response.isSuccessful) {
            response.body()?.disks ?: emptyList()
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun getDockerContainers(): Result<List<DockerContainer>> = runCatching {
        val service = buildApiService(preferencesManager)
        val response = service.getDockerContainers()
        if (response.isSuccessful) {
            response.body()?.containers ?: emptyList()
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun reboot(): Result<Unit> = runCatching {
        val service = buildApiService(preferencesManager)
        val response = service.powerAction("reboot")
        if (!response.isSuccessful) {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun shutdown(): Result<Unit> = runCatching {
        val service = buildApiService(preferencesManager)
        val response = service.powerAction("shutdown")
        if (!response.isSuccessful) {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun getCpuMetrics(hours: Int = 1): Result<CpuMetrics> = runCatching {
        val service = buildApiService(preferencesManager)
        val response = service.getCpuHistory(hours)
        if (response.isSuccessful) {
            val historyResponse = response.body() ?: CpuHistoryResponse()
            transformCpuHistory(historyResponse)
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    private fun transformCpuHistory(history: CpuHistoryResponse): CpuMetrics {
        if (history.samples.isEmpty()) {
            return CpuMetrics()
        }

        val usageValues = history.samples.map { it.v }
        val currentUsage = usageValues.lastOrNull() ?: 0f
        val averageUsage = usageValues.average().toFloat()
        val maxUsage = usageValues.maxOrNull() ?: 0f
        val minUsage = usageValues.minOrNull() ?: 0f
        val lastUpdated = history.samples.lastOrNull()?.t?.times(1000L) ?: System.currentTimeMillis()

        val dataPoints = history.samples.map { sample ->
            CpuDataPoint(
                timestamp = sample.t * 1000L,
                usage = sample.v
            )
        }

        return CpuMetrics(
            currentUsage = currentUsage,
            averageUsage = averageUsage,
            maxUsage = maxUsage,
            minUsage = minUsage,
            dataPoints = dataPoints,
            lastUpdated = lastUpdated,
            cpuCount = 0
        )
    }
}
