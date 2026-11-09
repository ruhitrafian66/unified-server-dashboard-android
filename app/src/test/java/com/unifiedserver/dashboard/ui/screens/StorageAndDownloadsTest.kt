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
class StorageAndDownloadsTest {
    @get:Rule val main = MainDispatcherRule()
    private val prefs = PreferencesManager(MemoryStore())

    private val movie = LibraryItem(id = "m", name = "Film.2024", kind = "movie", title = "Film", size = 5_000,
        deletable = true, torrent = LibraryTorrent(hash = "abc", state = "uploading", progress = 1f))
    private val folder = LibraryItem(id = "f", name = "Backups", kind = "other", size = 9_000, deletable = false)

    private inner class Library : LibraryRepository(prefs) {
        var items = listOf(movie, folder)
        var rescans = 0
        val deleted = mutableListOf<String>()
        override suspend fun getLibrary(refresh: Boolean): Result<LibraryResponse> {
            if (refresh) rescans++
            return Result.success(LibraryResponse(items = items))
        }
        override suspend fun getItem(id: String) = Result.success(items.first { it.id == id })
        override suspend fun deleteItem(id: String): Result<LibraryDeleteResponse> {
            deleted += id
            items = items.filterNot { it.id == id }
            return Result.success(LibraryDeleteResponse(success = true, freed = 5_000, removedTorrent = true))
        }
    }

    private inner class Torrents(var list: List<Torrent>) : TorrentRepository(prefs) {
        var refreshes = 0
        override suspend fun getTorrents(): Result<List<Torrent>> { refreshes++; return Result.success(list) }
    }

    @Test fun deletingMediaRescansAndRefreshesDownloads() = runTest {
        val library = Library()
        val torrents = Torrents(emptyList())
        val vm = StorageViewModel(library, DownloadsMonitor(torrents))
        vm.load()
        advanceUntilIdle()
        assertEquals(listOf("m", "f"), vm.uiState.value.visibleItems.map { it.id })
        vm.open(movie)
        advanceUntilIdle()
        assertEquals(movie, vm.uiState.value.detail)
        vm.delete(movie)
        advanceUntilIdle()
        assertEquals(listOf("m"), library.deleted)
        assertNull(vm.uiState.value.selected)
        assertEquals(1, library.rescans)
        assertEquals(1, torrents.refreshes)
        assertTrue(vm.uiState.value.snackbarMessage!!.startsWith("Deleted Film"))
        assertEquals(listOf("f"), vm.uiState.value.visibleItems.map { it.id })
        vm.viewModelScope.cancel()
    }

    @Test fun readOnlyFoldersAreNeverDeleted() = runTest {
        val library = Library()
        val vm = StorageViewModel(library, DownloadsMonitor(Torrents(emptyList())))
        vm.delete(folder)
        advanceUntilIdle()
        assertTrue(library.deleted.isEmpty())
        vm.viewModelScope.cancel()
    }

    @Test fun barShowsDownloadingAndSheetAlsoListsStoppedIncomplete() = runTest {
        val repo = Torrents(listOf(
            Torrent(hash = "1", name = "a", state = "downloading", progress = 0.5f),
            Torrent(hash = "2", name = "b", state = "stoppedDL", progress = 0.2f),
            Torrent(hash = "3", name = "c", state = "stoppedUP", progress = 1f),
            Torrent(hash = "4", name = "d", state = "uploading", progress = 1f)))
        val vm = DownloadsBarViewModel(DownloadsMonitor(repo), repo, prefs)
        vm.refresh()
        advanceUntilIdle()
        assertEquals(listOf("1"), vm.state.value.active.map { it.hash })
        assertEquals(listOf("2"), vm.state.value.stoppedIncomplete.map { it.hash })
        vm.viewModelScope.cancel()
    }
}
