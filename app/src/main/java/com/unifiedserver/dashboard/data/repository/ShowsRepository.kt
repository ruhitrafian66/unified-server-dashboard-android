package com.unifiedserver.dashboard.data.repository

import com.unifiedserver.dashboard.data.ApiService
import com.unifiedserver.dashboard.data.PreferencesManager
import com.unifiedserver.dashboard.data.models.*
import javax.inject.Inject

open class ShowsRepository @Inject constructor(
    private val preferencesManager: PreferencesManager
) {

    suspend fun getShows(): Result<List<Show>> = apiResult {
        val service = buildApiService(preferencesManager)
        val response = service.getShows()
        if (response.isSuccessful) {
            response.body()?.shows ?: emptyList()
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun addShow(request: AddShowRequest): Result<AddShowResponse> = apiResult {
        val service = buildApiService(preferencesManager)
        val response = service.addShow(request)
        if (response.isSuccessful) {
            (response.body() ?: throw Exception("No response body")).also {
                check(it.success) { it.message ?: "Server rejected adding the show." }
            }
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun updateShow(id: String, request: UpdateShowRequest): Result<Unit> = apiResult {
        val service = buildApiService(preferencesManager)
        val response = service.updateShow(id, request)
        response.requireSuccess()
    }

    suspend fun deleteShow(id: String): Result<Unit> = apiResult {
        val service = buildApiService(preferencesManager)
        val response = service.deleteShow(id)
        response.requireSuccess()
    }

    suspend fun checkEpisodes(): Result<CheckEpisodesResponse> = apiResult {
        val service = buildApiService(preferencesManager)
        val response = service.checkEpisodes()
        if (response.isSuccessful) {
            (response.body() ?: throw Exception("Empty episode-check response")).also {
                check(it.success) { it.message.ifBlank { "Server rejected the episode check." } }
            }
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    open suspend fun getAutoCheckConfig(): Result<AutoCheckConfig> = apiResult {
        val service = buildApiService(preferencesManager)
        val response = service.getAutoCheckConfig()
        if (response.isSuccessful) {
            response.body() ?: AutoCheckConfig()
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun setAutoCheckEnabled(enabled: Boolean): Result<Unit> = apiResult {
        val service = buildApiService(preferencesManager)
        val response = service.setAutoCheckConfig(AutoCheckUpdateRequest(enabled))
        response.requireSuccess()
    }

    suspend fun getQueueStatus(): Result<QueueStatusResponse> = apiResult {
        val service = buildApiService(preferencesManager)
        val response = service.getQueueStatus()
        if (response.isSuccessful) {
            response.body() ?: QueueStatusResponse()
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun clearQueue(): Result<ClearQueueResponse> = apiResult {
        val service = buildApiService(preferencesManager)
        val response = service.clearQueue()
        if (response.isSuccessful) {
            (response.body() ?: throw Exception("Empty queue response")).also {
                check(it.success) { it.message ?: "Server rejected clearing the queue." }
            }
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun searchShows(query: String): Result<List<TmdbSearchResult>> = apiResult {
        val service = buildApiService(preferencesManager)
        val response = service.searchShows(query)
        if (response.isSuccessful) {
            response.body()?.results ?: emptyList()
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun getTmdbConfig(): Result<TmdbConfig> = apiResult {
        val service = buildApiService(preferencesManager)
        val response = service.getTmdbConfig()
        if (response.isSuccessful) {
            response.body() ?: TmdbConfig()
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun setTmdbApiKey(apiKey: String): Result<Unit> = apiResult {
        val service = buildApiService(preferencesManager)
        val response = service.setTmdbApiKey(TmdbApiKeyRequest(apiKey))
        response.requireSuccess()
    }

    suspend fun downloadSeason(tmdbId: Int?, season: Int, showName: String): Result<DownloadSeasonResponse> = apiResult {
        val service = buildApiService(preferencesManager)
        val response = service.downloadSeason(DownloadSeasonRequest(tmdbId, showName, season))
        if (response.isSuccessful) {
            (response.body() ?: throw Exception("Empty season-download response")).also {
                check(it.success) { it.message ?: "Server rejected the season download." }
            }
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun getDownloadProgress(sessionId: String): Result<DownloadProgressResponse> = apiResult {
        val service = buildApiService(preferencesManager)
        val response = service.getDownloadProgress(sessionId)
        if (response.isSuccessful) {
            response.body() ?: DownloadProgressResponse()
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }
}
