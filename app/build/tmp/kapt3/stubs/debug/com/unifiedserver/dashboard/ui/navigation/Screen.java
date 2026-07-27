package com.unifiedserver.dashboard.ui.navigation;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u00002\u00020\u0001:\u0006\r\u000e\u000f\u0010\u0011\u0012B#\b\u0004\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\u0002\u0010\u0007R\u0013\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u000b\u0082\u0001\u0006\u0013\u0014\u0015\u0016\u0017\u0018\u00a8\u0006\u0019"}, d2 = {"Lcom/unifiedserver/dashboard/ui/navigation/Screen;", "", "route", "", "title", "icon", "Landroidx/compose/ui/graphics/vector/ImageVector;", "(Ljava/lang/String;Ljava/lang/String;Landroidx/compose/ui/graphics/vector/ImageVector;)V", "getIcon", "()Landroidx/compose/ui/graphics/vector/ImageVector;", "getRoute", "()Ljava/lang/String;", "getTitle", "Dashboard", "Settings", "Setup", "Shows", "System", "Torrents", "Lcom/unifiedserver/dashboard/ui/navigation/Screen$Dashboard;", "Lcom/unifiedserver/dashboard/ui/navigation/Screen$Settings;", "Lcom/unifiedserver/dashboard/ui/navigation/Screen$Setup;", "Lcom/unifiedserver/dashboard/ui/navigation/Screen$Shows;", "Lcom/unifiedserver/dashboard/ui/navigation/Screen$System;", "Lcom/unifiedserver/dashboard/ui/navigation/Screen$Torrents;", "app_debug"})
public abstract class Screen {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String route = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String title = null;
    @org.jetbrains.annotations.Nullable()
    private final androidx.compose.ui.graphics.vector.ImageVector icon = null;
    
    private Screen(java.lang.String route, java.lang.String title, androidx.compose.ui.graphics.vector.ImageVector icon) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getRoute() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getTitle() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final androidx.compose.ui.graphics.vector.ImageVector getIcon() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/unifiedserver/dashboard/ui/navigation/Screen$Dashboard;", "Lcom/unifiedserver/dashboard/ui/navigation/Screen;", "()V", "app_debug"})
    public static final class Dashboard extends com.unifiedserver.dashboard.ui.navigation.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.unifiedserver.dashboard.ui.navigation.Screen.Dashboard INSTANCE = null;
        
        private Dashboard() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/unifiedserver/dashboard/ui/navigation/Screen$Settings;", "Lcom/unifiedserver/dashboard/ui/navigation/Screen;", "()V", "app_debug"})
    public static final class Settings extends com.unifiedserver.dashboard.ui.navigation.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.unifiedserver.dashboard.ui.navigation.Screen.Settings INSTANCE = null;
        
        private Settings() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/unifiedserver/dashboard/ui/navigation/Screen$Setup;", "Lcom/unifiedserver/dashboard/ui/navigation/Screen;", "()V", "app_debug"})
    public static final class Setup extends com.unifiedserver.dashboard.ui.navigation.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.unifiedserver.dashboard.ui.navigation.Screen.Setup INSTANCE = null;
        
        private Setup() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/unifiedserver/dashboard/ui/navigation/Screen$Shows;", "Lcom/unifiedserver/dashboard/ui/navigation/Screen;", "()V", "app_debug"})
    public static final class Shows extends com.unifiedserver.dashboard.ui.navigation.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.unifiedserver.dashboard.ui.navigation.Screen.Shows INSTANCE = null;
        
        private Shows() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/unifiedserver/dashboard/ui/navigation/Screen$System;", "Lcom/unifiedserver/dashboard/ui/navigation/Screen;", "()V", "app_debug"})
    public static final class System extends com.unifiedserver.dashboard.ui.navigation.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.unifiedserver.dashboard.ui.navigation.Screen.System INSTANCE = null;
        
        private System() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/unifiedserver/dashboard/ui/navigation/Screen$Torrents;", "Lcom/unifiedserver/dashboard/ui/navigation/Screen;", "()V", "app_debug"})
    public static final class Torrents extends com.unifiedserver.dashboard.ui.navigation.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.unifiedserver.dashboard.ui.navigation.Screen.Torrents INSTANCE = null;
        
        private Torrents() {
        }
    }
}