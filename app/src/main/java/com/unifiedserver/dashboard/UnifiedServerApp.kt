package com.unifiedserver.dashboard

import android.app.Application
import com.unifiedserver.dashboard.data.repository.BackendClient
import com.unifiedserver.dashboard.data.repository.PreferencesAddressStore
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class UnifiedServerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Before any request: lets a cold start reuse the server address from the last run.
        BackendClient.dns.store = PreferencesAddressStore(this)
    }
}
