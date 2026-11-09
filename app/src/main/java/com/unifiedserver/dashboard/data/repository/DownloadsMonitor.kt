package com.unifiedserver.dashboard.data.repository

import com.unifiedserver.dashboard.data.models.Torrent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The app-wide qBittorrent list behind the downloads bar. Anything that adds or
 * changes a torrent calls [refresh] so the bar reflects it at once.
 */
@Singleton
open class DownloadsMonitor @Inject constructor(private val repository: TorrentRepository) {
    private val _torrents = MutableStateFlow<List<Torrent>>(emptyList())
    val torrents = _torrents.asStateFlow()
    private val refreshing = Mutex()

    /** Keeps the last list on failure; connection problems are shown on Home. */
    open suspend fun refresh() {
        if (!refreshing.tryLock()) return
        try {
            repository.getTorrents().onSuccess { _torrents.value = it }
        } finally {
            refreshing.unlock()
        }
    }

    fun clear() { _torrents.value = emptyList() }
}
