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

    @POST("api/qbittorrent/torrents/add-advanced")
    suspend fun addTorrentAdvanced(@Body request: AddTorrentAdvancedRequest): Response<ApiResponse<Any>>

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

    // ─── qBittorrent - Auto-Stop ──────────────────────────────────────────────
    @GET("api/qbittorrent/auto-stop/config")
    suspend fun getAutoStopConfig(): Response<AutoStopConfig>

    @POST("api/qbittorrent/auto-stop/config")
    suspend fun setAutoStopConfig(@Body config: AutoStopConfig): Response<ApiResponse<Any>>

    // ─── OMV / System ────────────────────────────────────────────────────────
    @GET("api/omv/system")
    suspend fun getSystemInfo(): Response<SystemInfo>

    // Returns { services: [...] }
    @GET("api/omv/services")
    suspend fun getServices(): Response<ServicesResponse>

    // Returns { disks: [...] }
    @GET("api/omv/disks")
    suspend fun getDisks(): Response<DisksResponse>

    @POST("api/omv/power/{action}")
    suspend fun powerAction(@Path("action") action: String): Response<ApiResponse<Any>>

    // ─── CPU Monitoring ──────────────────────────────────────────────────────
    @GET("api/omv/cpu-history")
    suspend fun getCpuHistory(@Query("hours") hours: Int = 1): Response<CpuHistoryResponse>

    // ─── Docker ──────────────────────────────────────────────────────────────
    // Returns { containers: [...] }
    @GET("api/docker/containers")
    suspend fun getDockerContainers(): Response<DockerContainersResponse>

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

    @POST("api/shows/check")
    suspend fun checkEpisodes(): Response<CheckEpisodesResponse>

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
}
