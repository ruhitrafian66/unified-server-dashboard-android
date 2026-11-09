package com.unifiedserver.dashboard.ui.utils

import com.unifiedserver.dashboard.data.models.LibraryItem
import com.unifiedserver.dashboard.data.models.LibraryTorrent
import java.time.Instant

enum class LibraryFilter(val label: String) { ALL("All"), MOVIES("Movies"), TV("TV"), OTHER("Other") }
enum class LibrarySort(val label: String) { SIZE("Largest"), RECENT("Newest"), TITLE("A–Z") }

private fun pad(n: Int) = n.toString().padStart(2, '0')

/** [4,5,6,7,8] → "S04–S08"; gaps are listed: [1,2,3,5] → "S01–S03, S05". */
fun seasonRange(numbers: List<Int>): String {
    val nums = numbers.distinct().sorted()
    if (nums.isEmpty()) return ""
    val parts = mutableListOf<String>()
    var start = nums[0]
    var prev = nums[0]
    for (n in nums.drop(1) + listOf(null)) {
        if (n != null && n == prev + 1) { prev = n; continue }
        parts += if (start == prev) "S${pad(start)}" else "S${pad(start)}–S${pad(prev)}"
        if (n != null) { start = n; prev = n }
    }
    return parts.joinToString(", ")
}

fun librarySummary(item: LibraryItem): String = when (item.kind) {
    "tv" -> "${seasonRange(item.allSeasonNumbers)} · ${item.episodeCount} episode${if (item.episodeCount == 1) "" else "s"}"
        .trimStart(' ', '·')
    "movie" -> if (item.videoCount > 1) "Movie · ${item.videoCount} videos" else "Movie"
    "leftover" -> "No video files — incomplete or leftover download"
    else -> "${"%,d".format(item.fileCount)} file${if (item.fileCount == 1) "" else "s"} · not media"
}

/** Short status of the matching torrent, or null if there is none. */
fun libraryTorrentStatus(torrent: LibraryTorrent?): String? = when {
    torrent == null -> null
    isDownloadingState(torrent.state, torrent.progress) -> "↓ ${(torrent.progress * 100).toInt()}%"
    torrent.progress < 1f -> "Stopped at ${(torrent.progress * 100).toInt()}%"
    isSeedingState(torrent.state) -> "Seeding"
    else -> "In qBittorrent"
}

private val EPISODE_SPLIT = Regex("""(?:^|[^a-z0-9])(?:S\d{1,2}[ ._-]?E\d{1,3}|\d{1,2}x\d{2,3})""", RegexOption.IGNORE_CASE)
private val EPISODE_TAIL = Regex("""\(|\[|\b(2160p|1080p|720p|480p|web|bluray|bdrip|x26[45]|hevc|ita|eng)\b""", RegexOption.IGNORE_CASE)

/** "Show.S01E02.The.Title.1080p.mkv" → "The Title" (blank if the file has no title). */
fun episodeTitle(file: String): String {
    val base = file.substringAfterLast('/').replace(Regex("""\.[a-z0-9]{2,4}$""", RegexOption.IGNORE_CASE), "")
    val parts = EPISODE_SPLIT.split(base)
    val after = parts.getOrNull(1) ?: return ""
    return after.replace(Regex("[._]+"), " ").split(EPISODE_TAIL).first().trim(' ', '-', '–')
}

/** Movies and shows first, then leftovers and other folders; each group in the chosen order. */
fun filterLibrary(items: List<LibraryItem>, filter: LibraryFilter, sort: LibrarySort, query: String): List<LibraryItem> {
    val q = query.trim().lowercase()
    val filtered = items.filter { item ->
        val kindMatches = when (filter) {
            LibraryFilter.ALL -> true
            LibraryFilter.MOVIES -> item.kind == "movie"
            LibraryFilter.TV -> item.kind == "tv"
            LibraryFilter.OTHER -> item.kind == "other" || item.kind == "leftover"
        }
        kindMatches && (q.isEmpty() || item.title.lowercase().contains(q) || item.name.lowercase().contains(q))
    }
    val order = when (sort) {
        LibrarySort.SIZE -> compareByDescending<LibraryItem> { it.size }
        LibrarySort.RECENT -> compareByDescending { it.modified?.let { m -> runCatching { Instant.parse(m) }.getOrNull() } }
        LibrarySort.TITLE -> compareBy { it.displayTitle.lowercase() }
    }
    return filtered.sortedWith(compareBy<LibraryItem> { if (it.isMedia) 0 else 1 }.then(order))
}
