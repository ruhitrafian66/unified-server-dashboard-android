package com.unifiedserver.dashboard.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class DownloadRequest(
    val query: String,
    val autoPick: Boolean = false   // true = auto-pick best 1080p+ source; false = open search sheet
)

/** Navigation carries the query; only Downloads owns the actual server search. */
@Singleton
class DownloadHandoff @Inject constructor() {
    val pendingQuery = MutableStateFlow<DownloadRequest?>(null)
    fun request(query: String, autoPick: Boolean = false) {
        pendingQuery.value = DownloadRequest(query, autoPick)
    }
    fun consume() { pendingQuery.value = null }
}
