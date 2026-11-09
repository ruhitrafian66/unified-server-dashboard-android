package com.unifiedserver.dashboard.data.repository

import com.unifiedserver.dashboard.MemoryStore
import com.unifiedserver.dashboard.data.PreferencesManager
import com.unifiedserver.dashboard.data.models.ApiResponse
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Response

class BackendClientTest {
    @Test fun normalizesUrlAndPreservesBasePath() {
        assertEquals("https://example.com/dashboard/", normalizeServerUrl(" https://example.com/dashboard "))
    }

    @Test fun rejectsBlankMalformedAndCredentialUrls() {
        listOf("", "/", "server.local", "ftp://example.com", "https://user:password@example.com",
            "https://example.com/?token=x", "https://example.com/#fragment").forEach {
            assertThrows(IllegalArgumentException::class.java) { normalizeServerUrl(it) }
        }
    }

    @Test fun reusesConfiguredService() {
        assertSame(BackendClient.forUrl("https://example.com"), BackendClient.forUrl("https://example.com/"))
    }

    @Test fun cancellationIsNotConvertedIntoFailure() {
        assertThrows(CancellationException::class.java) {
            apiResult<Unit> { throw CancellationException("cancel") }
        }
    }

    @Test fun rejectsBusinessFailureInSuccessfulHttpResponse() {
        assertThrows(java.io.IOException::class.java) {
            Response.success(ApiResponse<Any>(success = false, error = "Rejected")).requireSuccess()
        }
    }

    @Test fun acceptsConfirmedAction() {
        Response.success(ApiResponse<Any>(success = true)).requireSuccess()
    }

    @Test fun actionRepositoryDoesNotReportRejectedMutationAsSuccess() = runBlocking {
        val server = MockWebServer()
        server.start()
        try {
            server.enqueue(MockResponse().setBody("""{"success":false,"error":"Rejected"}"""))
            val preferences = PreferencesManager(MemoryStore())
            preferences.updateServerUrl(server.url("/").toString())
            val result = TorrentRepository(preferences).pauseTorrents("test-hash")
            assertTrue(result.isFailure)
            assertEquals("/api/qbittorrent/torrents/pause", server.takeRequest().path)
        } finally { server.shutdown() }
    }

    @Test fun paginationPassesOffsetToBackend() = runBlocking {
        val server = MockWebServer()
        server.start()
        try {
            server.enqueue(MockResponse().setBody("""{"results":[],"status":"Stopped","total":50}"""))
            val preferences = PreferencesManager(MemoryStore())
            preferences.updateServerUrl(server.url("/").toString())
            assertTrue(TorrentRepository(preferences).getSearchResults(7, 50).isSuccess)
            val url = server.takeRequest().requestUrl!!
            assertEquals("50", url.queryParameter("offset"))
            assertEquals("50", url.queryParameter("limit"))
        } finally { server.shutdown() }
    }

    private fun <T> withServer(body: MockResponse, block: suspend (PreferencesManager) -> T): Pair<T, okhttp3.mockwebserver.RecordedRequest> =
        runBlocking {
            val server = MockWebServer()
            server.start()
            try {
                server.enqueue(body)
                val preferences = PreferencesManager(MemoryStore())
                preferences.updateServerUrl(server.url("/").toString())
                block(preferences) to server.takeRequest()
            } finally { server.shutdown() }
        }

    @Test fun writesCarryTheCsrfHeaderTheBackendRequires() {
        val (result, request) = withServer(MockResponse().setBody("""{"success":true}""")) {
            TorrentRepository(it).resumeTorrents("abc123")
        }
        assertTrue(result.isSuccess)
        assertEquals("POST", request.method)
        assertEquals("dashboard", request.getHeader("X-Requested-With"))
    }

    @Test fun serverErrorMessageIsShownInsteadOfStatusCode() {
        val (result, _) = withServer(MockResponse().setResponseCode(409)
            .setBody("""{"error":"Show is already being tracked"}""")) {
            ShowsRepository(it).addShow(com.unifiedserver.dashboard.data.models.AddShowRequest(tmdbId = 1, name = "x"))
        }
        assertEquals("Show is already being tracked", result.exceptionOrNull()?.message)
    }

    @Test fun nonJsonErrorFallsBackToStatusCode() {
        val (result, _) = withServer(MockResponse().setResponseCode(502).setBody("<html>Bad gateway</html>")) {
            DashboardRepository(it).getOverview()
        }
        assertEquals("Server request failed (HTTP 502).", result.exceptionOrNull()?.message)
    }

