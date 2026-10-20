package com.unifiedserver.dashboard.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/** Navigation carries the query; only Downloads owns the actual server search. */
@Singleton
class DownloadHandoff @Inject constructor() {
    val pendingQuery = MutableStateFlow<String?>(null)
    fun request(query: String) { pendingQuery.value = query }
    fun consume() { pendingQuery.value = null }
}
