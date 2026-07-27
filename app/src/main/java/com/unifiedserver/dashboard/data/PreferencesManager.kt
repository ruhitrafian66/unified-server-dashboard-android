package com.unifiedserver.dashboard.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(private val dataStore: DataStore<Preferences>) {

    companion object {
        val SERVER_URL = stringPreferencesKey("server_url")
        val TMDB_API_KEY = stringPreferencesKey("tmdb_api_key")
        val AUTO_STOP_ENABLED = booleanPreferencesKey("auto_stop_enabled")
        val AUTO_STOP_SEEDING_TIME = intPreferencesKey("auto_stop_seeding_time")
        val AUTO_STOP_RATIO = floatPreferencesKey("auto_stop_ratio")
        val SETUP_COMPLETE = booleanPreferencesKey("setup_complete")

        const val DEFAULT_SERVER_URL = "http://192.168.1.100:3001"
    }

    val serverUrl: Flow<String> = dataStore.data.map { preferences ->
        preferences[SERVER_URL] ?: DEFAULT_SERVER_URL
    }

    val tmdbApiKey: Flow<String> = dataStore.data.map { preferences ->
        preferences[TMDB_API_KEY] ?: ""
    }

    val autoStopEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[AUTO_STOP_ENABLED] ?: false
    }

    val autoStopSeedingTime: Flow<Int> = dataStore.data.map { preferences ->
        preferences[AUTO_STOP_SEEDING_TIME] ?: 60
    }

    val autoStopRatio: Flow<Float> = dataStore.data.map { preferences ->
        preferences[AUTO_STOP_RATIO] ?: 1.0f
    }

    val setupComplete: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[SETUP_COMPLETE] ?: false
    }

    suspend fun updateServerUrl(url: String) {
        dataStore.edit { preferences ->
            preferences[SERVER_URL] = url
        }
    }

    suspend fun updateTmdbApiKey(key: String) {
        dataStore.edit { preferences ->
            preferences[TMDB_API_KEY] = key
        }
    }

    suspend fun updateAutoStopEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[AUTO_STOP_ENABLED] = enabled
        }
    }

    suspend fun updateAutoStopSeedingTime(minutes: Int) {
        dataStore.edit { preferences ->
            preferences[AUTO_STOP_SEEDING_TIME] = minutes
        }
    }

    suspend fun updateAutoStopRatio(ratio: Float) {
        dataStore.edit { preferences ->
            preferences[AUTO_STOP_RATIO] = ratio
        }
    }

    suspend fun setSetupComplete(complete: Boolean) {
        dataStore.edit { preferences ->
            preferences[SETUP_COMPLETE] = complete
        }
    }
}
