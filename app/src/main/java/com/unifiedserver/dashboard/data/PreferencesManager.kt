package com.unifiedserver.dashboard.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(private val dataStore: DataStore<Preferences>) {

    companion object {
        val SERVER_URL = stringPreferencesKey("server_url")
        val OMDB_API_KEY = stringPreferencesKey("omdb_api_key")
        // Superseded by server-side settings; removed from storage on first launch.
        private val LEGACY_KEYS = listOf("tmdb_api_key", "auto_stop_enabled", "auto_stop_seeding_time", "auto_stop_ratio")
        val SETUP_COMPLETE = booleanPreferencesKey("setup_complete")
    }

    val serverUrl: Flow<String> = dataStore.data.map { preferences ->
        preferences[SERVER_URL] ?: ""
    }

    val omdbApiKey: Flow<String> = dataStore.data.map { it[OMDB_API_KEY] ?: "" }

    suspend fun updateOmdbApiKey(key: String) {
        dataStore.edit { it[OMDB_API_KEY] = key }
    }

    val setupComplete: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[SETUP_COMPLETE] ?: false
    }

    suspend fun updateServerUrl(url: String) {
        dataStore.edit { preferences ->
            preferences[SERVER_URL] = url
        }
    }

    suspend fun configureServer(url: String) {
        dataStore.edit {
            it[SERVER_URL] = url
            it[SETUP_COMPLETE] = true
        }
    }

    /**
     * The TMDB key and seeding limits now live on the server (shared by every device).
     * Drop the old device copies so a stale API key is not kept around.
     */
    suspend fun removeLegacySettings() {
        dataStore.edit { prefs ->
            prefs.asMap().keys.filter { it.name in LEGACY_KEYS }.forEach { prefs.remove(it) }
        }
    }

    suspend fun setSetupComplete(complete: Boolean) {
        dataStore.edit { preferences ->
            preferences[SETUP_COMPLETE] = complete
        }
    }
}
