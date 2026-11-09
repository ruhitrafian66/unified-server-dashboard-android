package com.unifiedserver.dashboard.data.repository

import com.unifiedserver.dashboard.data.models.SearchResult
import org.junit.Assert.*
import org.junit.Test

class SourceSelectionTest {
    private val request = DownloadRequest("Example Movie 2024", true, "Example Movie", "2024")
    private fun source(name: String, seeds: Int = 10, bytes: Long = 100) =
        SearchResult(fileName = name, fileUrl = "https://example.test/$seeds/$bytes", fileSize = bytes, nbSeeders = seeds)

    @Test fun ranksBySeedersAcrossEligibleQualities() {
        val fourK = source("Example.Movie.2024.2160p", 1)
        val fullHd = source("Example.Movie.2024.1080p", 40)
        assertEquals(fullHd, selectBestSource(request, listOf(fourK, fullHd)))
    }

    @Test fun neverFallsBackBelowMinimumOrToDeadUnknownSources() {
        val candidates = listOf(source("Example Movie 2024 720p", 100),
            source("Example Movie 2024 2160p", 0), source("Example Movie 2024 1080p", 10, -1))
        assertNull(selectBestSource(request, candidates))
    }

    @Test fun enforcesTitleYearAndWholeQualityTokens() {
        val candidates = listOf(source("Another Movie 2024 1080p"),
            source("Example Movie 2023 1080p"), source("Example Movie 2024 4kids"))
        assertNull(selectBestSource(request, candidates))
    }

    @Test fun enforcesSizeAndExplicit4kRequirement() {
        val policy = request.copy(policy = AutoDownloadPolicy(2160, 100))
        val candidates = listOf(source("Example Movie 2024 2160p", 100, 101),
            source("Example Movie 2024 1080p", 100, 50))
        assertNull(selectBestSource(policy, candidates))
        val eligible = source("Example Movie 2024 UHD", 3, 100)
        assertEquals(eligible, selectBestSource(policy, candidates + eligible))
    }

    @Test fun requiresRequestedSeasonAndRejectsEpisodesOrOtherSeasons() {
        val tv = request.copy(title = "Example Show", year = null, season = 2)
        val wrong = listOf(source("Example.Show.S01.1080p"), source("Example.Show.S02E01.1080p"),
            source("Example.Show.Season.2.Episode.1.1080p"), source("Example.Show.S02.S03.1080p"),
            source("Example.Show.Season.2.Season.3.1080p"))
        assertNull(selectBestSource(tv, wrong))
        val pack = source("Example.Show.S02.Complete.1080p")
        assertEquals(pack, selectBestSource(tv, wrong + pack))
    }

    @Test fun doesNotUseUnsupportedOrBlankLinks() {
        val good = source("Example Movie 2024 1080p")
        assertNull(selectBestSource(request, listOf(good.copy(fileUrl = ""), good.copy(fileUrl = "javascript:run()"))))
        assertEquals(good, selectBestSource(request, listOf(good)))
    }

    @Test fun missingIdentityMustUseManualSelection() {
        assertNull(selectBestSource(DownloadRequest("something", true), listOf(source("something 2024 1080p"))))
    }

    @Test fun neverAutoPicksMalwareLookingReleases() {
        val fake = source("Example Movie 2024 1080p .exe", 500)
        val archive = source("Example.Movie.2024.1080p.rar password", 400)
        val real = source("Example.Movie.2024.1080p.WEB", 5)
        assertTrue(isSuspiciousSourceName(fake.fileName))
        assertFalse(isSuspiciousSourceName(real.fileName))
        assertEquals(real, selectBestSource(request, listOf(fake, archive, real)))
    }
}
