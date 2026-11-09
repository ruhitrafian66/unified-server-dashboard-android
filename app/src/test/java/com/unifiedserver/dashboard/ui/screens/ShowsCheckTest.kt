package com.unifiedserver.dashboard.ui.screens

import androidx.lifecycle.viewModelScope
import com.unifiedserver.dashboard.MainDispatcherRule
import com.unifiedserver.dashboard.MemoryStore
import com.unifiedserver.dashboard.data.PreferencesManager
import com.unifiedserver.dashboard.data.models.*
import com.unifiedserver.dashboard.data.repository.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ShowsCheckTest {
    @get:Rule val main = MainDispatcherRule()
    private val prefs = PreferencesManager(MemoryStore())

    private inner class Fake(var polls: Int, val downloads: Int, val startFails: Boolean = false) : ShowsRepository(prefs) {
        var showLoads = 0
        override suspend fun getShows(): Result<List<Show>> { showLoads++; return Result.success(emptyList()) }
        override suspend fun checkEpisodes() =
            if (startFails) Result.failure(Exception("Missing X-Requested-With header"))
            else Result.success(CheckEpisodesResponse(success = true, started = true, running = true,
                message = "Checking all active shows in the background"))
        override suspend fun getCheckStatus() = Result.success(
            if (--polls > 0) CheckStatusResponse(running = true)
            else CheckStatusResponse(running = false, lastSweep = SweepSummary(downloads = List(downloads) { "S01E0$it" })))
    }

    private fun vm(repo: ShowsRepository) =
        ShowsViewModel(repo, AutomationSettingsRepository(prefs, TorrentRepository(prefs), repo))

    @Test fun checkRunsInBackgroundUntilServerSweepFinishes() = runTest {
        val repo = Fake(polls = 3, downloads = 2)
        val vm = vm(repo)
        runCurrent()
        val loadsBefore = repo.showLoads
        vm.checkEpisodes()
        runCurrent()
        assertTrue(vm.uiState.value.isCheckingEpisodes)
        assertEquals("Checking all active shows in the background", vm.uiState.value.snackbarMessage)
        advanceUntilIdle()
        assertFalse(vm.uiState.value.isCheckingEpisodes)
        assertEquals("Check finished: 2 new episode(s) added", vm.uiState.value.snackbarMessage)
        assertTrue(repo.showLoads > loadsBefore)
        vm.viewModelScope.cancel()
    }

    @Test fun rejectedCheckReportsServerReason() = runTest {
        val vm = vm(Fake(polls = 1, downloads = 0, startFails = true))
        vm.checkEpisodes()
        advanceUntilIdle()
        assertFalse(vm.uiState.value.isCheckingEpisodes)
        assertEquals("Error starting check: Missing X-Requested-With header", vm.uiState.value.snackbarMessage)
        vm.viewModelScope.cancel()
    }
}
