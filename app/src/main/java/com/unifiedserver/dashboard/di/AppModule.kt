package com.unifiedserver.dashboard.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.unifiedserver.dashboard.data.PreferencesManager
import com.unifiedserver.dashboard.data.dataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Network clients are created lazily, only after a valid server has been chosen. */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> = context.dataStore

    @Provides
    @Singleton
    fun providePreferencesManager(dataStore: DataStore<Preferences>) = PreferencesManager(dataStore)
}
