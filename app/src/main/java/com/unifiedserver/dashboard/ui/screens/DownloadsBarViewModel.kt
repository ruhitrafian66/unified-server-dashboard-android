package com.unifiedserver.dashboard.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unifiedserver.dashboard.data.PreferencesManager
import com.unifiedserver.dashboard.data.models.Torrent
import com.unifiedserver.dashboard.data.repository.DownloadsMonitor
import com.unifiedserver.dashboard.data.repository.TorrentRepository
import com.unifiedserver.dashboard.ui.utils.isDownloading
import com.unifiedserver.dashboard.ui.utils.isStopped
import com.unifiedserver.dashboard.ui.utils.prettyTorrentName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DownloadsBarState(
    /** Torrents still fetching data, shown in the bar. */
    val active: List<Torrent> = emptyList(),
    /** Incomplete but stopped: listed in the sheet so they can be started again. */
    val stoppedIncomplete: List<Torrent> = emptyList(),
    /** Hash of the torrent with an action in flight. */
    val pendingHash: String? = null,
    val message: String? = null
)

@HiltViewModel
class DownloadsBarViewModel @Inject constructor(
    private val monitor: DownloadsMonitor,
    private val repository: TorrentRepository,
    preferences: PreferencesManager
) : ViewModel() {
    private val pending = MutableStateFlow<String?>(null)
    private val message = MutableStateFlow<String?>(null)

    val state: StateFlow<DownloadsBarState> = combine(monitor.torrents, pending, message) { torrents, hash, msg ->
        DownloadsBarState(
            active = torrents.filter { it.isDownloading },
            stoppedIncomplete = torrents.filter { it.isStopped && it.progress < 1f },
            pendingHash = hash,
            message = msg
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, DownloadsBarState())

    init {
        // A different server must not show the previous server's downloads.
        viewModelScope.launch {
            preferences.serverUrl.distinctUntilChanged().drop(1).collect {
                monitor.clear()
                monitor.refresh()
            }
        }
    }

    fun refresh() { viewModelScope.launch { monitor.refresh() } }

    fun stop(torrent: Torrent) = act(torrent, "Stopped ${prettyTorrentName(torrent.name)}") { repository.pauseTorrents(torrent.hash) }
    fun start(torrent: Torrent) = act(torrent, "Started ${prettyTorrentName(torrent.name)}") { repository.resumeTorrents(torrent.hash) }
    fun remove(torrent: Torrent, deleteFiles: Boolean) =
        act(torrent, "Removed ${prettyTorrentName(torrent.name)}") { repository.deleteTorrents(torrent.hash, deleteFiles) }

    fun clearMessage() { message.value = null }

    private fun act(torrent: Torrent, done: String, block: suspend () -> Result<Unit>) {
        if (pending.value != null) return
        pending.value = torrent.hash
        viewModelScope.launch {
            block().onSuccess { message.value = done }
                .onFailure { message.value = "Error: ${it.message}" }
            monitor.refresh()
            pending.value = null
        }
    }
}
