package com.unifiedserver.dashboard.data.repository

import com.unifiedserver.dashboard.data.ApiService
import com.unifiedserver.dashboard.data.PreferencesManager
import com.unifiedserver.dashboard.data.models.HealthResponse
import com.unifiedserver.dashboard.data.models.SystemInfo
import com.unifiedserver.dashboard.data.models.Torrent
import javax.inject.Inject

open class DashboardRepository @Inject constructor(
    private val preferencesManager: PreferencesManager
) {

    suspend fun getHealth(): Result<HealthResponse> = apiResult {
        val service = buildApiService(preferencesManager)
        val response = service.getHealth()
        if (response.isSuccessful) {
            response.body() ?: HealthResponse(status = "unknown")
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    open suspend fun testServer(url: String): Result<HealthResponse> = apiResult {
        val response = BackendClient.forUrl(url).getHealth()
        check(response.isSuccessful) { "Server request failed (HTTP ${response.code()})." }
        val health = response.body()
        check(health?.status.equals("ok", ignoreCase = true)) { "Server did not report healthy status." }
        checkNotNull(health)
    }

    suspend fun getTorrents(): Result<List<Torrent>> = apiResult {
        val service = buildApiService(preferencesManager)
        val response = service.getTorrents()
        if (response.isSuccessful) {
            response.body() ?: emptyList()
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun getSystemInfo(): Result<SystemInfo> = apiResult {
        val service = buildApiService(preferencesManager)
        val response = service.getSystemInfo()
        if (response.isSuccessful) {
            response.body() ?: SystemInfo()
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun getDisks(): Result<List<com.unifiedserver.dashboard.data.models.DiskDevice>> = apiResult {
        val service = buildApiService(preferencesManager)
        val response = service.getDisks()
        if (response.isSuccessful) {
            response.body()?.disks ?: emptyList()
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }
}
