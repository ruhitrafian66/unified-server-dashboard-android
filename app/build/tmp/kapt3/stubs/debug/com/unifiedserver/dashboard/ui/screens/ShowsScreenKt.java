package com.unifiedserver.dashboard.ui.screens;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\\\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\b\u001a\u00b6\u0001\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00032\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\b\u0010\b\u001a\u0004\u0018\u00010\t2\u0012\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u00010\u000b2\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00010\r2b\u0010\u000e\u001a^\u0012\u0013\u0012\u00110\t\u00a2\u0006\f\b\u0010\u0012\b\b\u0011\u0012\u0004\b\b(\u0011\u0012\u0015\u0012\u0013\u0018\u00010\u0012\u00a2\u0006\f\b\u0010\u0012\b\b\u0011\u0012\u0004\b\b(\u0013\u0012\u0013\u0012\u00110\u0012\u00a2\u0006\f\b\u0010\u0012\b\b\u0011\u0012\u0004\b\b(\u0014\u0012\u0013\u0012\u00110\u0012\u00a2\u0006\f\b\u0010\u0012\b\b\u0011\u0012\u0004\b\b(\u0015\u0012\u0004\u0012\u00020\u00010\u000fH\u0007\u001a\b\u0010\u0016\u001a\u00020\u0001H\u0007\u001a.\u0010\u0017\u001a\u00020\u00012\b\u0010\u0018\u001a\u0004\u0018\u00010\u00192\f\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00010\r2\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00010\rH\u0007\u001a\u007f\u0010\u001b\u001a\u00020\u00012\u0006\u0010\u001c\u001a\u00020\u001d2\u0012\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u00010\u000b2K\u0010\u001e\u001aG\u0012\u0013\u0012\u00110\u0012\u00a2\u0006\f\b\u0010\u0012\b\b\u0011\u0012\u0004\b\b(\u0013\u0012\u0013\u0012\u00110\t\u00a2\u0006\f\b\u0010\u0012\b\b\u0011\u0012\u0004\b\b(\u0011\u0012\u0013\u0012\u00110\u0012\u00a2\u0006\f\b\u0010\u0012\b\b\u0011\u0012\u0004\b\b(\u0014\u0012\u0004\u0012\u00020\u00010\u001f2\f\u0010 \u001a\b\u0012\u0004\u0012\u00020\u00010\rH\u0007\u001a\u0012\u0010!\u001a\u00020\u00012\b\b\u0002\u0010\"\u001a\u00020#H\u0007\u001a\u0018\u0010$\u001a\u00020\u00012\u0006\u0010%\u001a\u00020\u00122\u0006\u0010&\u001a\u00020\u0012H\u0007\u001a&\u0010'\u001a\u00020\u00012\u0006\u0010(\u001a\u00020\u00072\u0006\u0010)\u001a\u00020\u00032\f\u0010*\u001a\b\u0012\u0004\u0012\u00020\u00010\rH\u0007\u00a8\u0006+"}, d2 = {"AddShowDialog", "", "isTmdbConfigured", "", "isSearching", "searchResults", "", "Lcom/unifiedserver/dashboard/data/models/TmdbSearchResult;", "searchError", "", "onSearch", "Lkotlin/Function1;", "onDismiss", "Lkotlin/Function0;", "onAdd", "Lkotlin/Function4;", "Lkotlin/ParameterName;", "name", "", "tmdbId", "season", "episode", "EmptyShowsView", "QueueStatusSheet", "queueStatus", "Lcom/unifiedserver/dashboard/data/models/QueueStatusResponse;", "onClear", "SeasonDownloadTab", "uiState", "Lcom/unifiedserver/dashboard/ui/screens/ShowsUiState;", "onDownload", "Lkotlin/Function3;", "onClearProgress", "ShowsScreen", "viewModel", "Lcom/unifiedserver/dashboard/ui/screens/ShowsViewModel;", "ShowsSummaryRow", "activeCount", "totalCount", "TmdbResultItem", "result", "isSelected", "onClick", "app_debug"})
public final class ShowsScreenKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void ShowsScreen(@org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.ui.screens.ShowsViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void ShowsSummaryRow(int activeCount, int totalCount) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void EmptyShowsView() {
    }
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void AddShowDialog(boolean isTmdbConfigured, boolean isSearching, @org.jetbrains.annotations.NotNull()
    java.util.List<com.unifiedserver.dashboard.data.models.TmdbSearchResult> searchResults, @org.jetbrains.annotations.Nullable()
    java.lang.String searchError, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onSearch, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function4<? super java.lang.String, ? super java.lang.Integer, ? super java.lang.Integer, ? super java.lang.Integer, kotlin.Unit> onAdd) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void TmdbResultItem(@org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.data.models.TmdbSearchResult result, boolean isSelected, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void QueueStatusSheet(@org.jetbrains.annotations.Nullable()
    com.unifiedserver.dashboard.data.models.QueueStatusResponse queueStatus, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onClear, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void SeasonDownloadTab(@org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.ui.screens.ShowsUiState uiState, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onSearch, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function3<? super java.lang.Integer, ? super java.lang.String, ? super java.lang.Integer, kotlin.Unit> onDownload, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onClearProgress) {
    }
}