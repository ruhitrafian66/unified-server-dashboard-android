package com.unifiedserver.dashboard.ui.screens

import androidx.lifecycle.viewModelScope
import com.unifiedserver.dashboard.MainDispatcherRule
import com.unifiedserver.dashboard.MemoryStore
import com.unifiedserver.dashboard.data.PreferencesManager
import com.unifiedserver.dashboard.data.models.*
import com.unifiedserver.dashboard.data.repository.*
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AutomationSettingsTest {
    @get:Rule val main = MainDispatcherRule()
    private val prefs = PreferencesManager(MemoryStore())
    private var remoteEnabled = true
    private var unavailable = false
    private var saveRejected = false
    private val torrents = object : TorrentRepository(prefs) {
        override suspend fun getAutoStopConfig() = Result.success(AutoStopConfig(enabled = true, seedTimeLimit = 60))
    }
    private val shows = object : ShowsRepository(prefs) {
        override suspend fun getAutoCheckConfig(): Result<AutoCheckConfig> =
            if (unavailable) Result.failure(Exception("Offline")) else Result.success(AutoCheckConfig(remoteEnabled))
        override suspend fun setAutoCheckEnabled(enabled: Boolean): Result<Unit> {
            if (saveRejected) return Result.failure(Exception("Rejected"))
            remoteEnabled = enabled
            return Result.success(Unit)
        }
    }

    @Test fun settingsAndTrackedShowsObserveTheSameConfirmedValue() = runTest {
        val shared = AutomationSettingsRepository(prefs, torrents, shows)
        val settings = SettingsViewModel(prefs, torrents, shows, DashboardRepository(prefs), shared)
        val tracked = ShowsViewModel(shows, shared)
        advanceUntilIdle()
        assertTrue(settings.uiState.value.autoCheckEnabled)
        assertTrue(tracked.uiState.value.autoCheckEnabled)
        settings.saveAutoCheckEnabled(false)
        advanceUntilIdle()
        assertFalse(settings.uiState.value.autoCheckEnabled)
        assertFalse(tracked.uiState.value.autoCheckEnabled)
        tracked.setAutoCheckEnabled(true)
        advanceUntilIdle()
        assertTrue(settings.uiState.value.autoCheckEnabled)
        assertTrue(tracked.uiState.value.autoCheckEnabled)
        settings.viewModelScope.cancel()
        tracked.viewModelScope.cancel()
    }

    @Test fun failedRefreshRetainsConfirmedValueAndRetryClearsOnlyItsError() = runTest {
        val shared = AutomationSettingsRepository(prefs, torrents, shows)
        shared.refresh()
        unavailable = true
        shared.refresh()
        assertEquals(true, shared.state.value.autoCheck)
        assertEquals("Offline", shared.state.value.autoCheckError)
        assertNotNull(shared.state.value.autoStop)
        assertNull(shared.state.value.autoStopError)
        unavailable = false
        remoteEnabled = false
        shared.refresh()
        assertEquals(false, shared.state.value.autoCheck)
        assertNull(shared.state.value.autoCheckError)
    }

    @Test fun rejectedSaveDoesNotChangeConfirmedState() = runTest {
        val shared = AutomationSettingsRepository(prefs, torrents, shows)
        shared.refresh()
        saveRejected = true
        assertTrue(shared.saveAutoCheck(false).isFailure)
        assertEquals(true, shared.state.value.autoCheck)
    }

    @Test fun serverChangeDoesNotCarryPreviousSettingsIntoNewServer() = runTest {
        val shared = AutomationSettingsRepository(prefs, torrents, shows)
        shared.refresh()
        prefs.updateServerUrl("https://new.example/")
        unavailable = true
        shared.refresh()
        assertNull(shared.state.value.autoCheck)
        assertEquals("https://new.example/", shared.state.value.serverUrl)
    }

    @Test fun oldServerResponseCannotRepopulateSettingsAfterSwitch() = runTest {
        val slowShows = object : ShowsRepository(prefs) {
            override suspend fun getAutoCheckConfig(): Result<AutoCheckConfig> {
                delay(1000)
                return Result.success(AutoCheckConfig(enabled = true))
            }
        }
        val shared = AutomationSettingsRepository(prefs, torrents, slowShows)
        val refresh = launch { shared.refresh() }
        runCurrent()
        prefs.updateServerUrl("https://new.example/")
        advanceUntilIdle()
        refresh.join()
        assertNull(shared.state.value.autoCheck)
        assertNull(shared.state.value.autoStop)
        assertNotNull(shared.state.value.autoCheckError)
    }
}
