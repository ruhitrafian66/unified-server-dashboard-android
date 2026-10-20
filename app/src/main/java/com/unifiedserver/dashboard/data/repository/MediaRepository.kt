package com.unifiedserver.dashboard.data.repository

import com.unifiedserver.dashboard.data.OmdbApiService
import com.unifiedserver.dashboard.data.PreferencesManager
import com.unifiedserver.dashboard.data.TmdbApiService
import com.unifiedserver.dashboard.data.models.*
import kotlinx.coroutines.flow.first
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

open class MediaRepository @Inject constructor(private val preferencesManager: PreferencesManager) {
    private val tmdb by lazy { service("https://api.themoviedb.org/", TmdbApiService::class.java) }
    private val omdb by lazy { service("https://www.omdbapi.com/", OmdbApiService::class.java) }

    private fun <T> service(url: String, type: Class<T>): T = Retrofit.Builder()
        .baseUrl(url)
        .client(BackendClient.httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(type)

    private fun <T> Response<T>.requiredBody(): T {
        check(isSuccessful) { "Media service request failed (HTTP ${code()})." }
        return body() ?: error("Media service returned an empty response.")
    }

    open suspend fun getTrendingTv(): Result<List<TmdbMediaItem>> = apiResult {
        tmdb.getTrendingTv(requireApiKey()).requiredBody().results.map { it.copy(mediaType = "tv") }
    }

    open suspend fun getTrendingMovies(): Result<List<TmdbMediaItem>> = apiResult {
        tmdb.getTrendingMovies(requireApiKey()).requiredBody().results.map { it.copy(mediaType = "movie") }
    }

    suspend fun searchTv(query: String, page: Int = 1): Result<TmdbTrendingResponse> = apiResult {
        val result = tmdb.searchTv(requireApiKey(), query, page).requiredBody()
        result.copy(results = result.results.map { it.copy(mediaType = "tv") })
    }

    open suspend fun searchMovies(query: String, page: Int = 1): Result<TmdbTrendingResponse> = apiResult {
        val result = tmdb.searchMovies(requireApiKey(), query, page).requiredBody()
        result.copy(results = result.results.map { it.copy(mediaType = "movie") })
    }

    suspend fun searchAll(query: String, page: Int = 1): Result<TmdbTrendingResponse> = apiResult {
        val result = tmdb.searchMulti(requireApiKey(), query, page).requiredBody()
        TmdbTrendingResponse(result.page, result.results.filter { it.mediaType in listOf("tv", "movie") },
            result.totalPages, result.totalResults)
    }

    open suspend fun getMovieDetail(id: Int): Result<TmdbMovieDetail> = apiResult {
        tmdb.getMovieDetail(id, requireApiKey()).requiredBody()
    }

    suspend fun getTvDetail(id: Int): Result<TmdbTvDetail> = apiResult {
        tmdb.getTvDetail(id, requireApiKey()).requiredBody()
    }

    suspend fun getImdbRating(imdbId: String): Result<ImdbRatingResponse> = apiResult {
        val key = preferencesManager.omdbApiKey.first()
        check(key.isNotBlank()) { "Optional OMDb key is not configured." }
        val result = omdb.getRatingByImdbId(key, imdbId).requiredBody()
        check(result.response == "True") { "IMDb rating is unavailable." }
        result
    }

    private suspend fun requireApiKey(): String {
        val key = preferencesManager.tmdbApiKey.first()
        check(key.isNotBlank()) { "Add your TMDB key in Settings to browse media." }
        return key
    }
}
