package com.unifiedserver.dashboard.ui.utils

import com.unifiedserver.dashboard.data.models.Torrent

/** qBittorrent states, shared by the downloads bar and Storage. */
private val DOWNLOADING_STATES = setOf("downloading", "stalledDL", "metaDL", "forcedDL", "forcedMetaDL",
    "checkingDL", "allocating", "queuedDL", "moving")
// qBittorrent 5 renamed paused* to stopped*; both are accepted.
private val STOPPED_STATES = setOf("stoppedDL", "stoppedUP", "pausedDL", "pausedUP")
private val SEEDING_STATES = setOf("uploading", "stalledUP", "forcedUP", "queuedUP")

fun isDownloadingState(state: String, progress: Float) = state in DOWNLOADING_STATES && progress < 1f
fun isStoppedState(state: String) = state in STOPPED_STATES
fun isSeedingState(state: String) = state in SEEDING_STATES

val Torrent.isDownloading get() = isDownloadingState(state, progress)
val Torrent.isStopped get() = isStoppedState(state)

private val STATE_LABELS = mapOf(
    "downloading" to "Downloading", "stalledDL" to "Waiting for peers", "metaDL" to "Getting metadata",
    "forcedDL" to "Downloading (forced)", "forcedMetaDL" to "Getting metadata", "allocating" to "Allocating",
    "stoppedDL" to "Stopped", "pausedDL" to "Stopped", "stoppedUP" to "Complete", "pausedUP" to "Complete",
    "uploading" to "Seeding", "stalledUP" to "Seeding (idle)", "forcedUP" to "Seeding (forced)",
    "queuedDL" to "Queued", "queuedUP" to "Queued to seed",
    "checkingDL" to "Checking", "checkingUP" to "Checking", "checkingResumeData" to "Checking",
    "queuedForChecking" to "Queued for check", "moving" to "Moving files",
    "error" to "Error", "missingFiles" to "Missing files", "unknown" to "Unknown"
)

fun torrentStateLabel(state: String): String = STATE_LABELS[state] ?: state

/** Remaining time; qBittorrent reports 8640000 s for "unknown". */
fun formatDownloadEta(seconds: Long): String {
    if (seconds <= 0 || seconds >= 8_640_000) return "∞"
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    return if (h > 0) "${h}h ${m}m" else "${maxOf(1, m)}m"
}

private val VIDEO_SUFFIX = Regex("""\.(mkv|mp4|avi|m4v)$""", RegexOption.IGNORE_CASE)
private val SITE_PREFIX = Regex("""^\s*(www\.)?[\w-]+\.(org|com|net|to|se|cc|me)\s*[-–]+\s*""", RegexOption.IGNORE_CASE)
private val EPISODE_MARKER = Regex(
    """^(.*?)\b(S\d{1,2}(?:E\d{1,3})?(?:\s?-\s?S?\d{1,2})?|Seasons? \d+(?:\s?-\s?\d+)?)\b""", RegexOption.IGNORE_CASE)
private val TRAILING_YEAR = Regex("""\(?\b(19|20)\d{2}\b\)?[\s-]*$""")
private val TRAILING_PUNCT = Regex("""[\s(-]+$""")
private val TITLE_YEAR = Regex("""^(.*?)[\s(]((?:19|20)\d{2})\b""")
private val QUALITY_CUT = Regex("""\b(2160p|1080p|720p|480p|web|bluray|bdrip|x26[45]|hevc|complete)\b""", RegexOption.IGNORE_CASE)

/**
 * "Verity.2026.2160p.AMZN.WEB-DL…" → "Verity (2026)";
 * "Show.S02E05.1080p…" → "Show S02E05". Same rules as the web dashboard.
 */
fun prettyTorrentName(raw: String): String {
    val s = raw.replace(VIDEO_SUFFIX, "").replace(SITE_PREFIX, "")
        .replace(Regex("""\[[^]]*]"""), " ")
        .replace(Regex("[._]+"), " ")
        .replace(Regex("""\s+"""), " ")
        .trim()
    EPISODE_MARKER.find(s)?.let { m ->
        val before = m.groupValues[1]
        if (before.isNotBlank()) {
            val show = before.replace(TRAILING_YEAR, "").replace(TRAILING_PUNCT, "").trim()
            val raw2 = m.groupValues[2]
            val marker = if (raw2.first().equals('s', true) && raw2.getOrNull(1)?.isDigit() == true) raw2.uppercase()
                else raw2.replaceFirst(Regex("^seasons?", RegexOption.IGNORE_CASE), "Season")
            return "${show.ifBlank { before.trim() }} $marker"
        }
    }
    TITLE_YEAR.find(s)?.let { m ->
        if (m.groupValues[1].isNotBlank()) return "${m.groupValues[1].replace(TRAILING_PUNCT, "").trim()} (${m.groupValues[2]})"
    }
    val cut = QUALITY_CUT.find(s)?.range?.first ?: -1
    return (if (cut > 0) s.substring(0, cut) else s).trim().ifBlank { raw }
}
