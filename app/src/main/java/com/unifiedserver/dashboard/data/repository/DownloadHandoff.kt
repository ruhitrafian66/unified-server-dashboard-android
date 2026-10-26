package com.unifiedserver.dashboard.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class DownloadRequest(
    val query: String,
    val autoPick: Boolean = false,
    val title: String? = null,
    val year: String? = null,
    val season: Int? = null,
    val policy: AutoDownloadPolicy = AutoDownloadPolicy()
)

/** Navigation carries the query; only Downloads owns the actual server search. */
@Singleton
class DownloadHandoff @Inject constructor() {
    val pendingQuery = MutableStateFlow<DownloadRequest?>(null)
    fun request(query: String, autoPick: Boolean = false) {
        pendingQuery.value = DownloadRequest(query, autoPick)
    }
    fun request(request: DownloadRequest) { pendingQuery.value = request }
    fun consume() { pendingQuery.value = null }
}
