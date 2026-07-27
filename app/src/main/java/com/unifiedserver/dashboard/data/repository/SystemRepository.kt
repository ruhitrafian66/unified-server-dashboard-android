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
}
