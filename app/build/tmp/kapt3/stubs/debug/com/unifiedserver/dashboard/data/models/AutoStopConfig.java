package com.unifiedserver.dashboard.data.models;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0016\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B1\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\u0002\u0010\tJ\t\u0010\u0014\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0015\u001a\u00020\u0005H\u00c6\u0003J\u0010\u0010\u0016\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003\u00a2\u0006\u0002\u0010\rJ\u0010\u0010\u0017\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003\u00a2\u0006\u0002\u0010\u0010J:\u0010\u0018\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u0005H\u00c6\u0001\u00a2\u0006\u0002\u0010\u0019J\u0013\u0010\u001a\u001a\u00020\u00032\b\u0010\u001b\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001c\u001a\u00020\u0005H\u00d6\u0001J\t\u0010\u001d\u001a\u00020\u001eH\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u001a\u0010\u0006\u001a\u0004\u0018\u00010\u00078\u0006X\u0087\u0004\u00a2\u0006\n\n\u0002\u0010\u000e\u001a\u0004\b\f\u0010\rR\u001a\u0010\b\u001a\u0004\u0018\u00010\u00058\u0006X\u0087\u0004\u00a2\u0006\n\n\u0002\u0010\u0011\u001a\u0004\b\u000f\u0010\u0010R\u0016\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013\u00a8\u0006\u001f"}, d2 = {"Lcom/unifiedserver/dashboard/data/models/AutoStopConfig;", "", "enabled", "", "seedingTimeLimit", "", "ratioLimit", "", "seedTimeLimit", "(ZILjava/lang/Float;Ljava/lang/Integer;)V", "getEnabled", "()Z", "getRatioLimit", "()Ljava/lang/Float;", "Ljava/lang/Float;", "getSeedTimeLimit", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getSeedingTimeLimit", "()I", "component1", "component2", "component3", "component4", "copy", "(ZILjava/lang/Float;Ljava/lang/Integer;)Lcom/unifiedserver/dashboard/data/models/AutoStopConfig;", "equals", "other", "hashCode", "toString", "", "app_debug"})
public final class AutoStopConfig {
    private final boolean enabled = false;
    @com.google.gson.annotations.SerializedName(value = "delayMinutes")
    private final int seedingTimeLimit = 0;
    @com.google.gson.annotations.SerializedName(value = "ratioLimit")
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Float ratioLimit = null;
    @com.google.gson.annotations.SerializedName(value = "seedTimeLimit")
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer seedTimeLimit = null;
    
    public AutoStopConfig(boolean enabled, int seedingTimeLimit, @org.jetbrains.annotations.Nullable()
    java.lang.Float ratioLimit, @org.jetbrains.annotations.Nullable()
    java.lang.Integer seedTimeLimit) {
        super();
    }
    
    public final boolean getEnabled() {
        return false;
    }
    
    public final int getSeedingTimeLimit() {
        return 0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Float getRatioLimit() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getSeedTimeLimit() {
        return null;
    }
    
    public AutoStopConfig() {
        super();
    }
    
    public final boolean component1() {
        return false;
    }
    
    public final int component2() {
        return 0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Float component3() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.unifiedserver.dashboard.data.models.AutoStopConfig copy(boolean enabled, int seedingTimeLimit, @org.jetbrains.annotations.Nullable()
    java.lang.Float ratioLimit, @org.jetbrains.annotations.Nullable()
    java.lang.Integer seedTimeLimit) {
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