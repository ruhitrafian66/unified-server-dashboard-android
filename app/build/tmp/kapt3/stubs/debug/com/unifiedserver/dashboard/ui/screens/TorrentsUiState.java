package com.unifiedserver.dashboard.ui.screens;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b,\b\u0086\b\u0018\u00002\u00020\u0001B\u00a5\u0001\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u0012\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u0005\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\r\u0012\b\b\u0002\u0010\u000e\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u0005\u0012\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u0011\u0012\b\b\u0002\u0010\u0012\u001a\u00020\u0005\u0012\u000e\b\u0002\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00140\u0007\u0012\n\b\u0002\u0010\u0015\u001a\u0004\u0018\u00010\u0005\u0012\b\b\u0002\u0010\u0016\u001a\u00020\u000b\u0012\b\b\u0002\u0010\u0017\u001a\u00020\r\u00a2\u0006\u0002\u0010\u0018J\t\u0010,\u001a\u00020\u0003H\u00c6\u0003J\t\u0010-\u001a\u00020\u0005H\u00c6\u0003J\u000f\u0010.\u001a\b\u0012\u0004\u0012\u00020\u00140\u0007H\u00c6\u0003J\u000b\u0010/\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\t\u00100\u001a\u00020\u000bH\u00c6\u0003J\t\u00101\u001a\u00020\rH\u00c6\u0003J\u000b\u00102\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u000f\u00103\u001a\b\u0012\u0004\u0012\u00020\b0\u0007H\u00c6\u0003J\u000b\u00104\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\t\u00105\u001a\u00020\u000bH\u00c6\u0003J\t\u00106\u001a\u00020\rH\u00c6\u0003J\t\u00107\u001a\u00020\u0003H\u00c6\u0003J\t\u00108\u001a\u00020\u0005H\u00c6\u0003J\u0010\u00109\u001a\u0004\u0018\u00010\u0011H\u00c6\u0003\u00a2\u0006\u0002\u0010\u001eJ\u00ae\u0001\u0010:\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\u00032\b\b\u0002\u0010\u000f\u001a\u00020\u00052\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u00112\b\b\u0002\u0010\u0012\u001a\u00020\u00052\u000e\b\u0002\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00140\u00072\n\b\u0002\u0010\u0015\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u0016\u001a\u00020\u000b2\b\b\u0002\u0010\u0017\u001a\u00020\rH\u00c6\u0001\u00a2\u0006\u0002\u0010;J\u0013\u0010<\u001a\u00020\u00032\b\u0010=\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010>\u001a\u00020\u0011H\u00d6\u0001J\t\u0010?\u001a\u00020\u0005H\u00d6\u0001R\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u001bR\u0011\u0010\u000e\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u001bR\u0013\u0010\u0015\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001aR\u0015\u0010\u0010\u001a\u0004\u0018\u00010\u0011\u00a2\u0006\n\n\u0002\u0010\u001f\u001a\u0004\b\u001d\u0010\u001eR\u0011\u0010\u000f\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\u001aR\u0017\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00140\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\"R\u0011\u0010\u0016\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010$R\u0011\u0010\u0017\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b%\u0010&R\u0011\u0010\u0012\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b'\u0010\u001aR\u0013\u0010\t\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b(\u0010\u001aR\u0011\u0010\n\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b)\u0010$R\u0011\u0010\f\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b*\u0010&R\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b+\u0010\"\u00a8\u0006@"}, d2 = {"Lcom/unifiedserver/dashboard/ui/screens/TorrentsUiState;", "", "isLoading", "", "error", "", "torrents", "", "Lcom/unifiedserver/dashboard/data/models/Torrent;", "snackbarMessage", "torrentSortOption", "Lcom/unifiedserver/dashboard/ui/screens/TorrentSortOption;", "torrentSortOrder", "Lcom/unifiedserver/dashboard/ui/screens/SortOrder;", "isSearching", "searchQuery", "searchId", "", "searchStatus", "searchResults", "Lcom/unifiedserver/dashboard/data/models/SearchResult;", "searchError", "searchResultsSortOption", "searchResultsSortOrder", "(ZLjava/lang/String;Ljava/util/List;Ljava/lang/String;Lcom/unifiedserver/dashboard/ui/screens/TorrentSortOption;Lcom/unifiedserver/dashboard/ui/screens/SortOrder;ZLjava/lang/String;Ljava/lang/Integer;Ljava/lang/String;Ljava/util/List;Ljava/lang/String;Lcom/unifiedserver/dashboard/ui/screens/TorrentSortOption;Lcom/unifiedserver/dashboard/ui/screens/SortOrder;)V", "getError", "()Ljava/lang/String;", "()Z", "getSearchError", "getSearchId", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getSearchQuery", "getSearchResults", "()Ljava/util/List;", "getSearchResultsSortOption", "()Lcom/unifiedserver/dashboard/ui/screens/TorrentSortOption;", "getSearchResultsSortOrder", "()Lcom/unifiedserver/dashboard/ui/screens/SortOrder;", "getSearchStatus", "getSnackbarMessage", "getTorrentSortOption", "getTorrentSortOrder", "getTorrents", "component1", "component10", "component11", "component12", "component13", "component14", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(ZLjava/lang/String;Ljava/util/List;Ljava/lang/String;Lcom/unifiedserver/dashboard/ui/screens/TorrentSortOption;Lcom/unifiedserver/dashboard/ui/screens/SortOrder;ZLjava/lang/String;Ljava/lang/Integer;Ljava/lang/String;Ljava/util/List;Ljava/lang/String;Lcom/unifiedserver/dashboard/ui/screens/TorrentSortOption;Lcom/unifiedserver/dashboard/ui/screens/SortOrder;)Lcom/unifiedserver/dashboard/ui/screens/TorrentsUiState;", "equals", "other", "hashCode", "toString", "app_debug"})
public final class TorrentsUiState {
    private final boolean isLoading = false;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String error = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.unifiedserver.dashboard.data.models.Torrent> torrents = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String snackbarMessage = null;
    @org.jetbrains.annotations.NotNull()
    private final com.unifiedserver.dashboard.ui.screens.TorrentSortOption torrentSortOption = null;
    @org.jetbrains.annotations.NotNull()
    private final com.unifiedserver.dashboard.ui.screens.SortOrder torrentSortOrder = null;
    private final boolean isSearching = false;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String searchQuery = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer searchId = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String searchStatus = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.unifiedserver.dashboard.data.models.SearchResult> searchResults = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String searchError = null;
    @org.jetbrains.annotations.NotNull()
    private final com.unifiedserver.dashboard.ui.screens.TorrentSortOption searchResultsSortOption = null;
    @org.jetbrains.annotations.NotNull()
    private final com.unifiedserver.dashboard.ui.screens.SortOrder searchResultsSortOrder = null;
    
