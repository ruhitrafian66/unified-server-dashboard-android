package com.unifiedserver.dashboard.di;

@dagger.Module()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0007J\u0018\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u00042\u0006\u0010\n\u001a\u00020\u000bH\u0007J\u0018\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\r2\b\b\u0001\u0010\u000f\u001a\u00020\u0010H\u0007J\b\u0010\u0011\u001a\u00020\u0012H\u0007J\u0016\u0010\u0013\u001a\u00020\u000b2\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u000e0\rH\u0007J\u0018\u0010\u0015\u001a\u00020\u00062\u0006\u0010\u0016\u001a\u00020\u00122\u0006\u0010\n\u001a\u00020\u000bH\u0007J\u0018\u0010\u0017\u001a\u00020\u00182\u0006\u0010\t\u001a\u00020\u00042\u0006\u0010\n\u001a\u00020\u000bH\u0007J\u0018\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\t\u001a\u00020\u00042\u0006\u0010\n\u001a\u00020\u000bH\u0007J\u0018\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\t\u001a\u00020\u00042\u0006\u0010\n\u001a\u00020\u000bH\u0007\u00a8\u0006\u001d"}, d2 = {"Lcom/unifiedserver/dashboard/di/AppModule;", "", "()V", "provideApiService", "Lcom/unifiedserver/dashboard/data/ApiService;", "retrofit", "Lretrofit2/Retrofit;", "provideDashboardRepository", "Lcom/unifiedserver/dashboard/data/repository/DashboardRepository;", "apiService", "preferencesManager", "Lcom/unifiedserver/dashboard/data/PreferencesManager;", "provideDataStore", "Landroidx/datastore/core/DataStore;", "Landroidx/datastore/preferences/core/Preferences;", "context", "Landroid/content/Context;", "provideOkHttpClient", "Lokhttp3/OkHttpClient;", "providePreferencesManager", "dataStore", "provideRetrofit", "okHttpClient", "provideShowsRepository", "Lcom/unifiedserver/dashboard/data/repository/ShowsRepository;", "provideSystemRepository", "Lcom/unifiedserver/dashboard/data/repository/SystemRepository;", "provideTorrentRepository", "Lcom/unifiedserver/dashboard/data/repository/TorrentRepository;", "app_debug"})
@dagger.hilt.InstallIn(value = {dagger.hilt.components.SingletonComponent.class})
public final class AppModule {
    @org.jetbrains.annotations.NotNull()
    public static final com.unifiedserver.dashboard.di.AppModule INSTANCE = null;
    
    private AppModule() {
        super();
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences> provideDataStore(@dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.unifiedserver.dashboard.data.PreferencesManager providePreferencesManager(@org.jetbrains.annotations.NotNull()
    androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences> dataStore) {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final okhttp3.OkHttpClient provideOkHttpClient() {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final retrofit2.Retrofit provideRetrofit(@org.jetbrains.annotations.NotNull()
    okhttp3.OkHttpClient okHttpClient, @org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.data.PreferencesManager preferencesManager) {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.unifiedserver.dashboard.data.ApiService provideApiService(@org.jetbrains.annotations.NotNull()
    retrofit2.Retrofit retrofit) {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.unifiedserver.dashboard.data.repository.DashboardRepository provideDashboardRepository(@org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.data.ApiService apiService, @org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.data.PreferencesManager preferencesManager) {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.unifiedserver.dashboard.data.repository.TorrentRepository provideTorrentRepository(@org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.data.ApiService apiService, @org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.data.PreferencesManager preferencesManager) {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.unifiedserver.dashboard.data.repository.ShowsRepository provideShowsRepository(@org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.data.ApiService apiService, @org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.data.PreferencesManager preferencesManager) {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.unifiedserver.dashboard.data.repository.SystemRepository provideSystemRepository(@org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.data.ApiService apiService, @org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.data.PreferencesManager preferencesManager) {
        return null;
    }
}