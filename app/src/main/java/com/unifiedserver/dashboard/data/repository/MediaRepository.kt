package com.unifiedserver.dashboard.data.repository

import com.unifiedserver.dashboard.data.OmdbApiService
import com.unifiedserver.dashboard.data.PreferencesManager
import com.unifiedserver.dashboard.data.TmdbApiService
import com.unifiedserver.dashboard.data.models.*
import kotlinx.coroutines.flow.first
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MediaRepository @Inject constructor(
    private val preferencesManager: PreferencesManager
) {
    companion object {
        private const val TMDB_BASE_URL = "https://api.themoviedb.org/"
        private const val OMDB_BASE_URL = "https://www.omdbapi.com/"
        // Hardcoded free OMDb API key – get your own at https://www.omdbapi.com/apikey.aspx
        // Replace this with a real key or let the user configure it.
        private const val OMDB_API_KEY = "trilogy"   // fallback public key (limited)
    }

    private fun buildOkHttp(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.NONE
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private fun buildTmdbService(): TmdbApiService = Retrofit.Builder()
        .baseUrl(TMDB_BASE_URL)
        .client(buildOkHttp())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(TmdbApiService::class.java)

    private fun buildOmdbService(): OmdbApiService = Retrofit.Builder()
        .baseUrl(OMDB_BASE_URL)
        .client(buildOkHttp())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OmdbApiService::class.java)

    private suspend fun apiKey(): String = preferencesManager.tmdbApiKey.first()

    // ─── Trending ─────────────────────────────────────────────────────────────

    suspend fun getTrendingTv(): Result<List<TmdbMediaItem>> = runCatching {
        val key = requireApiKey()
        val response = buildTmdbService().getTrendingTv(key)
        if (response.isSuccessful) {
            response.body()?.results?.map { it.copy(mediaType = "tv") } ?: emptyList()
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun getTrendingMovies(): Result<List<TmdbMediaItem>> = runCatching {
        val key = requireApiKey()
        val response = buildTmdbService().getTrendingMovies(key)
        if (response.isSuccessful) {
            response.body()?.results?.map { it.copy(mediaType = "movie") } ?: emptyList()
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    // ─── Search ───────────────────────────────────────────────────────────────

    suspend fun searchTv(query: String): Result<List<TmdbMediaItem>> = runCatching {
        val key = requireApiKey()
        val response = buildTmdbService().searchTv(key, query)
        if (response.isSuccessful) {
            response.body()?.results?.map { it.copy(mediaType = "tv") } ?: emptyList()
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun searchMovies(query: String): Result<List<TmdbMediaItem>> = runCatching {
        val key = requireApiKey()
        val response = buildTmdbService().searchMovies(key, query)
        if (response.isSuccessful) {
            response.body()?.results?.map { it.copy(mediaType = "movie") } ?: emptyList()
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun searchAll(query: String): Result<List<TmdbMediaItem>> = runCatching {
        val key = requireApiKey()
        val response = buildTmdbService().searchMulti(key, query)
        if (response.isSuccessful) {
            response.body()?.results
                ?.filter { it.mediaType == "movie" || it.mediaType == "tv" }
                ?: emptyList()
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    // ─── Details ──────────────────────────────────────────────────────────────

    suspend fun getMovieDetail(id: Int): Result<TmdbMovieDetail> = runCatching {
        val key = requireApiKey()
        val response = buildTmdbService().getMovieDetail(id, key)
        if (response.isSuccessful) {
            response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun getTvDetail(id: Int): Result<TmdbTvDetail> = runCatching {
        val key = requireApiKey()
        val response = buildTmdbService().getTvDetail(id, key)
        if (response.isSuccessful) {
            response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    // ─── IMDB Rating via OMDb ─────────────────────────────────────────────────

    suspend fun getImdbRating(imdbId: String): Result<ImdbRatingResponse> = runCatching {
        val response = buildOmdbService().getRatingByImdbId(OMDB_API_KEY, imdbId)
        if (response.isSuccessful) {
            val body = response.body() ?: throw Exception("Empty response body")
            if (body.response != "True") throw Exception("IMDB ID not found")
            body
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private suspend fun requireApiKey(): String {
        val key = apiKey()
        if (key.isBlank()) throw Exception("TMDB API key not configured. Add it in Settings.")
        return key
    }
}
