package com.unifiedserver.dashboard.ui.screens

import androidx.lifecycle.viewModelScope
import com.unifiedserver.dashboard.MainDispatcherRule
import com.unifiedserver.dashboard.MemoryStore
import com.unifiedserver.dashboard.data.PreferencesManager
import com.unifiedserver.dashboard.data.models.*
import com.unifiedserver.dashboard.data.repository.*
import com.unifiedserver.dashboard.ui.utils.orderedDisks
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
        override suspend fun getTrendingMovies() = Result.success(emptyList<TmdbMediaItem>())
        override suspend fun searchMovies(query: String, page: Int): Result<TmdbTrendingResponse> {
            delay(if (query == "old") 1_000 else 10)
            return Result.success(TmdbTrendingResponse(page, listOf(TmdbMediaItem(id = page, title = query, mediaType = "movie")), 2, 2))
        }
        override suspend fun getMovieDetail(id: Int): Result<TmdbMovieDetail> {
            delay(if (id == 1) 1_000 else 10)
            return Result.success(TmdbMovieDetail(id = id, title = "Movie $id"))
        }
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
        assertEquals("Title 2024", handoff.pendingQuery.value)
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
        val vm = TorrentsViewModel(repo)
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
        val vm = TorrentsViewModel(repo)
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
        val vm = TorrentsViewModel(repo)
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

    @Test fun unrelatedPreferenceChangePreservesAutoCheckAndRejectedSavePreservesLocalConfig() = runTest {
        val prefs = preferences()
        prefs.updateAutoStopConfig(false, 60, 1f)
        val torrents = object : TorrentRepository(prefs) {
            override suspend fun getAutoStopConfig() = Result.success(AutoStopConfig(false, 60, 1f))
            override suspend fun setAutoStopConfig(config: AutoStopConfig) = Result.failure<Unit>(Exception("Rejected"))
        }
        val shows = object : ShowsRepository(prefs) {
            override suspend fun getAutoCheckConfig() = Result.success(AutoCheckConfig(enabled = true))
        }
        val vm = SettingsViewModel(prefs, torrents, shows, DashboardRepository(prefs))
        runCurrent()
        prefs.updateTmdbApiKey("test-only")
        runCurrent()
        assertTrue(vm.uiState.value.autoCheckEnabled)
        vm.saveAutoStopConfig(true, "5", "2.0")
        runCurrent()
        assertFalse(prefs.autoStopEnabled.first())
        assertEquals(60, prefs.autoStopSeedingTime.first())
        assertEquals("Rejected", vm.uiState.value.snackbarMessage)
        vm.viewModelScope.cancel()
    }

    @Test fun validatesNumericSettingsWithoutSilentDefaults() {
        assertEquals(5 to 1.5f, parseAutoStopLimits("5", "1,5"))
        listOf("", "-1", "NaN", "Infinity", "garbage").forEach { ratio ->
            assertThrows(IllegalArgumentException::class.java) { parseAutoStopLimits("5", ratio) }
        }
        assertThrows(IllegalArgumentException::class.java) { parseAutoStopLimits("", "1") }
    }

    @Test fun dateAddedSortUsesActualAddedTimestamp() {
        val old = Torrent(name = "old", addedOn = 1, seedingTime = 100)
        val recent = Torrent(name = "recent", addedOn = 2, seedingTime = 0)
        assertEquals(listOf(recent, old), sortTorrents(listOf(old, recent), TorrentSortOption.DATE_ADDED, SortOrder.DESCENDING))
    }

    @Test fun storageOrderIsSharedAndDoesNotHideNvmeOrRoot() {
        val root = DiskDevice(device = "mmcblk0", mountPoint = "/")
        val media = DiskDevice(device = "nvme0n1", mountPoint = "/media")
        assertEquals(listOf(media, root), orderedDisks(listOf(root, media)))
    }
}
