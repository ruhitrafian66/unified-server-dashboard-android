package com.unifiedserver.dashboard.data.repository

import com.unifiedserver.dashboard.data.PreferencesManager
import com.unifiedserver.dashboard.data.models.HealthResponse
import com.unifiedserver.dashboard.data.models.SystemOverview
import javax.inject.Inject

open class DashboardRepository @Inject constructor(
    private val preferencesManager: PreferencesManager
) {

    open suspend fun testServer(url: String): Result<HealthResponse> = apiResult {
        val response = BackendClient.forUrl(url).getHealth()
        if (!response.isSuccessful) throw response.httpFailure()
        val health = response.body()
        check(health?.status.equals("ok", ignoreCase = true)) { "Server did not report healthy status." }
        checkNotNull(health)
    }

    open suspend fun getOverview(): Result<SystemOverview> = apiResult {
        buildApiService(preferencesManager).getOverview().bodyOr { SystemOverview() }
    }
}
