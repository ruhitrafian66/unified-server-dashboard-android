package com.unifiedserver.dashboard.ui.screens;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000.\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\u001a\u0012\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u0007\u001a\u001a\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\bH\u0007\u001a\u0010\u0010\t\u001a\u00020\u00012\u0006\u0010\n\u001a\u00020\u000bH\u0007\u001a\u0018\u0010\f\u001a\u00020\u00012\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u000eH\u0007\u00a8\u0006\u0010"}, d2 = {"DashboardScreen", "", "viewModel", "Lcom/unifiedserver/dashboard/ui/screens/DashboardViewModel;", "HealthBanner", "isOnline", "", "modifier", "Landroidx/compose/ui/Modifier;", "SystemInfoCards", "info", "Lcom/unifiedserver/dashboard/data/models/SystemInfo;", "SystemInfoRow", "label", "", "value", "app_debug"})
public final class DashboardScreenKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void DashboardScreen(@org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.ui.screens.DashboardViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void SystemInfoCards(@org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.data.models.SystemInfo info) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void SystemInfoRow(@org.jetbrains.annotations.NotNull()
    java.lang.String label, @org.jetbrains.annotations.NotNull()
    java.lang.String value) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void HealthBanner(boolean isOnline, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
}