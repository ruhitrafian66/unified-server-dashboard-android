package com.unifiedserver.dashboard.ui.screens

import androidx.lifecycle.viewModelScope
import com.unifiedserver.dashboard.MainDispatcherRule
import com.unifiedserver.dashboard.MemoryStore
import com.unifiedserver.dashboard.data.PreferencesManager
import com.unifiedserver.dashboard.data.models.*
import com.unifiedserver.dashboard.data.repository.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WorkflowTest {
    @get:Rule val main = MainDispatcherRule()
    private fun preferences() = PreferencesManager(MemoryStore())

    @Test fun freshSetupDoesNotConstructInvalidRetrofitClient() = runTest {
        val prefs = preferences()
        val vm = SetupViewModel(prefs, DashboardRepository(prefs))
        runCurrent()
        assertEquals("", vm.uiState.value.serverUrl)
        assertFalse(vm.uiState.value.isConfigured)
        vm.viewModelScope.cancel()
    }

    @Test fun failedConnectionDoesNotOverwriteServer() = runTest {
        val prefs = preferences()
        prefs.updateServerUrl("https://old.example/")
        val repo = object : DashboardRepository(prefs) {
            override suspend fun testServer(url: String) = Result.failure<HealthResponse>(Exception("Offline"))
        }
        val vm = SetupViewModel(prefs, repo)
        vm.testAndSave("https://new.example")
        runCurrent()
        assertEquals("https://old.example/", prefs.serverUrl.first())
        assertFalse(prefs.setupComplete.first())
        assertEquals("Offline", vm.uiState.value.error)
        vm.viewModelScope.cancel()
    }

    @Test fun successfulSetupPersistsNormalizedUrlAndCompletion() = runTest {
        val prefs = preferences()
        val repo = object : DashboardRepository(prefs) {
            override suspend fun testServer(url: String) = Result.success(HealthResponse("ok"))
        }
        val vm = SetupViewModel(prefs, repo)
        vm.testAndSave(" https://new.example ")
        runCurrent()
        assertEquals("https://new.example/", prefs.serverUrl.first())
        assertTrue(prefs.setupComplete.first())
        vm.viewModelScope.cancel()
    }

    private class MediaFake(prefs: PreferencesManager) : MediaRepository(prefs) {
        override suspend fun getTrendingTv() = Result.success(emptyList<TmdbMediaItem>())
        var moviePages = 3
        override suspend fun getNewOnStreamingMovies(page: Int) = Result.success(TmdbTrendingResponse(page,
            listOf(TmdbMediaItem(id = 100 + page, title = "Movie $page", mediaType = "movie")), moviePages, moviePages))
        override suspend fun searchMovies(query: String, page: Int): Result<TmdbTrendingResponse> {
            delay(if (query == "old") 1_000 else 10)
            return Result.success(TmdbTrendingResponse(page, listOf(TmdbMediaItem(id = page, title = query, mediaType = "movie")), 2, 2))
        }
        override suspend fun getMovieDetail(id: Int): Result<TmdbMovieDetail> {
            delay(if (id == 1) 1_000 else 10)
            return Result.success(TmdbMovieDetail(id = id, title = "Movie $id"))
        }
        override suspend fun getTvDetail(id: Int) =
            Result.success(TmdbTvDetail(id = id, name = "Example Show", numberOfSeasons = 4))
    }

    @Test fun newerSearchCancelsOlderRequestAndClearStaysClear() = runTest {
        val vm = MediaViewModel(MediaFake(preferences()), DownloadHandoff())
        vm.onSearchQueryChange("old", "movie")
        advanceTimeBy(500)
        vm.onSearchQueryChange("new", "movie")
        advanceUntilIdle()
        assertEquals("new", vm.uiState.value.searchResults.single().title)
        vm.onSearchQueryChange("old", "movie")
        advanceTimeBy(500)
        vm.clearSearch()
        advanceUntilIdle()
        assertTrue(vm.uiState.value.searchResults.isEmpty())
        assertFalse(vm.uiState.value.isSearching)
        vm.viewModelScope.cancel()
    }

    @Test fun mediaPaginationAppendsAndDetailsCannotOverwriteNewSelection() = runTest {
        val vm = MediaViewModel(MediaFake(preferences()), DownloadHandoff())
        vm.onSearchQueryChange("new", "movie")
        advanceUntilIdle()
        vm.loadMore()
        advanceUntilIdle()
        assertEquals(2, vm.uiState.value.searchResults.size)
        vm.openDetail(TmdbMediaItem(id = 1))
        runCurrent()
        vm.openDetail(TmdbMediaItem(id = 2))
        advanceUntilIdle()
        assertEquals(2, vm.uiState.value.movieDetail?.id)
        vm.viewModelScope.cancel()
    }

    @Test fun findSourcesHandsOffQueryWithoutStartingBackendSearch() = runTest {
        val handoff = DownloadHandoff()
        val vm = MediaViewModel(MediaFake(preferences()), handoff)
        vm.openDetail(TmdbMediaItem(id = 3, title = "Title", releaseDate = "2024-01-01"))
        vm.downloadFromDetail()
        assertEquals("Title 2024", handoff.pendingQuery.value?.query)
        assertEquals(false, handoff.pendingQuery.value?.autoPick)
        assertNull(vm.uiState.value.selectedItem)
        handoff.consume()
        assertNull(handoff.pendingQuery.value)
        vm.viewModelScope.cancel()
    }

    private class TorrentFake(prefs: PreferencesManager) : TorrentRepository(prefs) {
        var polls = 0
        var slow = false
        override suspend fun startSearch(pattern: String, plugins: String, category: String) = Result.success(0)
        override suspend fun getSearchResults(id: Int, offset: Int): Result<SearchResultsResponse> {
            polls++
            if (slow) delay(1_000)
            return Result.success(SearchResultsResponse(listOf(SearchResult(fileName = "Found", fileUrl = "magnet:test")), "Stopped", 1))
        }
        override suspend fun stopSearch(id: Int) = Result.success(Unit)
    }

    @Test fun stoppedSearchExitsPollingAndZeroIsValidSearchId() = runTest {
        val repo = TorrentFake(preferences())
        val vm = TorrentsViewModel(repo, DownloadsMonitor(repo))
        vm.startSearch("query")
        advanceUntilIdle()
        advanceTimeBy(60_000)
        runCurrent()
        assertEquals(1, repo.polls)
        assertEquals(0, vm.uiState.value.searchId)
        assertFalse(vm.uiState.value.isSearching)
        vm.viewModelScope.cancel()
    }

    @Test fun clearingTorrentSearchCancelsLateResults() = runTest {
        val repo = TorrentFake(preferences()).apply { slow = true }
        val vm = TorrentsViewModel(repo, DownloadsMonitor(repo))
        vm.startSearch("query")
        runCurrent()
        vm.clearSearch()
        advanceUntilIdle()
        assertTrue(vm.uiState.value.searchResults.isEmpty())
        assertNull(vm.uiState.value.searchId)
        vm.viewModelScope.cancel()
    }

    @Test fun paginationCursorCountsRawRowsEvenWhenSourcesAreDuplicates() = runTest {
        val offsets = mutableListOf<Int>()
        val repo = object : TorrentRepository(preferences()) {
            override suspend fun startSearch(pattern: String, plugins: String, category: String) = Result.success(1)
            override suspend fun stopSearch(id: Int) = Result.success(Unit)
            override suspend fun getSearchResults(id: Int, offset: Int): Result<SearchResultsResponse> {
                offsets += offset
                return Result.success(SearchResultsResponse(
                    listOf(SearchResult(fileUrl = "same"), SearchResult(fileUrl = "source-$offset")),
                    "Stopped", 4))
            }
        }
        val vm = TorrentsViewModel(repo, DownloadsMonitor(repo))
        vm.startSearch("query")
        advanceUntilIdle()
        vm.loadMoreResults()
        advanceUntilIdle()
        vm.loadMoreResults()
        advanceUntilIdle()
        assertEquals(listOf(0, 2), offsets)
        assertEquals(4, vm.uiState.value.searchOffset)
        assertEquals(3, vm.uiState.value.searchResults.size)
        vm.viewModelScope.cancel()
    }

    @Test fun unrelatedPreferenceChangePreservesAutoCheckAndRejectedSavePreservesServerConfig() = runTest {
        val prefs = preferences()
        val torrents = object : TorrentRepository(prefs) {
            override suspend fun getAutoStopConfig() = Result.success(AutoStopConfig(false, 60, 1f))
            override suspend fun setAutoStopConfig(config: AutoStopConfig) = Result.failure<Unit>(Exception("Rejected"))
        }
        val shows = object : ShowsRepository(prefs) {
            override suspend fun getAutoCheckConfig() = Result.success(AutoCheckConfig(enabled = true))
        }
        val vm = SettingsViewModel(prefs, torrents, shows, DashboardRepository(prefs),
            AutomationSettingsRepository(prefs, torrents, shows))
        runCurrent()
        prefs.updateOmdbApiKey("test-only")
        runCurrent()
        assertTrue(vm.uiState.value.autoCheckEnabled)
        vm.saveAutoStopConfig(true, "5", "2.0")
        runCurrent()
        assertFalse(vm.uiState.value.autoStopEnabled)
        assertEquals(60, vm.uiState.value.autoStopMinutes)
        assertEquals("Rejected", vm.uiState.value.snackbarMessage)
        vm.viewModelScope.cancel()
    }

    @Test fun validatesSeedingLimitsAndTreatsBlankAsNoLimit() {
        assertEquals(5 to 1.5f, parseAutoStopLimits("5", "1,5"))
        assertEquals(null to 2f, parseAutoStopLimits("", "2"))
        assertEquals(30 to null, parseAutoStopLimits("30", " "))
        assertEquals(null to null, parseAutoStopLimits("", ""))
        listOf("-1", "NaN", "Infinity", "garbage").forEach { ratio ->
            assertThrows(IllegalArgumentException::class.java) { parseAutoStopLimits("5", ratio) }
        }
        listOf("-1", "1.5", "x").forEach { minutes ->
            assertThrows(IllegalArgumentException::class.java) { parseAutoStopLimits(minutes, "1") }
        }
    }

    @Test fun moviesLoadMoreAppendsNextStreamingPageAndStopsAtLastPage() = runTest {
        val media = MediaFake(preferences()).apply { moviePages = 2 }
        val vm = MediaViewModel(media, DownloadHandoff())
        advanceUntilIdle()
        assertEquals(listOf(101), vm.uiState.value.trendingMovies.map { it.id })
        vm.loadMoreMovies()
        advanceUntilIdle()
        assertEquals(listOf(101, 102), vm.uiState.value.trendingMovies.map { it.id })
        assertEquals(2, vm.uiState.value.moviesPage)
        vm.loadMoreMovies()
        advanceUntilIdle()
        assertEquals(2, vm.uiState.value.trendingMovies.size)
        vm.viewModelScope.cancel()
    }

    @Test fun storageListsMediaFirstAndWarnsOnlyForFullSeparateSystemDisk() {
        val root = DiskUsage(mountPoint = "/", size = 100, used = 90, available = 10, percent = 90f)
        val media = DiskUsage(mountPoint = "/mnt/storage", size = 1000, used = 100, available = 900, percent = 10f)
        val overview = SystemOverview(disks = OverviewDisks(root = root, storage = media))
        assertEquals(listOf("Media storage" to media, "System disk" to root),
            com.unifiedserver.dashboard.ui.components.overviewDisks(overview))
        assertNotNull(com.unifiedserver.dashboard.ui.components.systemDiskWarning(overview))
        // Same mount (no separate SSD): show it once, no SD-card warning.
        val single = SystemOverview(disks = OverviewDisks(root = root, storage = root))
        assertEquals(1, com.unifiedserver.dashboard.ui.components.overviewDisks(single).size)
        assertNull(com.unifiedserver.dashboard.ui.components.systemDiskWarning(single))
    }

    @Test fun automaticDownloadRequiresSeparateConfirmationAndCarriesRules() = runTest {
        val handoff = DownloadHandoff()
        val vm = MediaViewModel(MediaFake(preferences()), handoff)
        vm.openDetail(TmdbMediaItem(id = 3, title = "Title", releaseDate = "2024-01-01"))
        vm.downloadFromDetail(autoPick = true)
        assertNull(handoff.pendingQuery.value)
        assertTrue(vm.uiState.value.showAutomaticOptions)
        vm.confirmAutomaticDownload(AutoDownloadPolicy(minimumResolution = 2160, maximumBytes = 1000))
        assertEquals(true, handoff.pendingQuery.value?.autoPick)
        assertEquals(2160, handoff.pendingQuery.value?.policy?.minimumResolution)
        assertEquals(1000L, handoff.pendingQuery.value?.policy?.maximumBytes)
        assertNull(vm.uiState.value.selectedItem)
        assertFalse(vm.uiState.value.showAutomaticOptions)
        vm.viewModelScope.cancel()
    }

    @Test fun tvSelectionDoesNotAddTheSeriesPremiereYearAndClosingClearsDialogs() = runTest {
        val handoff = DownloadHandoff()
        val vm = MediaViewModel(MediaFake(preferences()), handoff)
        vm.openDetail(TmdbMediaItem(id = 3, name = "Example Show", mediaType = "tv", firstAirDate = "2020-01-01"))
        advanceUntilIdle()
        vm.downloadFromDetail()
        assertTrue(vm.uiState.value.showSeasonPicker)
        vm.downloadWithSeason(2)
        assertEquals("Example Show S02", handoff.pendingQuery.value?.query)
        assertEquals(false, handoff.pendingQuery.value?.autoPick)
        assertEquals(2, handoff.pendingQuery.value?.season)
        assertNull(handoff.pendingQuery.value?.year)
        assertFalse(vm.uiState.value.showSeasonPicker)
        vm.viewModelScope.cancel()
    }
}
