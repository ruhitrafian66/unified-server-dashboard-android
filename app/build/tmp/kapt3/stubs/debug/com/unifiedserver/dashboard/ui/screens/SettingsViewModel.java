package com.unifiedserver.dashboard.ui.screens;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000V\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u001f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\u0006\u0010\u0011\u001a\u00020\u0012J\b\u0010\u0013\u001a\u00020\u0012H\u0002J\b\u0010\u0014\u001a\u00020\u0012H\u0002J\u000e\u0010\u0015\u001a\u00020\u00122\u0006\u0010\u0016\u001a\u00020\u0017J\u001e\u0010\u0018\u001a\u00020\u00122\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001cJ\u000e\u0010\u001d\u001a\u00020\u00122\u0006\u0010\u001e\u001a\u00020\u001fJ\u000e\u0010 \u001a\u00020\u00122\u0006\u0010!\u001a\u00020\u001fR\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000b0\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000b0\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010\u00a8\u0006\""}, d2 = {"Lcom/unifiedserver/dashboard/ui/screens/SettingsViewModel;", "Landroidx/lifecycle/ViewModel;", "preferencesManager", "Lcom/unifiedserver/dashboard/data/PreferencesManager;", "torrentRepository", "Lcom/unifiedserver/dashboard/data/repository/TorrentRepository;", "showsRepository", "Lcom/unifiedserver/dashboard/data/repository/ShowsRepository;", "(Lcom/unifiedserver/dashboard/data/PreferencesManager;Lcom/unifiedserver/dashboard/data/repository/TorrentRepository;Lcom/unifiedserver/dashboard/data/repository/ShowsRepository;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/unifiedserver/dashboard/ui/screens/SettingsUiState;", "prefsFlow", "Lkotlinx/coroutines/flow/StateFlow;", "uiState", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "clearSnackbar", "", "loadAutoCheckConfig", "loadAutoStopConfig", "saveAutoCheckEnabled", "enabled", "", "saveAutoStopConfig", "delayMinutes", "", "ratio", "", "saveTmdbApiKey", "key", "", "updateServerUrl", "url", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class SettingsViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.unifiedserver.dashboard.data.PreferencesManager preferencesManager = null;
    @org.jetbrains.annotations.NotNull()
    private final com.unifiedserver.dashboard.data.repository.TorrentRepository torrentRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.unifiedserver.dashboard.data.repository.ShowsRepository showsRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.unifiedserver.dashboard.ui.screens.SettingsUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.unifiedserver.dashboard.ui.screens.SettingsUiState> uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.unifiedserver.dashboard.ui.screens.SettingsUiState> prefsFlow = null;
    
    @javax.inject.Inject()
    public SettingsViewModel(@org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.data.PreferencesManager preferencesManager, @org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.data.repository.TorrentRepository torrentRepository, @org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.data.repository.ShowsRepository showsRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.unifiedserver.dashboard.ui.screens.SettingsUiState> getUiState() {
        return null;
    }
    
    public final void updateServerUrl(@org.jetbrains.annotations.NotNull()
    java.lang.String url) {
    }
    
    public final void saveTmdbApiKey(@org.jetbrains.annotations.NotNull()
    java.lang.String key) {
    }
    
    public final void saveAutoStopConfig(boolean enabled, int delayMinutes, float ratio) {
    }
    
    private final void loadAutoStopConfig() {
    }
    
    private final void loadAutoCheckConfig() {
    }
    
    public final void saveAutoCheckEnabled(boolean enabled) {
    }
    
    public final void clearSnackbar() {
    }
}