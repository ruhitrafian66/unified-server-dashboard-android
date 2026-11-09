package com.unifiedserver.dashboard.data.models

import com.google.gson.annotations.SerializedName

// ─── Health ──────────────────────────────────────────────────────────────────

data class HealthResponse(
    val status: String = "unknown",
    val timestamp: String? = null,
    val platform: String? = null,
    val nodeVersion: String? = null,
    val uptime: Long? = null,
    /** Short SHA of the backend build that is running. */
    val commit: String? = null
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
    @SerializedName("seeding_time") val seedingTime: Long = 0,
    @SerializedName("added_on") val addedOn: Long = 0
)

data class AddTorrentRequest(val urls: String)

data class TorrentActionRequest(
    val hashes: String,
    val deleteFiles: Boolean = false
)

/**
 * Seeding limits, stored in qBittorrent's own preferences by the backend.
 * A null limit is off. Enabled with both limits off means "stop as soon as complete".
 */
data class AutoStopConfig(
    val enabled: Boolean = false,
    /** Minutes to keep seeding after completion. */
    val seedTimeLimit: Int? = null,
    val ratioLimit: Float? = null
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

// ─── System overview (/api/system/overview) ──────────────────────────────────
// One call per poll; sections that failed on the server are listed in `errors`.

data class SystemOverview(
    val timestamp: String? = null,
    val cpu: CpuNow? = null,
    val memory: MemoryUsage? = null,
    val uptime: Long? = null,
    val load: List<Float>? = null,
    val temperature: Float? = null,
    val disks: OverviewDisks? = null,
    val services: List<ServiceStatus>? = null,
    val containers: List<DockerContainer>? = null,
    val torrentClient: TorrentClientStatus? = null,
    val deploy: DeployInfo? = null,
    val errors: Map<String, String>? = null
)

data class CpuNow(val percent: Float? = null, val cores: Int? = null)

data class MemoryUsage(
    val total: Long = 0,
    val used: Long = 0,
    val percent: Float = 0f,
    val swapUsed: Long = 0,
    val swapTotal: Long = 0
)

data class OverviewDisks(val root: DiskUsage? = null, val storage: DiskUsage? = null)

data class DiskUsage(
    val mountPoint: String = "",
    val size: Long = 0,
    val used: Long = 0,
    val available: Long = 0,
    val percent: Float = 0f
)

data class TorrentClientStatus(
    val url: String? = null,
    /** connected | firewalled | disconnected */
    val connection: String = "",
    val dhtNodes: Int = 0,
    val dlSpeed: Long = 0,
    val upSpeed: Long = 0
)

data class DeployInfo(val commit: String? = null, val startedAt: String? = null, val node: String? = null)

data class ServiceStatus(
    val service: String = "",
    val status: String = "inactive",
    val sub: String? = null
) {
    val running: Boolean get() = status == "active"
}

// ─── CPU Monitoring ──────────────────────────────────────────────────────────

data class CpuSample(
    val t: Long = 0,
    val v: Float = 0f
)

data class CpuHistoryResponse(
    val samples: List<CpuSample> = emptyList(),
    val interval: Int = 60
)

data class CpuMetrics(
    val currentUsage: Float = 0f,
    val averageUsage: Float = 0f,
    val maxUsage: Float = 0f,
    val minUsage: Float = 0f,
    val dataPoints: List<CpuDataPoint> = emptyList(),
    @SerializedName("lastUpdated") val lastUpdated: Long = 0,
    val cpuCount: Int = 0
)

data class CpuDataPoint(
    val timestamp: Long = 0,
    val usage: Float = 0f
)

data class CpuMetricsResponse(
    val success: Boolean = true,
    val data: CpuMetrics = CpuMetrics(),
    val message: String? = null
)

// ─── Docker ──────────────────────────────────────────────────────────────────

data class DockerContainer(
    val name: String = "",
    val state: String = "",
    val status: String = "",
    val image: String = "",
    val running: Boolean = false,
    /** From the container health check; null when it has none. */
    val healthy: Boolean? = null
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
    /** Aired episodes not found within the server's retry window; search these manually. */
    val missedEpisodes: List<EpisodeRef>? = null,
    /** An aired episode the server is still retrying. */
    val pendingEpisode: EpisodeRef? = null,
    val tmdbId: Int? = null,
    val tmdbName: String? = null,
    val tmdbOverview: String? = null,
    val firstAirDate: String? = null,
    val posterPath: String? = null,
    val nextEpisodeAirDate: String? = null
)

data class EpisodeRef(
    val season: Int = 0,
    val episode: Int = 0,
    val airDate: String? = null,
    val lastTried: String? = null,
    val tries: Int = 0
) {
    val code: String get() = episodeCode(season, episode)
}

fun episodeCode(season: Int, episode: Int) =
    "S${season.toString().padStart(2, '0')}E${episode.toString().padStart(2, '0')}"

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
    val sweepIntervalHours: Int? = null,
    val retryWindowDays: Int? = null,
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

/** POST /api/shows/check starts a background sweep; poll [CheckStatusResponse] for the outcome. */
data class CheckEpisodesResponse(
    val success: Boolean = false,
    val started: Boolean = false,
    val running: Boolean = false,
    val message: String = ""
)

data class CheckStatusResponse(
    val running: Boolean = false,
    val lastSweep: SweepSummary? = null,
    val inFlight: Int = 0
)

data class SweepSummary(
    val at: String? = null,
    val reason: String? = null,
    val checked: Int = 0,
    val downloads: List<Any> = emptyList(),
    val missed: List<Any> = emptyList(),
    val errors: List<Any> = emptyList()
)

data class ApiResponse<T>(
    val success: Boolean = true,
    val data: T? = null,
    val message: String? = null,
    val error: String? = null
)

// ─── Media Discovery (TMDB, proxied by the backend at /api/tmdb) ───────────────────────────────────────

data class TmdbMediaItem(
    val id: Int = 0,
    val title: String = "",           // movies use "title"
    val name: String = "",            // TV shows use "name"
    val overview: String = "",
    @SerializedName("poster_path") val posterPath: String? = null,
    @SerializedName("backdrop_path") val backdropPath: String? = null,
    @SerializedName("release_date") val releaseDate: String? = null,       // movies
    @SerializedName("first_air_date") val firstAirDate: String? = null,    // TV
    @SerializedName("vote_average") val voteAverage: Float = 0f,
    @SerializedName("vote_count") val voteCount: Int = 0,
    @SerializedName("media_type") val mediaType: String? = null,           // "movie" or "tv"
    val popularity: Float = 0f,
    @SerializedName("genre_ids") val genreIds: List<Int> = emptyList()
) {
    val displayTitle: String get() = title.ifBlank { name }
    val displayDate: String get() = releaseDate ?: firstAirDate ?: ""
    val year: String get() = displayDate.take(4)
    val posterUrl: String? get() = posterPath?.let { "https://image.tmdb.org/t/p/w342$it" }
    val backdropUrl: String? get() = backdropPath?.let { "https://image.tmdb.org/t/p/w780$it" }
}

data class TmdbTrendingResponse(
    val page: Int = 1,
    val results: List<TmdbMediaItem> = emptyList(),
    @SerializedName("total_pages") val totalPages: Int = 1,
    @SerializedName("total_results") val totalResults: Int = 0
)

/** GET /api/tmdb/movies/streaming: films with a recent digital release. */
data class TmdbDiscoverResponse(
    val page: Int = 1,
    val results: List<TmdbMediaItem> = emptyList(),
    @SerializedName("total_pages") val totalPages: Int = 1,
    @SerializedName("total_results") val totalResults: Int = 0,
    val windowDays: Int? = null
)

data class TmdbSearchMultiResponse(
    val page: Int = 1,
    val results: List<TmdbMediaItem> = emptyList(),
    @SerializedName("total_pages") val totalPages: Int = 1,
    @SerializedName("total_results") val totalResults: Int = 0
)

data class TmdbMovieDetail(
    val id: Int = 0,
    val title: String = "",
    val overview: String = "",
    val tagline: String = "",
    @SerializedName("poster_path") val posterPath: String? = null,
    @SerializedName("backdrop_path") val backdropPath: String? = null,
    @SerializedName("release_date") val releaseDate: String? = null,
    val runtime: Int? = null,
    @SerializedName("vote_average") val voteAverage: Float = 0f,
    @SerializedName("vote_count") val voteCount: Int = 0,
    val status: String = "",
    val genres: List<TmdbGenre> = emptyList(),
    @SerializedName("imdb_id") val imdbId: String? = null,
    val popularity: Float = 0f,
    @SerializedName("external_ids") val externalIds: TmdbExternalIds? = null
) {
    val year: String get() = releaseDate?.take(4) ?: ""
    val posterUrl: String? get() = posterPath?.let { "https://image.tmdb.org/t/p/w342$it" }
    val backdropUrl: String? get() = backdropPath?.let { "https://image.tmdb.org/t/p/w780$it" }
}

data class TmdbTvDetail(
    val id: Int = 0,
    val name: String = "",
    val overview: String = "",
    val tagline: String = "",
    @SerializedName("poster_path") val posterPath: String? = null,
    @SerializedName("backdrop_path") val backdropPath: String? = null,
    @SerializedName("first_air_date") val firstAirDate: String? = null,
    @SerializedName("last_air_date") val lastAirDate: String? = null,
    @SerializedName("number_of_seasons") val numberOfSeasons: Int = 0,
    @SerializedName("number_of_episodes") val numberOfEpisodes: Int = 0,
    @SerializedName("vote_average") val voteAverage: Float = 0f,
    @SerializedName("vote_count") val voteCount: Int = 0,
    val status: String = "",
    val genres: List<TmdbGenre> = emptyList(),
    @SerializedName("external_ids") val externalIds: TmdbExternalIds? = null
) {
    val year: String get() = firstAirDate?.take(4) ?: ""
    val posterUrl: String? get() = posterPath?.let { "https://image.tmdb.org/t/p/w342$it" }
    val backdropUrl: String? get() = backdropPath?.let { "https://image.tmdb.org/t/p/w780$it" }
}

data class TmdbGenre(
    val id: Int = 0,
    val name: String = ""
)

data class TmdbExternalIds(
    @SerializedName("imdb_id") val imdbId: String? = null
)

data class ImdbRatingResponse(
    @SerializedName("Title") val title: String = "",
    @SerializedName("Year") val year: String = "",
    @SerializedName("Rated") val rated: String = "",
    @SerializedName("Runtime") val runtime: String = "",
    @SerializedName("Genre") val genre: String = "",
    @SerializedName("Director") val director: String = "",
    @SerializedName("Actors") val actors: String = "",
    @SerializedName("Plot") val plot: String = "",
    @SerializedName("imdbRating") val imdbRating: String = "N/A",
    @SerializedName("imdbVotes") val imdbVotes: String = "",
    @SerializedName("imdbID") val imdbId: String = "",
    @SerializedName("Type") val type: String = "",
    @SerializedName("Response") val response: String = "False"
)

// ─── Media library (/api/library) ────────────────────────────────────────────
// One item per top-level entry of the server's download folder.

data class LibraryResponse(
    val root: String? = null,
    val scannedAt: String? = null,
    /** False when qBittorrent was unreachable during the scan (no torrent status). */
    val torrentsAvailable: Boolean = true,
    val disk: LibraryDisk? = null,
    val items: List<LibraryItem> = emptyList(),
    val junk: JunkSummary = JunkSummary()
)

data class LibraryDisk(val size: Long = 0, val available: Long = 0, val used: Long = 0) {
    val percent: Float get() = if (used + available > 0) used * 100f / (used + available) else 0f
}

data class JunkSummary(val count: Int = 0, val size: Long = 0, val items: List<JunkItem> = emptyList())

data class JunkItem(val name: String = "", val size: Long = 0, val reason: String? = null)

data class LibraryItem(
    val id: String = "",
    /** Name on disk. */
    val name: String = "",
    val isFile: Boolean = false,
    val size: Long = 0,
    val modified: String? = null,
    val fileCount: Int = 0,
    /** movie | tv | leftover (release folder without video) | other (not media, read-only) */
    val kind: String = "other",
    val title: String = "",
    val year: Int? = null,
    val quality: List<String> = emptyList(),
    /** List response only. */
    val seasonNumbers: List<Int>? = null,
    /** Detail response only. */
    val seasons: List<LibrarySeason>? = null,
    val episodeCount: Int = 0,
    /** Seasons the release name promises that are not on disk. */
    val missingSeasons: List<Int> = emptyList(),
    val extrasCount: Int? = null,
    val extras: List<LibraryFile>? = null,
    val subtitles: Int = 0,
    val videoCount: Int = 0,
    val mainFile: LibraryFile? = null,
    val torrent: LibraryTorrent? = null,
    val deletable: Boolean = false,
    /** Ids of other copies of the same title. */
    val duplicateOf: List<String> = emptyList(),
    val poster: String? = null,
    val tmdbId: Int? = null
) {
    val isMedia: Boolean get() = kind == "movie" || kind == "tv"
    val displayTitle: String get() = if (isMedia) title.ifBlank { name } else name
    val posterUrl: String? get() = poster?.let { "https://image.tmdb.org/t/p/w185$it" }
    val allSeasonNumbers: List<Int> get() = seasonNumbers ?: seasons?.map { it.season }.orEmpty()
}

data class LibrarySeason(val season: Int = 0, val episodes: List<LibraryEpisode> = emptyList(), val size: Long = 0)

data class LibraryEpisode(val episode: Int = 0, val file: String = "", val size: Long = 0)

data class LibraryFile(val file: String = "", val size: Long = 0)

data class LibraryTorrent(
    val hash: String = "",
    val state: String = "",
    val progress: Float = 0f,
    val ratio: Float = 0f,
    val dlspeed: Long = 0,
    val eta: Long = 0
)

data class LibraryDeleteResponse(val success: Boolean = false, val freed: Long = 0, val removedTorrent: Boolean = false)

data class LibraryCleanupResponse(val success: Boolean = false, val removed: Int = 0, val freed: Long = 0)
