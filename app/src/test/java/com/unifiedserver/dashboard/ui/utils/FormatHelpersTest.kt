package com.unifiedserver.dashboard.ui.utils

import com.unifiedserver.dashboard.data.models.LibraryItem
import com.unifiedserver.dashboard.data.models.LibraryTorrent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FormatHelpersTest {
    @Test fun prettyTorrentNameMatchesWebDashboard() {
        assertEquals("Verity (2026)", prettyTorrentName("Verity.2026.2160p.AMZN.WEB-DL.DDP5.1.H.265.mkv"))
        assertEquals("Some Show S02E05", prettyTorrentName("Some.Show.S02E05.1080p.WEB.h264-GRP"))
        assertEquals("Modern Family S01-S11", prettyTorrentName("Modern.Family.S01-S11.1080p.BluRay"))
        assertEquals("Example Show Season 3", prettyTorrentName("www.site.org - Example Show Season 3 Complete"))
        assertEquals("Plain Name", prettyTorrentName("Plain_Name_1080p"))
    }

    @Test fun seasonRangeCollapsesRunsAndListsGaps() {
        assertEquals("S04–S08", seasonRange(listOf(8, 4, 5, 6, 7)))
        assertEquals("S01–S03, S05", seasonRange(listOf(1, 2, 3, 5)))
        assertEquals("S02", seasonRange(listOf(2)))
        assertEquals("", seasonRange(emptyList()))
    }

    @Test fun episodeTitleIsTakenFromTheFileName() {
        assertEquals("The Title", episodeTitle("Season 1/Show.S01E02.The.Title.1080p.WEB.mkv"))
        assertEquals("", episodeTitle("Show.S01E02.1080p.mkv"))
        assertEquals("", episodeTitle("random.mkv"))
    }

    @Test fun downloadEtaTreatsQbittorrentInfinityAsUnknown() {
        assertEquals("∞", formatDownloadEta(8_640_000))
        assertEquals("1m", formatDownloadEta(20))
        assertEquals("1h 5m", formatDownloadEta(3_900))
    }

    @Test fun torrentStatusInLibrary() {
        assertNull(libraryTorrentStatus(null))
        assertEquals("↓ 42%", libraryTorrentStatus(LibraryTorrent(state = "downloading", progress = 0.42f)))
        assertEquals("Stopped at 30%", libraryTorrentStatus(LibraryTorrent(state = "stoppedDL", progress = 0.3f)))
        assertEquals("Seeding", libraryTorrentStatus(LibraryTorrent(state = "stalledUP", progress = 1f)))
        assertEquals("In qBittorrent", libraryTorrentStatus(LibraryTorrent(state = "stoppedUP", progress = 1f)))
    }

    @Test fun libraryFilterKeepsMediaFirstAndMatchesNameOrTitle() {
        val folder = LibraryItem(id = "f", name = "Backups", kind = "other", size = 900)
        val leftover = LibraryItem(id = "l", name = "Old.Release.2020", kind = "leftover", title = "Old Release", size = 1)
        val movie = LibraryItem(id = "m", name = "Film.2024.1080p", kind = "movie", title = "Film", size = 50,
            modified = "2026-01-01T00:00:00Z")
        val show = LibraryItem(id = "s", name = "Show.S01", kind = "tv", title = "Show", size = 100,
            modified = "2026-03-01T00:00:00Z")
        val all = listOf(folder, leftover, movie, show)
        assertEquals(listOf("s", "m", "f", "l"), filterLibrary(all, LibraryFilter.ALL, LibrarySort.SIZE, "").map { it.id })
        assertEquals(listOf("f", "l"), filterLibrary(all, LibraryFilter.OTHER, LibrarySort.SIZE, "").map { it.id }.sorted())
        assertEquals(listOf("m"), filterLibrary(all, LibraryFilter.ALL, LibrarySort.SIZE, "1080p").map { it.id })
        assertEquals(listOf("s", "m"), filterLibrary(all, LibraryFilter.ALL, LibrarySort.RECENT, "").take(2).map { it.id })
        assertEquals(listOf("m", "s"), filterLibrary(all, LibraryFilter.ALL, LibrarySort.TITLE, "").take(2).map { it.id })
    }
}
