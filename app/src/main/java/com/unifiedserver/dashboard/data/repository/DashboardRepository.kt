package com.unifiedserver.dashboard.data.repository

import com.unifiedserver.dashboard.data.PreferencesManager
import com.unifiedserver.dashboard.data.models.HealthResponse
import com.unifiedserver.dashboard.data.models.SystemOverview
import com.unifiedserver.dashboard.data.models.Torrent
import javax.inject.Inject

open class DashboardRepository @Inject constructor(
    private val preferencesManager: PreferencesManager
) {

    suspend fun getHealth(): Result<HealthResponse> = apiResult {
        buildApiService(preferencesManager).getHealth().bodyOr { HealthResponse(status = "unknown") }
    }

    open suspend fun testServer(url: String): Result<HealthResponse> = apiResult {
        val response = BackendClient.forUrl(url).getHealth()
        if (!response.isSuccessful) throw response.httpFailure()
        val health = response.body()
        check(health?.status.equals("ok", ignoreCase = true)) { "Server did not report healthy status." }
        checkNotNull(health)
    }

    suspend fun getTorrents(): Result<List<Torrent>> = apiResult {
        buildApiService(preferencesManager).getTorrents().bodyOr { emptyList() }
    }

    suspend fun getOverview(): Result<SystemOverview> = apiResult {
        buildApiService(preferencesManager).getOverview().bodyOr { SystemOverview() }
    }
}
