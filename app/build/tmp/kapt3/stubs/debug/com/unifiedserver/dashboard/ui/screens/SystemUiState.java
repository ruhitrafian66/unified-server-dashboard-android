package com.unifiedserver.dashboard.ui.screens;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0017\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001Bc\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t\u0012\u000e\b\u0002\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\t\u0012\u000e\b\u0002\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\t\u0012\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\u0002\u0010\u0010J\t\u0010\u001b\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\u001c\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u000b\u0010\u001d\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\u000f\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\n0\tH\u00c6\u0003J\u000f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\f0\tH\u00c6\u0003J\u000f\u0010 \u001a\b\u0012\u0004\u0012\u00020\u000e0\tH\u00c6\u0003J\u000b\u0010!\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003Jg\u0010\"\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00072\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t2\u000e\b\u0002\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\t2\u000e\b\u0002\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\t2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u0005H\u00c6\u0001J\u0013\u0010#\u001a\u00020\u00032\b\u0010$\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010%\u001a\u00020&H\u00d6\u0001J\t\u0010'\u001a\u00020\u0005H\u00d6\u0001R\u0017\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0017\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0012R\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u0016R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0012R\u0013\u0010\u000f\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0015R\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001a\u00a8\u0006("}, d2 = {"Lcom/unifiedserver/dashboard/ui/screens/SystemUiState;", "", "isLoading", "", "error", "", "systemInfo", "Lcom/unifiedserver/dashboard/data/models/SystemInfo;", "services", "", "Lcom/unifiedserver/dashboard/data/models/ServiceStatus;", "disks", "Lcom/unifiedserver/dashboard/data/models/DiskDevice;", "dockerContainers", "Lcom/unifiedserver/dashboard/data/models/DockerContainer;", "snackbarMessage", "(ZLjava/lang/String;Lcom/unifiedserver/dashboard/data/models/SystemInfo;Ljava/util/List;Ljava/util/List;Ljava/util/List;Ljava/lang/String;)V", "getDisks", "()Ljava/util/List;", "getDockerContainers", "getError", "()Ljava/lang/String;", "()Z", "getServices", "getSnackbarMessage", "getSystemInfo", "()Lcom/unifiedserver/dashboard/data/models/SystemInfo;", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "copy", "equals", "other", "hashCode", "", "toString", "app_debug"})
public final class SystemUiState {
    private final boolean isLoading = false;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String error = null;
    @org.jetbrains.annotations.Nullable()
    private final com.unifiedserver.dashboard.data.models.SystemInfo systemInfo = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.unifiedserver.dashboard.data.models.ServiceStatus> services = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.unifiedserver.dashboard.data.models.DiskDevice> disks = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.unifiedserver.dashboard.data.models.DockerContainer> dockerContainers = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String snackbarMessage = null;
    
    public SystemUiState(boolean isLoading, @org.jetbrains.annotations.Nullable()
    java.lang.String error, @org.jetbrains.annotations.Nullable()
    com.unifiedserver.dashboard.data.models.SystemInfo systemInfo, @org.jetbrains.annotations.NotNull()
    java.util.List<com.unifiedserver.dashboard.data.models.ServiceStatus> services, @org.jetbrains.annotations.NotNull()
    java.util.List<com.unifiedserver.dashboard.data.models.DiskDevice> disks, @org.jetbrains.annotations.NotNull()
    java.util.List<com.unifiedserver.dashboard.data.models.DockerContainer> dockerContainers, @org.jetbrains.annotations.Nullable()
    java.lang.String snackbarMessage) {
        super();
    }
    
    public final boolean isLoading() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getError() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.unifiedserver.dashboard.data.models.SystemInfo getSystemInfo() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.unifiedserver.dashboard.data.models.ServiceStatus> getServices() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.unifiedserver.dashboard.data.models.DiskDevice> getDisks() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.unifiedserver.dashboard.data.models.DockerContainer> getDockerContainers() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getSnackbarMessage() {
        return null;
    }
    
    public SystemUiState() {
        super();
    }
    
    public final boolean component1() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.unifiedserver.dashboard.data.models.SystemInfo component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.unifiedserver.dashboard.data.models.ServiceStatus> component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.unifiedserver.dashboard.data.models.DiskDevice> component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.unifiedserver.dashboard.data.models.DockerContainer> component6() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component7() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.unifiedserver.dashboard.ui.screens.SystemUiState copy(boolean isLoading, @org.jetbrains.annotations.Nullable()
    java.lang.String error, @org.jetbrains.annotations.Nullable()
    com.unifiedserver.dashboard.data.models.SystemInfo systemInfo, @org.jetbrains.annotations.NotNull()
    java.util.List<com.unifiedserver.dashboard.data.models.ServiceStatus> services, @org.jetbrains.annotations.NotNull()
    java.util.List<com.unifiedserver.dashboard.data.models.DiskDevice> disks, @org.jetbrains.annotations.NotNull()
    java.util.List<com.unifiedserver.dashboard.data.models.DockerContainer> dockerContainers, @org.jetbrains.annotations.Nullable()
    java.lang.String snackbarMessage) {
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