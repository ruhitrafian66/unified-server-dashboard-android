package com.unifiedserver.dashboard.data.repository

import com.unifiedserver.dashboard.data.PreferencesManager
import com.unifiedserver.dashboard.data.models.AutoStopConfig
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

data class AutomationSettings(
    val serverUrl: String? = null,
    val autoStop: AutoStopConfig? = null,
    val autoCheck: Boolean? = null,
    val refreshing: Boolean = false,
    val autoStopError: String? = null,
    val autoCheckError: String? = null
)

/** One server-confirmed snapshot shared by Settings and Tracked shows. */
@Singleton
class AutomationSettingsRepository @Inject constructor(
    private val preferences: PreferencesManager,
    private val torrents: TorrentRepository,
    private val shows: ShowsRepository
) {
    private val _state = MutableStateFlow(AutomationSettings())
    val state = _state.asStateFlow()
    private val mutex = Mutex()

    private suspend fun selectServer(): String {
        val url = preferences.serverUrl.first()
        if (_state.value.serverUrl != url) _state.value = AutomationSettings(serverUrl = url)
        return url
    }

    private suspend fun verifyServer(expected: String): Boolean {
        val current = preferences.serverUrl.first()
        if (current == expected) return true
        _state.value = AutomationSettings(serverUrl = current,
            autoStopError = "Server changed. Reload settings.", autoCheckError = "Server changed. Reload settings.")
        return false
    }

    fun invalidate() { _state.value = AutomationSettings() }

    suspend fun refresh() {
        if (!mutex.tryLock()) return
        try {
            val server = selectServer()
            _state.value = _state.value.copy(refreshing = true)
            coroutineScope {
                val stop = async { torrents.getAutoStopConfig() }
                val check = async { shows.getAutoCheckConfig() }
                val stopResult = stop.await()
                val checkResult = check.await()
                if (!verifyServer(server)) return@coroutineScope
                _state.value = _state.value.copy(
                    autoStop = stopResult.getOrNull() ?: _state.value.autoStop,
                    autoCheck = checkResult.getOrNull()?.enabled ?: _state.value.autoCheck,
                    autoStopError = stopResult.exceptionOrNull()?.message,
                    autoCheckError = checkResult.exceptionOrNull()?.message)
            }
        } finally {
            _state.value = _state.value.copy(refreshing = false)
            mutex.unlock()
        }
    }

    suspend fun saveAutoCheck(enabled: Boolean): Result<Unit> = apiResult {
        mutex.withLock {
            val server = selectServer()
            shows.setAutoCheckEnabled(enabled).getOrThrow()
            check(verifyServer(server)) { "Server changed while saving. Reload its settings." }
            _state.value = _state.value.copy(autoCheck = enabled, autoCheckError = null)
        }
    }

    suspend fun saveAutoStop(config: AutoStopConfig): Result<Unit> = apiResult {
        mutex.withLock {
            val server = selectServer()
            torrents.setAutoStopConfig(config).getOrThrow()
            check(verifyServer(server)) { "Server changed while saving. Reload its settings." }
            // qBittorrent normalises the limits; show what it actually stored.
            val confirmed = torrents.getAutoStopConfig().getOrNull() ?: config
            _state.value = _state.value.copy(autoStop = confirmed, autoStopError = null)
        }
    }
}
