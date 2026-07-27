package com.unifiedserver.dashboard.ui.screens;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b#\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u00a9\u0001\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u0012\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u0005\u0012\b\b\u0002\u0010\n\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u000b\u001a\u00020\u0003\u0012\u000e\b\u0002\u0010\f\u001a\b\u0012\u0004\u0012\u00020\r0\u0007\u0012\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u0005\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u0011\u0012\b\b\u0002\u0010\u0012\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0013\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0014\u001a\u0004\u0018\u00010\u0015\u0012\n\b\u0002\u0010\u0016\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\u0002\u0010\u0017J\t\u0010'\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010(\u001a\u0004\u0018\u00010\u0011H\u00c6\u0003J\t\u0010)\u001a\u00020\u0003H\u00c6\u0003J\t\u0010*\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010+\u001a\u0004\u0018\u00010\u0015H\u00c6\u0003J\u000b\u0010,\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u000b\u0010-\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u000f\u0010.\u001a\b\u0012\u0004\u0012\u00020\b0\u0007H\u00c6\u0003J\u000b\u0010/\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\t\u00100\u001a\u00020\u0003H\u00c6\u0003J\t\u00101\u001a\u00020\u0003H\u00c6\u0003J\u000f\u00102\u001a\b\u0012\u0004\u0012\u00020\r0\u0007H\u00c6\u0003J\u000b\u00103\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\t\u00104\u001a\u00020\u0003H\u00c6\u0003J\u00ad\u0001\u00105\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\n\u001a\u00020\u00032\b\b\u0002\u0010\u000b\u001a\u00020\u00032\u000e\b\u0002\u0010\f\u001a\b\u0012\u0004\u0012\u00020\r0\u00072\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u000f\u001a\u00020\u00032\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u00112\b\b\u0002\u0010\u0012\u001a\u00020\u00032\b\b\u0002\u0010\u0013\u001a\u00020\u00032\n\b\u0002\u0010\u0014\u001a\u0004\u0018\u00010\u00152\n\b\u0002\u0010\u0016\u001a\u0004\u0018\u00010\u0005H\u00c6\u0001J\u0013\u00106\u001a\u00020\u00032\b\u00107\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u00108\u001a\u000209H\u00d6\u0001J\t\u0010:\u001a\u00020\u0005H\u00d6\u0001R\u0011\u0010\u0012\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0013\u0010\u0014\u001a\u0004\u0018\u00010\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0013\u0010\u0016\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001dR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001dR\u0011\u0010\n\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u0019R\u0011\u0010\u0013\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0019R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u0019R\u0011\u0010\u000b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\u0019R\u0013\u0010\u0010\u001a\u0004\u0018\u00010\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010 R\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\"R\u0013\u0010\t\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\u001dR\u0011\u0010\u000f\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010\u0019R\u0013\u0010\u000e\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b%\u0010\u001dR\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\r0\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010\"\u00a8\u0006;"}, d2 = {"Lcom/unifiedserver/dashboard/ui/screens/ShowsUiState;", "", "isLoading", "", "error", "", "shows", "", "Lcom/unifiedserver/dashboard/data/models/Show;", "snackbarMessage", "isCheckingEpisodes", "isSearchingTmdb", "tmdbSearchResults", "Lcom/unifiedserver/dashboard/data/models/TmdbSearchResult;", "tmdbSearchError", "tmdbConfigured", "queueStatus", "Lcom/unifiedserver/dashboard/data/models/QueueStatusResponse;", "autoCheckEnabled", "isDownloadingSeason", "downloadProgress", "Lcom/unifiedserver/dashboard/data/models/DownloadProgressResponse;", "downloadSessionId", "(ZLjava/lang/String;Ljava/util/List;Ljava/lang/String;ZZLjava/util/List;Ljava/lang/String;ZLcom/unifiedserver/dashboard/data/models/QueueStatusResponse;ZZLcom/unifiedserver/dashboard/data/models/DownloadProgressResponse;Ljava/lang/String;)V", "getAutoCheckEnabled", "()Z", "getDownloadProgress", "()Lcom/unifiedserver/dashboard/data/models/DownloadProgressResponse;", "getDownloadSessionId", "()Ljava/lang/String;", "getError", "getQueueStatus", "()Lcom/unifiedserver/dashboard/data/models/QueueStatusResponse;", "getShows", "()Ljava/util/List;", "getSnackbarMessage", "getTmdbConfigured", "getTmdbSearchError", "getTmdbSearchResults", "component1", "component10", "component11", "component12", "component13", "component14", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "other", "hashCode", "", "toString", "app_debug"})
public final class ShowsUiState {
    private final boolean isLoading = false;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String error = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.unifiedserver.dashboard.data.models.Show> shows = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String snackbarMessage = null;
    private final boolean isCheckingEpisodes = false;
    private final boolean isSearchingTmdb = false;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.unifiedserver.dashboard.data.models.TmdbSearchResult> tmdbSearchResults = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String tmdbSearchError = null;
    private final boolean tmdbConfigured = false;
    @org.jetbrains.annotations.Nullable()
    private final com.unifiedserver.dashboard.data.models.QueueStatusResponse queueStatus = null;
    private final boolean autoCheckEnabled = false;
    private final boolean isDownloadingSeason = false;
    @org.jetbrains.annotations.Nullable()
    private final com.unifiedserver.dashboard.data.models.DownloadProgressResponse downloadProgress = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String downloadSessionId = null;
    
