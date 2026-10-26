package com.unifiedserver.dashboard.data.repository

import com.unifiedserver.dashboard.data.models.SearchResult
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.text.Normalizer
import java.util.Locale

data class AutoDownloadPolicy(
    val minimumResolution: Int = 1080,
    val maximumBytes: Long? = null,
    val minimumSeeders: Int = 1
)

private fun words(value: String): String = Normalizer.normalize(value, Normalizer.Form.NFKD)
    .replace("\\p{M}+".toRegex(), "").lowercase(Locale.ROOT)
    .replace("[^a-z0-9]+".toRegex(), " ").trim()

fun sourceResolution(name: String): Int {
    val normalized = words(name)
    return when {
        Regex("\\b(2160p|4k|uhd)\\b").containsMatchIn(normalized) -> 2160
        Regex("\\b1080p\\b").containsMatchIn(normalized) -> 1080
        Regex("\\b720p\\b").containsMatchIn(normalized) -> 720
        else -> 0
    }
}

/** Conservative filename matching, not a guarantee about the file's actual contents. */
fun selectBestSource(request: DownloadRequest, results: List<SearchResult>): SearchResult? {
    val title = words(request.title.orEmpty())
    if (title.isBlank()) return null
    val policy = request.policy
    if (policy.minimumResolution !in listOf(1080, 2160) || policy.minimumSeeders < 1 ||
        (policy.maximumBytes != null && policy.maximumBytes <= 0)) return null
    return results.asSequence().filter { candidate ->
        val name = words(candidate.fileName)
        val titleMatches = " $name ".contains(" $title ")
        val identityMatches = if (request.season != null) {
            val season = request.season
            val seasonToken = Regex("\\b(?:s0?$season|season 0?$season)\\b")
            val episode = Regex("\\bs\\d{1,2}\\s*e\\d{1,3}\\b|\\bepisode\\s*\\d+\\b")
            val otherSeasons = Regex("\\b(?:s|season\\s*)(\\d{1,2})(?!\\d)").findAll(name)
                .any { it.groupValues[1].toIntOrNull() != season }
            season > 0 && seasonToken.containsMatchIn(name) && !episode.containsMatchIn(name) && !otherSeasons
        } else {
            val year = request.year
            year != null && Regex("\\d{4}").matches(year) && Regex("\\b$year\\b").containsMatchIn(name)
        }
        val usableLink = candidate.fileUrl.toHttpUrlOrNull() != null ||
            (candidate.fileUrl.startsWith("magnet:?") &&
                (candidate.fileUrl.contains("xt=urn:btih:") || candidate.fileUrl.contains("xt=urn:btmh:")))
        titleMatches && identityMatches && usableLink && candidate.fileSize > 0 &&
            candidate.nbSeeders >= policy.minimumSeeders &&
            sourceResolution(candidate.fileName) >= policy.minimumResolution &&
            (policy.maximumBytes == null || candidate.fileSize <= policy.maximumBytes)
    }.distinctBy { it.fileUrl }
        .sortedWith(compareByDescending<SearchResult> { it.nbSeeders }
            .thenByDescending { sourceResolution(it.fileName) }.thenBy { it.fileSize })
        .firstOrNull()
}