    @Test fun overviewParsesPartialFailures() {
        val json = """{"cpu":{"percent":12.5,"cores":4},"memory":null,"uptime":3600,"load":[0.5,0.4,0.3],
            "temperature":51.2,"disks":{"root":{"mountPoint":"/","size":100,"used":50,"available":50,"percent":50},
            "storage":null},"services":[{"service":"smbd","status":"active","sub":"running"}],
            "containers":[{"name":"gluetun","state":"running","status":"Up 2 days (healthy)","image":"gluetun",
            "running":true,"healthy":true}],"torrentClient":{"connection":"firewalled","dhtNodes":120,
            "dlSpeed":1024,"upSpeed":0},"deploy":{"commit":"abc123def456"},"errors":{"memory":"EACCES","storage":"ENOENT"}}"""
        val (result, request) = withServer(MockResponse().setBody(json)) { DashboardRepository(it).getOverview() }
        assertEquals("/api/system/overview", request.path)
        val overview = result.getOrThrow()
        assertEquals(12.5f, overview.cpu?.percent)
        assertNull(overview.memory)
        assertTrue(overview.services!!.single().running)
        assertEquals(true, overview.containers!!.single().healthy)
        assertEquals("firewalled", overview.torrentClient?.connection)
        assertEquals(setOf("memory", "storage"), overview.errors?.keys)
        assertEquals(listOf("memory", "media storage"),
            com.unifiedserver.dashboard.ui.components.unavailableSections(overview))
    }

    @Test fun mediaUsesServerTmdbProxyWithoutLocalKey() {
        val (result, request) = withServer(MockResponse().setBody(
            """{"page":1,"results":[{"id":7,"title":"Film"}],"total_pages":3,"total_results":60,"windowDays":120}""")) {
            MediaRepository(it).getNewOnStreamingMovies(1)
        }
        assertEquals("/api/tmdb/movies/streaming?page=1", request.path)
        assertNull(request.requestUrl?.queryParameter("api_key"))
        val page = result.getOrThrow()
        assertEquals("movie", page.results.single().mediaType)
        assertEquals(3, page.totalPages)
    }

    @Test fun libraryParsesScanAndDeletesWithCsrfHeader() {
        val json = """{"root":"/mnt/storage/Server","torrentsAvailable":false,
            "disk":{"size":1000,"available":250,"used":750},
            "items":[{"id":"TW9kZXJu","name":"Modern.Family.S01-S11","kind":"tv","title":"Modern Family","year":2009,
              "quality":["1080p"],"seasonNumbers":[4,5,6,7,8],"episodeCount":120,"missingSeasons":[1,2,3,9,10,11],
              "size":900,"fileCount":130,"deletable":true,"duplicateOf":[],"poster":"/p.jpg","torrent":null}],
            "junk":{"count":2,"size":4096,"items":[{"name":"._x","size":4096,"reason":"macOS metadata file"}]}}"""
        val (library, request) = withServer(MockResponse().setBody(json)) { LibraryRepository(it).getLibrary(refresh = true) }
        assertEquals("/api/library?refresh=1", request.path)
        val scan = library.getOrThrow()
        assertFalse(scan.torrentsAvailable)
        assertEquals(75f, scan.disk!!.percent)
        val item = scan.items.single()
        assertEquals(listOf(4, 5, 6, 7, 8), item.allSeasonNumbers)
        assertEquals("https://image.tmdb.org/t/p/w185/p.jpg", item.posterUrl)
        assertEquals("S04–S08 · 120 episodes", com.unifiedserver.dashboard.ui.utils.librarySummary(item))

        val (deleted, deleteRequest) = withServer(MockResponse().setBody("""{"success":true,"freed":900,"removedTorrent":false}""")) {
            LibraryRepository(it).deleteItem("TW9kZXJu")
        }
        assertEquals(900L, deleted.getOrThrow().freed)
        assertEquals("DELETE", deleteRequest.method)
        assertEquals("/api/library/items/TW9kZXJu", deleteRequest.path)
        assertEquals("dashboard", deleteRequest.getHeader("X-Requested-With"))
    }

    @Test fun refusedLibraryDeleteShowsServerReason() {
        val (result, _) = withServer(MockResponse().setResponseCode(403)
            .setBody("""{"error":"This folder is not media; manage it over the file share instead."}""")) {
            LibraryRepository(it).deleteItem("x")
        }
        assertEquals("This folder is not media; manage it over the file share instead.", result.exceptionOrNull()?.message)
    }
}