    public TorrentsUiState(boolean isLoading, @org.jetbrains.annotations.Nullable()
    java.lang.String error, @org.jetbrains.annotations.NotNull()
    java.util.List<com.unifiedserver.dashboard.data.models.Torrent> torrents, @org.jetbrains.annotations.Nullable()
    java.lang.String snackbarMessage, @org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.ui.screens.TorrentSortOption torrentSortOption, @org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.ui.screens.SortOrder torrentSortOrder, boolean isSearching, @org.jetbrains.annotations.NotNull()
    java.lang.String searchQuery, @org.jetbrains.annotations.Nullable()
    java.lang.Integer searchId, @org.jetbrains.annotations.NotNull()
    java.lang.String searchStatus, @org.jetbrains.annotations.NotNull()
    java.util.List<com.unifiedserver.dashboard.data.models.SearchResult> searchResults, @org.jetbrains.annotations.Nullable()
    java.lang.String searchError, @org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.ui.screens.TorrentSortOption searchResultsSortOption, @org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.ui.screens.SortOrder searchResultsSortOrder) {
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
    public final java.util.List<com.unifiedserver.dashboard.data.models.Torrent> getTorrents() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getSnackbarMessage() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.unifiedserver.dashboard.ui.screens.TorrentSortOption getTorrentSortOption() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.unifiedserver.dashboard.ui.screens.SortOrder getTorrentSortOrder() {
        return null;
    }
    
    public final boolean isSearching() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getSearchQuery() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getSearchId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getSearchStatus() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.unifiedserver.dashboard.data.models.SearchResult> getSearchResults() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getSearchError() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.unifiedserver.dashboard.ui.screens.TorrentSortOption getSearchResultsSortOption() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.unifiedserver.dashboard.ui.screens.SortOrder getSearchResultsSortOrder() {
        return null;
    }
    
    public TorrentsUiState() {
        super();
    }
    
    public final boolean component1() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component10() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.unifiedserver.dashboard.data.models.SearchResult> component11() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component12() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.unifiedserver.dashboard.ui.screens.TorrentSortOption component13() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.unifiedserver.dashboard.ui.screens.SortOrder component14() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.unifiedserver.dashboard.data.models.Torrent> component3() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.unifiedserver.dashboard.ui.screens.TorrentSortOption component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.unifiedserver.dashboard.ui.screens.SortOrder component6() {
        return null;
    }
    
    public final boolean component7() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component8() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.unifiedserver.dashboard.ui.screens.TorrentsUiState copy(boolean isLoading, @org.jetbrains.annotations.Nullable()
    java.lang.String error, @org.jetbrains.annotations.NotNull()
    java.util.List<com.unifiedserver.dashboard.data.models.Torrent> torrents, @org.jetbrains.annotations.Nullable()
    java.lang.String snackbarMessage, @org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.ui.screens.TorrentSortOption torrentSortOption, @org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.ui.screens.SortOrder torrentSortOrder, boolean isSearching, @org.jetbrains.annotations.NotNull()
    java.lang.String searchQuery, @org.jetbrains.annotations.Nullable()
    java.lang.Integer searchId, @org.jetbrains.annotations.NotNull()
    java.lang.String searchStatus, @org.jetbrains.annotations.NotNull()
    java.util.List<com.unifiedserver.dashboard.data.models.SearchResult> searchResults, @org.jetbrains.annotations.Nullable()
    java.lang.String searchError, @org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.ui.screens.TorrentSortOption searchResultsSortOption, @org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.ui.screens.SortOrder searchResultsSortOrder) {
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