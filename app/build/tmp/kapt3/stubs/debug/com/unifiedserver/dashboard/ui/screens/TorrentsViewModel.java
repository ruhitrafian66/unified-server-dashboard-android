package com.unifiedserver.dashboard.ui.screens;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000d\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fJ\u0006\u0010\u0010\u001a\u00020\rJ\u0006\u0010\u0011\u001a\u00020\rJ\u0018\u0010\u0012\u001a\u00020\r2\u0006\u0010\u0013\u001a\u00020\u000f2\b\b\u0002\u0010\u0014\u001a\u00020\u0015J\u0016\u0010\u0016\u001a\u00020\r2\u0006\u0010\u0017\u001a\u00020\u0018H\u0082@\u00a2\u0006\u0002\u0010\u0019J\f\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u001c0\u001bJ\f\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u001e0\u001bJ\u0006\u0010\u001f\u001a\u00020\rJ\u000e\u0010 \u001a\u00020\r2\u0006\u0010\u0013\u001a\u00020\u000fJ\u0016\u0010!\u001a\u00020\r2\u0006\u0010\u0017\u001a\u00020\u0018H\u0082@\u00a2\u0006\u0002\u0010\u0019J\u0006\u0010\"\u001a\u00020\rJ\u000e\u0010#\u001a\u00020\r2\u0006\u0010\u0013\u001a\u00020\u000fJ\u000e\u0010$\u001a\u00020\r2\u0006\u0010%\u001a\u00020&J\u000e\u0010'\u001a\u00020\r2\u0006\u0010%\u001a\u00020&J,\u0010(\u001a\b\u0012\u0004\u0012\u00020\u001c0\u001b2\f\u0010)\u001a\b\u0012\u0004\u0012\u00020\u001c0\u001b2\u0006\u0010%\u001a\u00020&2\u0006\u0010*\u001a\u00020+H\u0002J,\u0010,\u001a\b\u0012\u0004\u0012\u00020\u001e0\u001b2\f\u0010-\u001a\b\u0012\u0004\u0012\u00020\u001e0\u001b2\u0006\u0010%\u001a\u00020&2\u0006\u0010*\u001a\u00020+H\u0002J\u000e\u0010.\u001a\u00020\r2\u0006\u0010/\u001a\u00020\u000fJ\u0006\u00100\u001a\u00020\rR\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u00a8\u00061"}, d2 = {"Lcom/unifiedserver/dashboard/ui/screens/TorrentsViewModel;", "Landroidx/lifecycle/ViewModel;", "repository", "Lcom/unifiedserver/dashboard/data/repository/TorrentRepository;", "(Lcom/unifiedserver/dashboard/data/repository/TorrentRepository;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/unifiedserver/dashboard/ui/screens/TorrentsUiState;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "addTorrent", "", "url", "", "clearSearch", "clearSnackbar", "deleteTorrent", "hash", "deleteFiles", "", "fetchSearchResults", "id", "", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getSortedSearchResults", "", "Lcom/unifiedserver/dashboard/data/models/SearchResult;", "getSortedTorrents", "Lcom/unifiedserver/dashboard/data/models/Torrent;", "loadTorrents", "pauseTorrent", "pollSearchStatus", "refresh", "resumeTorrent", "setSearchResultsSort", "sortOption", "Lcom/unifiedserver/dashboard/ui/screens/TorrentSortOption;", "setTorrentSort", "sortSearchResults", "results", "sortOrder", "Lcom/unifiedserver/dashboard/ui/screens/SortOrder;", "sortTorrents", "torrents", "startSearch", "query", "stopSearch", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class TorrentsViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.unifiedserver.dashboard.data.repository.TorrentRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.unifiedserver.dashboard.ui.screens.TorrentsUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.unifiedserver.dashboard.ui.screens.TorrentsUiState> uiState = null;
    
    @javax.inject.Inject()
    public TorrentsViewModel(@org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.data.repository.TorrentRepository repository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.unifiedserver.dashboard.ui.screens.TorrentsUiState> getUiState() {
        return null;
    }
    
    public final void loadTorrents() {
    }
    
    public final void addTorrent(@org.jetbrains.annotations.NotNull()
    java.lang.String url) {
    }
    
    public final void pauseTorrent(@org.jetbrains.annotations.NotNull()
    java.lang.String hash) {
    }
    
    public final void resumeTorrent(@org.jetbrains.annotations.NotNull()
    java.lang.String hash) {
    }
    
    public final void deleteTorrent(@org.jetbrains.annotations.NotNull()
    java.lang.String hash, boolean deleteFiles) {
    }
    
    public final void startSearch(@org.jetbrains.annotations.NotNull()
    java.lang.String query) {
    }
    
    private final java.lang.Object pollSearchStatus(int id, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final java.lang.Object fetchSearchResults(int id, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    public final void stopSearch() {
    }
    
    public final void clearSearch() {
    }
    
    public final void clearSnackbar() {
    }
    
    public final void refresh() {
    }
    
    public final void setTorrentSort(@org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.ui.screens.TorrentSortOption sortOption) {
    }
    
    public final void setSearchResultsSort(@org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.ui.screens.TorrentSortOption sortOption) {
    }
    
    private final java.util.List<com.unifiedserver.dashboard.data.models.Torrent> sortTorrents(java.util.List<com.unifiedserver.dashboard.data.models.Torrent> torrents, com.unifiedserver.dashboard.ui.screens.TorrentSortOption sortOption, com.unifiedserver.dashboard.ui.screens.SortOrder sortOrder) {
        return null;
    }
    
    private final java.util.List<com.unifiedserver.dashboard.data.models.SearchResult> sortSearchResults(java.util.List<com.unifiedserver.dashboard.data.models.SearchResult> results, com.unifiedserver.dashboard.ui.screens.TorrentSortOption sortOption, com.unifiedserver.dashboard.ui.screens.SortOrder sortOrder) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.unifiedserver.dashboard.data.models.Torrent> getSortedTorrents() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.unifiedserver.dashboard.data.models.SearchResult> getSortedSearchResults() {
        return null;
    }
}