package com.unifiedserver.dashboard.data.repository

import com.unifiedserver.dashboard.data.ApiService
import com.unifiedserver.dashboard.data.PreferencesManager
import com.unifiedserver.dashboard.data.models.HealthResponse
import com.unifiedserver.dashboard.data.models.SystemInfo
import com.unifiedserver.dashboard.data.models.Torrent
import javax.inject.Inject

class DashboardRepository @Inject constructor(
    private val apiService: ApiService,
    private val preferencesManager: PreferencesManager
) {

    suspend fun getHealth(): Result<HealthResponse> = runCatching {
        val service = buildApiService(preferencesManager)
        val response = service.getHealth()
        if (response.isSuccessful) {
            response.body() ?: HealthResponse(status = "unknown")
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun getTorrents(): Result<List<Torrent>> = runCatching {
        val service = buildApiService(preferencesManager)
        val response = service.getTorrents()
        if (response.isSuccessful) {
            response.body() ?: emptyList()
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun getSystemInfo(): Result<SystemInfo> = runCatching {
        val service = buildApiService(preferencesManager)
        val response = service.getSystemInfo()
        if (response.isSuccessful) {
            response.body() ?: SystemInfo()
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }
}
