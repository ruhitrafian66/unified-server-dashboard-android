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
}
