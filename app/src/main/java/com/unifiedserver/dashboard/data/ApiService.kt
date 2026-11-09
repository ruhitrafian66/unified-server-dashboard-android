package com.unifiedserver.dashboard.data

import com.unifiedserver.dashboard.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ─── Health ──────────────────────────────────────────────────────────────
    @GET("api/health")
    suspend fun getHealth(): Response<HealthResponse>

    // ─── qBittorrent - Torrents ───────────────────────────────────────────────
    @GET("api/qbittorrent/torrents")
    suspend fun getTorrents(): Response<List<Torrent>>

    @POST("api/qbittorrent/torrents/add")
    suspend fun addTorrent(@Body request: AddTorrentRequest): Response<ApiResponse<Any>>

    @POST("api/qbittorrent/torrents/{action}")
    suspend fun torrentAction(
        @Path("action") action: String,
        @Body request: TorrentActionRequest
    ): Response<ApiResponse<Any>>

    // ─── qBittorrent - Search ─────────────────────────────────────────────────
    @POST("api/qbittorrent/search/start")
    suspend fun startSearch(@Body request: SearchStartRequest): Response<SearchStartResponse>

    // Backend returns a JSON array of status objects
    @GET("api/qbittorrent/search/status/{id}")
    suspend fun getSearchStatus(@Path("id") id: Int): Response<List<SearchStatusItem>>

    @GET("api/qbittorrent/search/results/{id}")
    suspend fun getSearchResults(
        @Path("id") id: Int,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): Response<SearchResultsResponse>

    @POST("api/qbittorrent/search/stop")
    suspend fun stopSearch(@Body request: SearchStopRequest): Response<ApiResponse<Any>>

    // ─── qBittorrent - Seeding limits (stored in qBittorrent's preferences) ────
    @GET("api/qbittorrent/auto-stop/config")
    suspend fun getAutoStopConfig(): Response<AutoStopConfig>

    @POST("api/qbittorrent/auto-stop/config")
    suspend fun setAutoStopConfig(@Body config: AutoStopConfig): Response<ApiResponse<Any>>

    // ─── System ──────────────────────────────────────────────────────────────
    // CPU, memory, disks, services, containers and torrent client in one call.
    @GET("api/system/overview")
    suspend fun getOverview(): Response<SystemOverview>

    @GET("api/system/cpu-history")
    suspend fun getCpuHistory(@Query("hours") hours: Int = 1): Response<CpuHistoryResponse>

    @POST("api/system/power/{action}")
    suspend fun powerAction(@Path("action") action: String): Response<ApiResponse<Any>>

    // ─── TV Shows ─────────────────────────────────────────────────────────────
    // Returns { shows: [...] }
    @GET("api/shows")
    suspend fun getShows(): Response<ShowsResponse>

    @POST("api/shows")
    suspend fun addShow(@Body request: AddShowRequest): Response<AddShowResponse>

    @PUT("api/shows/{id}")
    suspend fun updateShow(
        @Path("id") id: String,
        @Body request: UpdateShowRequest
    ): Response<ApiResponse<Show>>

    @DELETE("api/shows/{id}")
    suspend fun deleteShow(@Path("id") id: String): Response<ApiResponse<Any>>

    // Starts a background check of all active shows (HTTP 202).
    @POST("api/shows/check")
    suspend fun checkEpisodes(): Response<CheckEpisodesResponse>

    @GET("api/shows/check/status")
    suspend fun getCheckStatus(): Response<CheckStatusResponse>

    @GET("api/shows/config/auto-check")
    suspend fun getAutoCheckConfig(): Response<AutoCheckConfig>

    @POST("api/shows/config/auto-check")
    suspend fun setAutoCheckConfig(@Body request: AutoCheckUpdateRequest): Response<ApiResponse<Any>>

    @GET("api/shows/queue/status")
    suspend fun getQueueStatus(): Response<QueueStatusResponse>

    @POST("api/shows/queue/clear")
    suspend fun clearQueue(): Response<ClearQueueResponse>

    // Returns { results: [...] }
    @GET("api/shows/search/{query}")
    suspend fun searchShows(@Path("query") query: String): Response<TmdbSearchResponse>

    @GET("api/shows/config/tmdb")
    suspend fun getTmdbConfig(): Response<TmdbConfig>

    @POST("api/shows/config/tmdb")
    suspend fun setTmdbApiKey(@Body request: TmdbApiKeyRequest): Response<ApiResponse<Any>>

    @POST("api/shows/download-season")
    suspend fun downloadSeason(@Body request: DownloadSeasonRequest): Response<DownloadSeasonResponse>

    @GET("api/shows/download-progress/{sessionId}")
    suspend fun getDownloadProgress(@Path("sessionId") sessionId: String): Response<DownloadProgressResponse>

    // ─── TMDB proxy (the server holds the API key) ────────────────────────────
    @GET("api/tmdb/trending/tv/week")
    suspend fun getTrendingTv(): Response<TmdbTrendingResponse>

    // Films with a digital release in the last few months, newest-popular first.
    @GET("api/tmdb/movies/streaming")
    suspend fun getNewOnStreamingMovies(@Query("page") page: Int = 1): Response<TmdbDiscoverResponse>

    @GET("api/tmdb/search/multi")
    suspend fun tmdbSearchMulti(@Query("query") query: String, @Query("page") page: Int = 1): Response<TmdbSearchMultiResponse>

    @GET("api/tmdb/search/tv")
    suspend fun tmdbSearchTv(@Query("query") query: String, @Query("page") page: Int = 1): Response<TmdbTrendingResponse>

    @GET("api/tmdb/search/movie")
    suspend fun tmdbSearchMovies(@Query("query") query: String, @Query("page") page: Int = 1): Response<TmdbTrendingResponse>

    @GET("api/tmdb/movie/{id}")
    suspend fun getMovieDetail(
        @Path("id") id: Int,
        @Query("append_to_response") append: String = "external_ids"
    ): Response<TmdbMovieDetail>

    @GET("api/tmdb/tv/{id}")
    suspend fun getTvDetail(
        @Path("id") id: Int,
        @Query("append_to_response") append: String = "external_ids"
    ): Response<TmdbTvDetail>

    // ─── Media library (download folder) ──────────────────────────────────────
    // The server caches scans for 30 s; refresh=1 forces a rescan.
    @GET("api/library")
    suspend fun getLibrary(@Query("refresh") refresh: Int? = null): Response<LibraryResponse>

    @GET("api/library/items/{id}")
    suspend fun getLibraryItem(@Path("id") id: String): Response<LibraryItem>

    // Deletes the files and the matching torrent; refused for non-media folders.
    @DELETE("api/library/items/{id}")
    suspend fun deleteLibraryItem(@Path("id") id: String): Response<LibraryDeleteResponse>

    @POST("api/library/cleanup")
    suspend fun cleanupLibrary(): Response<LibraryCleanupResponse>
}
