package com.unifiedserver.dashboard.data.repository

import com.unifiedserver.dashboard.data.ApiService
import com.unifiedserver.dashboard.data.PreferencesManager
import com.unifiedserver.dashboard.data.models.*
import javax.inject.Inject

class ShowsRepository @Inject constructor(
    private val apiService: ApiService,
    private val preferencesManager: PreferencesManager
) {

    suspend fun getShows(): Result<List<Show>> = runCatching {
        val service = buildApiService(preferencesManager)
        val response = service.getShows()
        if (response.isSuccessful) {
            response.body()?.shows ?: emptyList()
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun addShow(request: AddShowRequest): Result<AddShowResponse> = runCatching {
        val service = buildApiService(preferencesManager)
        val response = service.addShow(request)
        if (response.isSuccessful) {
            response.body() ?: throw Exception("No response body")
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun updateShow(id: String, request: UpdateShowRequest): Result<Unit> = runCatching {
        val service = buildApiService(preferencesManager)
        val response = service.updateShow(id, request)
        if (!response.isSuccessful) {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun deleteShow(id: String): Result<Unit> = runCatching {
        val service = buildApiService(preferencesManager)
        val response = service.deleteShow(id)
        if (!response.isSuccessful) {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun checkEpisodes(): Result<CheckEpisodesResponse> = runCatching {
        val service = buildApiService(preferencesManager)
        val response = service.checkEpisodes()
        if (response.isSuccessful) {
            response.body() ?: CheckEpisodesResponse()
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun getAutoCheckConfig(): Result<AutoCheckConfig> = runCatching {
        val service = buildApiService(preferencesManager)
        val response = service.getAutoCheckConfig()
        if (response.isSuccessful) {
            response.body() ?: AutoCheckConfig()
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun setAutoCheckEnabled(enabled: Boolean): Result<Unit> = runCatching {
        val service = buildApiService(preferencesManager)
        val response = service.setAutoCheckConfig(AutoCheckUpdateRequest(enabled))
        if (!response.isSuccessful) {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun getQueueStatus(): Result<QueueStatusResponse> = runCatching {
        val service = buildApiService(preferencesManager)
        val response = service.getQueueStatus()
        if (response.isSuccessful) {
            response.body() ?: QueueStatusResponse()
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun clearQueue(): Result<ClearQueueResponse> = runCatching {
        val service = buildApiService(preferencesManager)
        val response = service.clearQueue()
        if (response.isSuccessful) {
            response.body() ?: ClearQueueResponse()
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun searchShows(query: String): Result<List<TmdbSearchResult>> = runCatching {
        val service = buildApiService(preferencesManager)
        val response = service.searchShows(query)
        if (response.isSuccessful) {
            response.body()?.results ?: emptyList()
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun getTmdbConfig(): Result<TmdbConfig> = runCatching {
        val service = buildApiService(preferencesManager)
        val response = service.getTmdbConfig()
        if (response.isSuccessful) {
            response.body() ?: TmdbConfig()
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun setTmdbApiKey(apiKey: String): Result<Unit> = runCatching {
        val service = buildApiService(preferencesManager)
        val response = service.setTmdbApiKey(TmdbApiKeyRequest(apiKey))
        if (!response.isSuccessful) {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun downloadSeason(tmdbId: Int?, season: Int, showName: String): Result<DownloadSeasonResponse> = runCatching {
        val service = buildApiService(preferencesManager)
        val response = service.downloadSeason(DownloadSeasonRequest(tmdbId, showName, season))
        if (response.isSuccessful) {
            response.body() ?: DownloadSeasonResponse()
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun getDownloadProgress(sessionId: String): Result<DownloadProgressResponse> = runCatching {
        val service = buildApiService(preferencesManager)
        val response = service.getDownloadProgress(sessionId)
        if (response.isSuccessful) {
            response.body() ?: DownloadProgressResponse()
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }
}
