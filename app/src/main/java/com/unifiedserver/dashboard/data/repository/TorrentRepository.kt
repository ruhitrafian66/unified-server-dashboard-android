package com.unifiedserver.dashboard.data.repository

import com.unifiedserver.dashboard.data.ApiService
import com.unifiedserver.dashboard.data.PreferencesManager
import com.unifiedserver.dashboard.data.models.*
import javax.inject.Inject

open class TorrentRepository @Inject constructor(
    private val preferencesManager: PreferencesManager
) {

    open suspend fun getTorrents(): Result<List<Torrent>> = apiResult {
        val service = buildApiService(preferencesManager)
        val response = service.getTorrents()
        if (response.isSuccessful) {
            response.body() ?: emptyList()
        } else {
            throw response.httpFailure()
        }
    }

    open suspend fun addTorrent(urls: String): Result<Unit> = apiResult {
        val service = buildApiService(preferencesManager)
        val response = service.addTorrent(AddTorrentRequest(urls))
        response.requireSuccess()
    }

    suspend fun pauseTorrents(hashes: String): Result<Unit> = apiResult {
        val service = buildApiService(preferencesManager)
        val response = service.torrentAction("pause", TorrentActionRequest(hashes))
        response.requireSuccess()
    }

    suspend fun resumeTorrents(hashes: String): Result<Unit> = apiResult {
        val service = buildApiService(preferencesManager)
        val response = service.torrentAction("resume", TorrentActionRequest(hashes))
        response.requireSuccess()
    }

    suspend fun deleteTorrents(hashes: String, deleteFiles: Boolean = false): Result<Unit> = apiResult {
        val service = buildApiService(preferencesManager)
        val response = service.torrentAction("delete", TorrentActionRequest(hashes, deleteFiles))
        response.requireSuccess()
    }

    open suspend fun startSearch(pattern: String, plugins: String = "enabled", category: String = "all"): Result<Int> = apiResult {
        val service = buildApiService(preferencesManager)
        val response = service.startSearch(SearchStartRequest(pattern, plugins, category))
        if (response.isSuccessful) {
            response.body()?.id ?: throw Exception("No search ID returned")
        } else {
            throw response.httpFailure()
        }
    }

    suspend fun getSearchStatus(id: Int): Result<SearchStatusItem> = apiResult {
        val service = buildApiService(preferencesManager)
        val response = service.getSearchStatus(id)
        if (response.isSuccessful) {
            val statusList = response.body()
            when {
                statusList == null -> throw Exception("Response body is null")
                statusList.isEmpty() -> throw Exception("Search ID $id not found on server")
                else -> statusList.first()
            }
        } else {
            throw response.httpFailure()
        }
    }

    open suspend fun getSearchResults(id: Int, offset: Int = 0): Result<SearchResultsResponse> = apiResult {
        val service = buildApiService(preferencesManager)
        val response = service.getSearchResults(id, offset = offset)
        if (response.isSuccessful) {
            val results = response.body()
            when {
                results == null -> throw Exception("Response body is null for search results")
                results.results.isEmpty() && results.status.isBlank() -> 
                    throw Exception("Empty search results for ID $id")
                else -> results
            }
        } else {
            throw response.httpFailure()
        }
    }

    open suspend fun stopSearch(id: Int): Result<Unit> = apiResult {
        val service = buildApiService(preferencesManager)
        val response = service.stopSearch(SearchStopRequest(id))
        response.requireSuccess()
    }

    open suspend fun getAutoStopConfig(): Result<AutoStopConfig> = apiResult {
        val service = buildApiService(preferencesManager)
        val response = service.getAutoStopConfig()
        if (response.isSuccessful) {
            response.body() ?: AutoStopConfig()
        } else {
            throw response.httpFailure()
        }
    }

    open suspend fun setAutoStopConfig(config: AutoStopConfig): Result<Unit> = apiResult {
        val service = buildApiService(preferencesManager)
        val response = service.setAutoStopConfig(config)
        response.requireSuccess()
    }
}
