package com.unifiedserver.dashboard.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.unifiedserver.dashboard.data.ApiService
import com.unifiedserver.dashboard.data.PreferencesManager
import com.unifiedserver.dashboard.data.dataStore
import com.unifiedserver.dashboard.data.repository.DashboardRepository
import com.unifiedserver.dashboard.data.repository.ShowsRepository
import com.unifiedserver.dashboard.data.repository.SystemRepository
import com.unifiedserver.dashboard.data.repository.TorrentRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun providePreferencesManager(dataStore: DataStore<Preferences>): PreferencesManager {
        return PreferencesManager(dataStore)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        preferencesManager: PreferencesManager
    ): Retrofit {
        val baseUrl = runBlocking { preferencesManager.serverUrl.first() }
            .trimEnd('/') + "/"
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDashboardRepository(
        apiService: ApiService,
        preferencesManager: PreferencesManager
    ): DashboardRepository {
        return DashboardRepository(apiService, preferencesManager)
    }

    @Provides
    @Singleton
    fun provideTorrentRepository(
        apiService: ApiService,
        preferencesManager: PreferencesManager
    ): TorrentRepository {
        return TorrentRepository(apiService, preferencesManager)
    }

    @Provides
    @Singleton
    fun provideShowsRepository(
        apiService: ApiService,
        preferencesManager: PreferencesManager
    ): ShowsRepository {
        return ShowsRepository(apiService, preferencesManager)
    }

    @Provides
    @Singleton
    fun provideSystemRepository(
        apiService: ApiService,
        preferencesManager: PreferencesManager
    ): SystemRepository {
        return SystemRepository(apiService, preferencesManager)
    }
}
