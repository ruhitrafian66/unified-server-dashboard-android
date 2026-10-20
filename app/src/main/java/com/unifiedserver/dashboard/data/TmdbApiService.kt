package com.unifiedserver.dashboard.data

import com.unifiedserver.dashboard.data.models.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {

    // ─── Trending ─────────────────────────────────────────────────────────────
    @GET("3/trending/tv/week")
    suspend fun getTrendingTv(
        @Query("api_key") apiKey: String
    ): Response<TmdbTrendingResponse>

    @GET("3/trending/movie/week")
    suspend fun getTrendingMovies(
        @Query("api_key") apiKey: String
    ): Response<TmdbTrendingResponse>

    // ─── Search ───────────────────────────────────────────────────────────────
    @GET("3/search/multi")
    suspend fun searchMulti(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): Response<TmdbSearchMultiResponse>

    @GET("3/search/tv")
    suspend fun searchTv(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): Response<TmdbTrendingResponse>

    @GET("3/search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): Response<TmdbTrendingResponse>

    // ─── Details ──────────────────────────────────────────────────────────────
    @GET("3/movie/{movie_id}")
    suspend fun getMovieDetail(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("append_to_response") append: String = "external_ids"
    ): Response<TmdbMovieDetail>

    @GET("3/tv/{series_id}")
    suspend fun getTvDetail(
        @Path("series_id") seriesId: Int,
        @Query("api_key") apiKey: String,
        @Query("append_to_response") append: String = "external_ids"
    ): Response<TmdbTvDetail>
}
