package com.unifiedserver.dashboard.ui.screens;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\r\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0005\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J3\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u00112\b\b\u0002\u0010\u0012\u001a\u00020\u00112\b\b\u0002\u0010\u0013\u001a\u00020\u0011\u00a2\u0006\u0002\u0010\u0014J\u0006\u0010\u0015\u001a\u00020\rJ\b\u0010\u0016\u001a\u00020\rH\u0002J\u0006\u0010\u0017\u001a\u00020\rJ\u0006\u0010\u0018\u001a\u00020\rJ\u0006\u0010\u0019\u001a\u00020\rJ\u0006\u0010\u001a\u001a\u00020\rJ\u000e\u0010\u001b\u001a\u00020\r2\u0006\u0010\u001c\u001a\u00020\u000fJ\u000e\u0010\u001d\u001a\u00020\r2\u0006\u0010\u001e\u001a\u00020\u001fJ\u001e\u0010 \u001a\u00020\r2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010!\u001a\u00020\u000f2\u0006\u0010\u0012\u001a\u00020\u0011J\b\u0010\"\u001a\u00020\rH\u0002J\u0006\u0010#\u001a\u00020\rJ\u0006\u0010$\u001a\u00020\rJ\u0010\u0010%\u001a\u00020\r2\u0006\u0010&\u001a\u00020\u000fH\u0002J\u0006\u0010'\u001a\u00020\rJ\u000e\u0010(\u001a\u00020\r2\u0006\u0010)\u001a\u00020\u000fJ\u000e\u0010*\u001a\u00020\r2\u0006\u0010+\u001a\u00020,J\b\u0010-\u001a\u00020\rH\u0002J\u000e\u0010.\u001a\u00020\r2\u0006\u0010\u001e\u001a\u00020\u001fJ\u001e\u0010/\u001a\u00020\r2\u0006\u00100\u001a\u00020\u000f2\u0006\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u0013\u001a\u00020\u0011R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u00a8\u00061"}, d2 = {"Lcom/unifiedserver/dashboard/ui/screens/ShowsViewModel;", "Landroidx/lifecycle/ViewModel;", "repository", "Lcom/unifiedserver/dashboard/data/repository/ShowsRepository;", "(Lcom/unifiedserver/dashboard/data/repository/ShowsRepository;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/unifiedserver/dashboard/ui/screens/ShowsUiState;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "addShow", "", "name", "", "tmdbId", "", "season", "episode", "(Ljava/lang/String;Ljava/lang/Integer;II)V", "checkEpisodes", "checkTmdbConfig", "clearDownloadProgress", "clearQueue", "clearSnackbar", "clearTmdbSearch", "deleteShow", "id", "downloadSeason", "show", "Lcom/unifiedserver/dashboard/data/models/Show;", "downloadSeasonPack", "showName", "loadAutoCheckConfig", "loadQueueStatus", "loadShows", "pollDownloadProgress", "sessionId", "refresh", "searchTmdb", "query", "setAutoCheckEnabled", "enabled", "", "startAutoCheckPolling", "toggleShowStatus", "updateProgress", "showId", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class ShowsViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.unifiedserver.dashboard.data.repository.ShowsRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.unifiedserver.dashboard.ui.screens.ShowsUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.unifiedserver.dashboard.ui.screens.ShowsUiState> uiState = null;
    
    @javax.inject.Inject()
    public ShowsViewModel(@org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.data.repository.ShowsRepository repository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.unifiedserver.dashboard.ui.screens.ShowsUiState> getUiState() {
        return null;
    }
    
    private final void startAutoCheckPolling() {
    }
    
    public final void loadQueueStatus() {
    }
    
    public final void clearQueue() {
    }
    
    private final void loadAutoCheckConfig() {
    }
    
    public final void setAutoCheckEnabled(boolean enabled) {
    }
    
    public final void loadShows() {
    }
    
    public final void addShow(@org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.Nullable()
    java.lang.Integer tmdbId, int season, int episode) {
    }
    
    public final void toggleShowStatus(@org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.data.models.Show show) {
    }
    
    public final void updateProgress(@org.jetbrains.annotations.NotNull()
    java.lang.String showId, int season, int episode) {
    }
    
    public final void deleteShow(@org.jetbrains.annotations.NotNull()
    java.lang.String id) {
    }
    
    public final void checkEpisodes() {
    }
    
    public final void downloadSeason(@org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.data.models.Show show) {
    }
    
    public final void downloadSeasonPack(int tmdbId, @org.jetbrains.annotations.NotNull()
    java.lang.String showName, int season) {
    }
    
    private final void pollDownloadProgress(java.lang.String sessionId) {
    }
    
    public final void clearDownloadProgress() {
    }
    
    public final void searchTmdb(@org.jetbrains.annotations.NotNull()
    java.lang.String query) {
    }
    
    public final void clearTmdbSearch() {
    }
    
    private final void checkTmdbConfig() {
    }
    
    public final void clearSnackbar() {
    }
    
    public final void refresh() {
    }
}