    public ShowsUiState(boolean isLoading, @org.jetbrains.annotations.Nullable()
    java.lang.String error, @org.jetbrains.annotations.NotNull()
    java.util.List<com.unifiedserver.dashboard.data.models.Show> shows, @org.jetbrains.annotations.Nullable()
    java.lang.String snackbarMessage, boolean isCheckingEpisodes, boolean isSearchingTmdb, @org.jetbrains.annotations.NotNull()
    java.util.List<com.unifiedserver.dashboard.data.models.TmdbSearchResult> tmdbSearchResults, @org.jetbrains.annotations.Nullable()
    java.lang.String tmdbSearchError, boolean tmdbConfigured, @org.jetbrains.annotations.Nullable()
    com.unifiedserver.dashboard.data.models.QueueStatusResponse queueStatus, boolean autoCheckEnabled, boolean isDownloadingSeason, @org.jetbrains.annotations.Nullable()
    com.unifiedserver.dashboard.data.models.DownloadProgressResponse downloadProgress, @org.jetbrains.annotations.Nullable()
    java.lang.String downloadSessionId) {
        super();
    }
    
    public final boolean isLoading() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getError() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.unifiedserver.dashboard.data.models.Show> getShows() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getSnackbarMessage() {
        return null;
    }
    
    public final boolean isCheckingEpisodes() {
        return false;
    }
    
    public final boolean isSearchingTmdb() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.unifiedserver.dashboard.data.models.TmdbSearchResult> getTmdbSearchResults() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getTmdbSearchError() {
        return null;
    }
    
    public final boolean getTmdbConfigured() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.unifiedserver.dashboard.data.models.QueueStatusResponse getQueueStatus() {
        return null;
    }
    
    public final boolean getAutoCheckEnabled() {
        return false;
    }
    
    public final boolean isDownloadingSeason() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.unifiedserver.dashboard.data.models.DownloadProgressResponse getDownloadProgress() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getDownloadSessionId() {
        return null;
    }
    
    public ShowsUiState() {
        super();
    }
    
    public final boolean component1() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.unifiedserver.dashboard.data.models.QueueStatusResponse component10() {
        return null;
    }
    
    public final boolean component11() {
        return false;
    }
    
    public final boolean component12() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.unifiedserver.dashboard.data.models.DownloadProgressResponse component13() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component14() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.unifiedserver.dashboard.data.models.Show> component3() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component4() {
        return null;
    }
    
    public final boolean component5() {
        return false;
    }
    
    public final boolean component6() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.unifiedserver.dashboard.data.models.TmdbSearchResult> component7() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component8() {
        return null;
    }
    
    public final boolean component9() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.unifiedserver.dashboard.ui.screens.ShowsUiState copy(boolean isLoading, @org.jetbrains.annotations.Nullable()
    java.lang.String error, @org.jetbrains.annotations.NotNull()
    java.util.List<com.unifiedserver.dashboard.data.models.Show> shows, @org.jetbrains.annotations.Nullable()
    java.lang.String snackbarMessage, boolean isCheckingEpisodes, boolean isSearchingTmdb, @org.jetbrains.annotations.NotNull()
    java.util.List<com.unifiedserver.dashboard.data.models.TmdbSearchResult> tmdbSearchResults, @org.jetbrains.annotations.Nullable()
    java.lang.String tmdbSearchError, boolean tmdbConfigured, @org.jetbrains.annotations.Nullable()
    com.unifiedserver.dashboard.data.models.QueueStatusResponse queueStatus, boolean autoCheckEnabled, boolean isDownloadingSeason, @org.jetbrains.annotations.Nullable()
    com.unifiedserver.dashboard.data.models.DownloadProgressResponse downloadProgress, @org.jetbrains.annotations.Nullable()
    java.lang.String downloadSessionId) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}