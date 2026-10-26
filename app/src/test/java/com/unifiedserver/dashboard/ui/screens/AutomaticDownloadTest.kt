package com.unifiedserver.dashboard.ui.screens

import androidx.lifecycle.viewModelScope
import com.unifiedserver.dashboard.MainDispatcherRule
import com.unifiedserver.dashboard.MemoryStore
import com.unifiedserver.dashboard.data.PreferencesManager
import com.unifiedserver.dashboard.data.models.*
import com.unifiedserver.dashboard.data.repository.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AutomaticDownloadTest {
    @get:Rule val main = MainDispatcherRule()
    private val request = DownloadRequest("Example Movie 2024", true, "Example Movie", "2024")
    private fun source(seeds: Int) = SearchResult("Example.Movie.2024.1080p",
        "https://example.test/$seeds", 100, seeds)

    private inner class Fake : TorrentRepository(PreferencesManager(MemoryStore())) {
        var polls = 0
        var runningPolls = 0
        var secondPage = false
        var addFails = false
        var addDelay = 0L
        val added = mutableListOf<String>()
        val offsets = mutableListOf<Int>()
        var stops = 0
        override suspend fun startSearch(pattern: String, plugins: String, category: String) = Result.success(0)
        override suspend fun getTorrents() = Result.success(emptyList<Torrent>())
        override suspend fun getSearchResults(id: Int, offset: Int): Result<SearchResultsResponse> {
            polls++
            offsets += offset
            return Result.success(SearchResultsResponse(listOf(source(if (offset == 0) 1 else 50)),
                if (polls <= runningPolls) "Running" else "Stopped", if (secondPage) 2 else 1))
        }
        override suspend fun stopSearch(id: Int): Result<Unit> { stops++; return Result.success(Unit) }
        override suspend fun addTorrent(urls: String): Result<Unit> {
            added += urls
            delay(addDelay)
            return if (addFails) Result.failure(Exception("Connection lost")) else Result.success(Unit)
        }
    }

    @Test fun selectsAcrossAllPagesAndShowsPersistentSuccess() = runTest {
        val repo = Fake().apply { secondPage = true }
        val vm = TorrentsViewModel(repo)
        vm.autoPickAndAdd(request)
        advanceUntilIdle()
        assertEquals(listOf(0, 1), repo.offsets)
        assertEquals(listOf("https://example.test/50"), repo.added)
        assertEquals(DownloadStage.SUCCEEDED, vm.uiState.value.operation?.stage)
        assertEquals(1, repo.stops)
        vm.viewModelScope.cancel()
    }

    @Test fun stableFirstPageDoesNotEndSearchPrematurely() = runTest {
        val repo = Fake().apply { runningPolls = 5 }
        val vm = TorrentsViewModel(repo)
        vm.autoPickAndAdd(request)
        advanceTimeBy(7_000)
        runCurrent()
        assertTrue(repo.added.isEmpty())
        assertEquals(DownloadStage.SEARCHING, vm.uiState.value.operation?.stage)
        advanceUntilIdle()
        assertEquals(6, repo.polls)
        assertEquals(1, repo.added.size)
        vm.viewModelScope.cancel()
    }

    @Test fun cancellationStopsServerSearchAndNeverAdds() = runTest {
        val repo = Fake().apply { runningPolls = 1000 }
        val vm = TorrentsViewModel(repo)
        vm.autoPickAndAdd(request)
        runCurrent()
        vm.stopSearch()
        advanceUntilIdle()
        assertTrue(repo.added.isEmpty())
        assertEquals(DownloadStage.CANCELLED, vm.uiState.value.operation?.stage)
        assertEquals(1, repo.stops)
        vm.viewModelScope.cancel()
    }

    @Test fun timeoutNeverSelectsAnUnfinishedResultSet() = runTest {
        val repo = Fake().apply { runningPolls = 1000 }
        val vm = TorrentsViewModel(repo)
        vm.autoPickAndAdd(request)
        advanceUntilIdle()
        assertTrue(repo.added.isEmpty())
        assertEquals(DownloadStage.FAILED, vm.uiState.value.operation?.stage)
        assertTrue(vm.uiState.value.operation?.message.orEmpty().contains("timed out"))
        vm.viewModelScope.cancel()
    }

    @Test fun uncertainAddDoesNotOfferAutomaticRetry() = runTest {
        val repo = Fake().apply { addFails = true }
        val vm = TorrentsViewModel(repo)
        vm.autoPickAndAdd(request)
        advanceUntilIdle()
        assertEquals(DownloadStage.FAILED, vm.uiState.value.operation?.stage)
        assertEquals(false, vm.uiState.value.operation?.canRetry)
        vm.retryAutomaticDownload()
        advanceUntilIdle()
        assertEquals(1, repo.added.size)
        vm.viewModelScope.cancel()
    }

    @Test fun addingCannotBeReplacedOrPresentedAsCancelled() = runTest {
        val repo = Fake().apply { addDelay = 1000 }
        val vm = TorrentsViewModel(repo)
        vm.autoPickAndAdd(request)
        runCurrent()
        assertEquals(DownloadStage.ADDING, vm.uiState.value.operation?.stage)
        assertTrue(vm.uiState.value.isMutating)
        vm.stopSearch()
        vm.autoPickAndAdd(request.copy(query = "Another"))
        advanceUntilIdle()
        assertEquals(1, repo.added.size)
        assertEquals(DownloadStage.SUCCEEDED, vm.uiState.value.operation?.stage)
        assertFalse(vm.uiState.value.isMutating)
        vm.viewModelScope.cancel()
    }
}
