package com.unifiedserver.dashboard.data

import com.unifiedserver.dashboard.data.models.ImdbRatingResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * OMDb API (Open Movie Database) – free tier, returns IMDB rating by IMDB ID.
 * Base URL: https://www.omdbapi.com/
 * Free API key can be obtained at: https://www.omdbapi.com/apikey.aspx
 * We use the public endpoint with an apikey query param.
 */
interface OmdbApiService {

    @GET("/")
    suspend fun getRatingByImdbId(
        @Query("apikey") apiKey: String,
        @Query("i") imdbId: String,
        @Query("type") type: String? = null  // "movie" or "series"
    ): Response<ImdbRatingResponse>
}
