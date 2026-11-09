package com.unifiedserver.dashboard.data.repository

import com.unifiedserver.dashboard.data.OmdbApiService
import com.unifiedserver.dashboard.data.PreferencesManager
import com.unifiedserver.dashboard.data.models.*
import kotlinx.coroutines.flow.first
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

/**
 * TMDB requests go through the backend's read-only /api/tmdb proxy, which adds the
 * server's API key; the key is configured once on the server for every device.
 * IMDb ratings use an optional personal OMDb key held on this device.
 */
open class MediaRepository @Inject constructor(private val preferencesManager: PreferencesManager) {
    private val omdb by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.omdbapi.com/")
            .client(BackendClient.httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(OmdbApiService::class.java)
    }

    private suspend fun api() = buildApiService(preferencesManager)

    open suspend fun getTrendingTv(): Result<List<TmdbMediaItem>> = apiResult {
        api().getTrendingTv().bodyOr { TmdbTrendingResponse() }.results.map { it.copy(mediaType = "tv") }
    }

    /** Movies newly out on digital/streaming (not still cinema-only), paged. */
    open suspend fun getNewOnStreamingMovies(page: Int = 1): Result<TmdbTrendingResponse> = apiResult {
        val result = api().getNewOnStreamingMovies(page).bodyOr { TmdbDiscoverResponse() }
        TmdbTrendingResponse(result.page, result.results.map { it.copy(mediaType = "movie") },
            result.totalPages, result.totalResults)
    }

    suspend fun searchTv(query: String, page: Int = 1): Result<TmdbTrendingResponse> = apiResult {
        val result = api().tmdbSearchTv(query, page).bodyOr { TmdbTrendingResponse() }
        result.copy(results = result.results.map { it.copy(mediaType = "tv") })
    }

    open suspend fun searchMovies(query: String, page: Int = 1): Result<TmdbTrendingResponse> = apiResult {
        val result = api().tmdbSearchMovies(query, page).bodyOr { TmdbTrendingResponse() }
        result.copy(results = result.results.map { it.copy(mediaType = "movie") })
    }

    suspend fun searchAll(query: String, page: Int = 1): Result<TmdbTrendingResponse> = apiResult {
        val result = api().tmdbSearchMulti(query, page).bodyOr { TmdbSearchMultiResponse() }
        TmdbTrendingResponse(result.page, result.results.filter { it.mediaType in listOf("tv", "movie") },
            result.totalPages, result.totalResults)
    }

    open suspend fun getMovieDetail(id: Int): Result<TmdbMovieDetail> = apiResult {
        val response = api().getMovieDetail(id)
        if (!response.isSuccessful) throw response.httpFailure()
        response.body() ?: error("Media service returned an empty response.")
    }

    open suspend fun getTvDetail(id: Int): Result<TmdbTvDetail> = apiResult {
        val response = api().getTvDetail(id)
        if (!response.isSuccessful) throw response.httpFailure()
        response.body() ?: error("Media service returned an empty response.")
    }

    suspend fun getImdbRating(imdbId: String): Result<ImdbRatingResponse> = apiResult {
        val key = preferencesManager.omdbApiKey.first()
        check(key.isNotBlank()) { "Optional OMDb key is not configured." }
        val response = omdb.getRatingByImdbId(key, imdbId)
        check(response.isSuccessful) { "IMDb rating request failed (HTTP ${response.code()})." }
        val result = response.body() ?: error("IMDb rating is unavailable.")
        check(result.response == "True") { "IMDb rating is unavailable." }
        result
    }
}
