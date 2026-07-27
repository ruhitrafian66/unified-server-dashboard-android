package com.unifiedserver.dashboard.ui.components;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000*\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\u001a,\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0007\u001aD\u0010\u0007\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\b\b\u0002\u0010\t\u001a\u00020\nH\u0007\u001a\u0013\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e\u00a2\u0006\u0002\u0010\u000f\u001a\u000e\u0010\u0010\u001a\u00020\u000e2\u0006\u0010\r\u001a\u00020\u000e\u00a8\u0006\u0011"}, d2 = {"TorrentCard", "", "torrent", "Lcom/unifiedserver/dashboard/data/models/Torrent;", "onPause", "Lkotlin/Function0;", "onResume", "TorrentItem", "onDelete", "modifier", "Landroidx/compose/ui/Modifier;", "torrentStateColor", "Landroidx/compose/ui/graphics/Color;", "state", "", "(Ljava/lang/String;)J", "torrentStateLabel", "app_debug"})
public final class TorrentItemKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void TorrentItem(@org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.data.models.Torrent torrent, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onPause, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onResume, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDelete, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void TorrentCard(@org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.data.models.Torrent torrent, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onPause, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onResume) {
    }
    
    public static final long torrentStateColor(@org.jetbrains.annotations.NotNull()
    java.lang.String state) {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String torrentStateLabel(@org.jetbrains.annotations.NotNull()
    java.lang.String state) {
        return null;
    }
}