package com.unifiedserver.dashboard.data.models

import com.google.gson.annotations.SerializedName

// ─── Health ──────────────────────────────────────────────────────────────────

data class HealthResponse(
    val status: String = "unknown",
    val timestamp: String? = null,
    val platform: String? = null,
    val nodeVersion: String? = null
)

// ─── Torrent ──────────────────────────────────────────────────────────────────

data class Torrent(
    val hash: String = "",
    val name: String = "",
    val state: String = "",
    val progress: Float = 0f,
    val dlspeed: Long = 0,
    val upspeed: Long = 0,
    val size: Long = 0,
    val completed: Long = 0,
    val ratio: Float = 0f,
    val eta: Long = 0,
    val num_seeds: Int = 0,
    val num_leechs: Int = 0,
    val save_path: String = "",
    @SerializedName("seeding_time") val seedingTime: Long = 0
)

data class AddTorrentRequest(val urls: String)

data class AddTorrentAdvancedRequest(
    val urls: String,
    val savepath: String,
    val category: String? = null
)

data class TorrentActionRequest(
    val hashes: String,
    val deleteFiles: Boolean = false
)

data class AutoStopConfig(
    val enabled: Boolean = false,
    @SerializedName("delayMinutes") val seedingTimeLimit: Int = 0,
    @SerializedName("ratioLimit") val ratioLimit: Float? = null,
    @SerializedName("seedTimeLimit") val seedTimeLimit: Int? = null
)

// ─── Search ──────────────────────────────────────────────────────────────────

data class SearchStartRequest(
    val pattern: String,
    val plugins: String = "enabled",
    val category: String = "all"
)

data class SearchStartResponse(val id: Int)

// Backend returns an array of status objects
data class SearchStatusItem(
    val id: Int = 0,
    val status: String = "Running",
    val total: Int = 0
)

data class SearchResult(
    val fileName: String = "",
    val fileUrl: String = "",
    val fileSize: Long = -1,
    val nbSeeders: Int = 0,
    val nbLeechers: Int = 0,
    val engineName: String = "",
    val siteUrl: String = ""
)

data class SearchResultsResponse(
    val results: List<SearchResult> = emptyList(),
    val status: String = "",
    val total: Int = 0
)

data class SearchStopRequest(val id: Int)

// ─── OMV / System ────────────────────────────────────────────────────────────
// The backend returns flat string values from shell commands

data class SystemInfo(
    // Raw string values from the backend (e.g. "2.1" for CPU, "512/1024 MB (50.00%)" for memory)
    val cpu: String = "",
    val memory: String = "",
    val disk: String = "",
    val uptime: String = ""
)

data class ServicesResponse(
    val services: List<ServiceStatus> = emptyList()
)

data class ServiceStatus(
    val service: String = "",
    val status: String = "inactive"
) {
    val running: Boolean get() = status == "active"
}

data class DisksResponse(
    val disks: List<DiskDevice> = emptyList()
)

data class DiskDevice(
    val device: String = "",
    val size: String = "",
    val used: String = "",
    val available: String = "",
    @SerializedName("usePercent") val usePercent: String = "0%",
    val mountPoint: String = ""
) {
    val usedPercent: Float
        get() = usePercent.removeSuffix("%").toFloatOrNull() ?: 0f
}

// ─── Docker ──────────────────────────────────────────────────────────────────

data class DockerContainersResponse(
    val containers: List<DockerContainer> = emptyList()
)

data class DockerContainer(
    val name: String = "",
    val state: String = "",
    val status: String = "",
    val image: String = "",
    val running: Boolean = false
)

// ─── TV Shows ─────────────────────────────────────────────────────────────────
// Backend uses string IDs (Date.now().toString())

data class Show(
    val id: String = "",
    val name: String = "",
    val currentSeason: Int = 1,
    val currentEpisode: Int = 0,
    val status: String = "active",
    val dateAdded: String? = null,
    val lastChecked: String? = null,
    val downloadedEpisodes: List<Any> = emptyList(),
    val tmdbId: Int? = null,
    val tmdbName: String? = null,
    val tmdbOverview: String? = null,
    val firstAirDate: String? = null,
    val nextEpisodeAirDate: String? = null
)

data class ShowsResponse(
    val shows: List<Show> = emptyList()
)

data class AddShowRequest(
    val tmdbId: Int? = null,
    val name: String,
    val currentSeason: Int = 1,
    val currentEpisode: Int = 0,
    val status: String = "active"
)

data class UpdateShowRequest(
    val currentSeason: Int? = null,
    val currentEpisode: Int? = null,
    val status: String? = null
)

data class AddShowResponse(
    val success: Boolean = false,
    val show: Show? = null,
    val catchUpEpisodes: Int = 0,
    val message: String? = null
)

data class TmdbSearchResult(
    val id: Int = 0,
    val name: String = "",
    @SerializedName("original_name") val originalName: String? = null,
    val overview: String? = null,
    @SerializedName("firstAirDate") val firstAirDate: String? = null,
    @SerializedName("posterPath") val posterPath: String? = null,
    @SerializedName("voteAverage") val voteAverage: Float? = null
)

data class TmdbSearchResponse(
    val results: List<TmdbSearchResult> = emptyList()
)

data class TmdbConfig(
    val configured: Boolean = false,
    val hasKey: Boolean = false,
    val keyLength: Int = 0
)

data class TmdbApiKeyRequest(val apiKey: String)

data class DownloadSeasonRequest(
    val tmdbId: Int? = null,
    val showName: String,
    val season: Int
)

data class DownloadSeasonResponse(
    val success: Boolean = false,
    val sessionId: String? = null,
    val message: String? = null
)

data class DownloadProgressResponse(
    val status: String = "not_found",
    val message: String? = null,
    val progress: Int = 0,
    val downloads: List<Any> = emptyList(),
    val seasonPack: Boolean = false,
    val success: Boolean = false,
    val error: Boolean = false
)

data class AutoCheckConfig(
    val enabled: Boolean = false,
    val scheduledChecks: Int = 0,
    val type: String = "targeted"
)

data class AutoCheckUpdateRequest(
    val enabled: Boolean
)

data class QueueItem(
    val showName: String = "",
    val season: Int = 0,
    val episode: Int = 0,
    val searchType: String = "episode",
    val queuedAt: String = ""
)

data class QueueStatusResponse(
    val queueLength: Int = 0,
    val processing: Boolean = false,
    val lastSearchTime: Long = 0,
    val nextSearchIn: Long = 0,
    val queuedItems: List<QueueItem> = emptyList(),
    val searchInterval: Int = 45
)

data class ClearQueueResponse(
    val success: Boolean = false,
    val message: String? = null,
    val clearedCount: Int = 0
)

data class CheckEpisodesResponse(
    val success: Boolean = false,
    val message: String = "",
    val downloads: List<Any> = emptyList()
)

data class ApiResponse<T>(
    val success: Boolean = true,
    val data: T? = null,
    val message: String? = null,
    val error: String? = null
)
