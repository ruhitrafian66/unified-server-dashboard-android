package com.unifiedserver.dashboard.data.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u001c\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\n\u0010\u000bJ\u001c\u0010\f\u001a\b\u0012\u0004\u0012\u00020\r0\bH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u000e\u0010\u000bJ\"\u0010\u000f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00110\u00100\bH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u0012\u0010\u000bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006\u0013"}, d2 = {"Lcom/unifiedserver/dashboard/data/repository/DashboardRepository;", "", "apiService", "Lcom/unifiedserver/dashboard/data/ApiService;", "preferencesManager", "Lcom/unifiedserver/dashboard/data/PreferencesManager;", "(Lcom/unifiedserver/dashboard/data/ApiService;Lcom/unifiedserver/dashboard/data/PreferencesManager;)V", "getHealth", "Lkotlin/Result;", "Lcom/unifiedserver/dashboard/data/models/HealthResponse;", "getHealth-IoAF18A", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getSystemInfo", "Lcom/unifiedserver/dashboard/data/models/SystemInfo;", "getSystemInfo-IoAF18A", "getTorrents", "", "Lcom/unifiedserver/dashboard/data/models/Torrent;", "getTorrents-IoAF18A", "app_debug"})
public final class DashboardRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.unifiedserver.dashboard.data.ApiService apiService = null;
    @org.jetbrains.annotations.NotNull()
    private final com.unifiedserver.dashboard.data.PreferencesManager preferencesManager = null;
    
    @javax.inject.Inject()
    public DashboardRepository(@org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.data.ApiService apiService, @org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.data.PreferencesManager preferencesManager) {
        super();
    }
}