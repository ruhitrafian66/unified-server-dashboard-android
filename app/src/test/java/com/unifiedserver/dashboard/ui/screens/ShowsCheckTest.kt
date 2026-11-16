package com.unifiedserver.dashboard.ui.screens

import androidx.lifecycle.viewModelScope
import com.unifiedserver.dashboard.MainDispatcherRule
import com.unifiedserver.dashboard.MemoryStore
import com.unifiedserver.dashboard.data.PreferencesManager
import com.unifiedserver.dashboard.data.models.*
import com.unifiedserver.dashboard.data.repository.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
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
        var showDelay = 0L
        var diskFull = false
        val episodeRequests = mutableListOf<String>()
        override suspend fun getShows(): Result<List<Show>> {
            showLoads++
            delay(showDelay)
            return Result.success(emptyList())
        }
        override suspend fun downloadEpisodes(showId: String, season: Int, startEpisode: Int, endEpisode: Int): Result<DownloadSeasonResponse> {
            episodeRequests += "$showId S$season E$startEpisode-$endEpisode"
            delay(10)
            return Result.success(
                if (diskFull) DownloadSeasonResponse(success = false, message = "Insufficient disk space (less than 5GB available)")
                else DownloadSeasonResponse(success = true, sessionId = "s1", message = "Searching Example S03 E$startEpisode–$endEpisode in the background"))
        }
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

    @Test fun periodicRefreshIsSilentAndDoesNotRepeatTheOpeningLoad() = runTest {
        val repo = Fake(polls = 1, downloads = 0).apply { showDelay = 10 }
        val vm = vm(repo)
        vm.refresh(silent = true)   // the screen's first tick, while the opening load is still running
        runCurrent()
        assertTrue("the first load shows progress", vm.uiState.value.isLoading)
        advanceUntilIdle()
        assertEquals(1, repo.showLoads)

        vm.refresh(silent = true)
        runCurrent()
        assertEquals(2, repo.showLoads)
        assertFalse("later periodic refreshes show no spinner", vm.uiState.value.isLoading)
        advanceUntilIdle()

        vm.refresh()                // pull to refresh
        runCurrent()
        assertTrue(vm.uiState.value.isLoading)
        advanceUntilIdle()
        assertFalse(vm.uiState.value.isLoading)
        vm.viewModelScope.cancel()
    }

    @Test fun trackedShowDownloadAsksForAnEpisodeRangeOnceAndReportsTheQueue() = runTest {
        val repo = Fake(polls = 1, downloads = 0)
        val vm = vm(repo)
        advanceUntilIdle()
        val show = Show(id = "42", name = "Example", currentSeason = 3)

        vm.downloadEpisodes(show, 2, 99)
        vm.downloadEpisodes(show, 2, 99)   // a second tap while the first request is in flight
        assertEquals("42", vm.uiState.value.requestingEpisodesFor)
        advanceUntilIdle()
        assertEquals(listOf("42 S3 E2-99"), repo.episodeRequests)
        assertEquals("Searching Example S03 E2–99 in the background. Progress is in the search queue.",
            vm.uiState.value.snackbarMessage)
        assertNull(vm.uiState.value.requestingEpisodesFor)

        repo.diskFull = true
        vm.downloadEpisodes(show, 1, 99)
        advanceUntilIdle()
        assertEquals("Insufficient disk space (less than 5GB available)", vm.uiState.value.snackbarMessage)
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
