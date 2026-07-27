package com.unifiedserver.dashboard.data.models;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0017\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001BG\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\u0007\u0012\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n\u0012\b\b\u0002\u0010\f\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\rJ\t\u0010\u0018\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0007H\u00c6\u0003J\u000f\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u000b0\nH\u00c6\u0003J\t\u0010\u001d\u001a\u00020\u0003H\u00c6\u0003JK\u0010\u001e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00072\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n2\b\b\u0002\u0010\f\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u001f\u001a\u00020\u00052\b\u0010 \u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010!\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\"\u001a\u00020#H\u00d6\u0001R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\b\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000fR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\f\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0014\u00a8\u0006$"}, d2 = {"Lcom/unifiedserver/dashboard/data/models/QueueStatusResponse;", "", "queueLength", "", "processing", "", "lastSearchTime", "", "nextSearchIn", "queuedItems", "", "Lcom/unifiedserver/dashboard/data/models/QueueItem;", "searchInterval", "(IZJJLjava/util/List;I)V", "getLastSearchTime", "()J", "getNextSearchIn", "getProcessing", "()Z", "getQueueLength", "()I", "getQueuedItems", "()Ljava/util/List;", "getSearchInterval", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "other", "hashCode", "toString", "", "app_debug"})
public final class QueueStatusResponse {
    private final int queueLength = 0;
    private final boolean processing = false;
    private final long lastSearchTime = 0L;
    private final long nextSearchIn = 0L;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.unifiedserver.dashboard.data.models.QueueItem> queuedItems = null;
    private final int searchInterval = 0;
    
    public QueueStatusResponse(int queueLength, boolean processing, long lastSearchTime, long nextSearchIn, @org.jetbrains.annotations.NotNull()
    java.util.List<com.unifiedserver.dashboard.data.models.QueueItem> queuedItems, int searchInterval) {
        super();
    }
    
    public final int getQueueLength() {
        return 0;
    }
    
    public final boolean getProcessing() {
        return false;
    }
    
    public final long getLastSearchTime() {
        return 0L;
    }
    
    public final long getNextSearchIn() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.unifiedserver.dashboard.data.models.QueueItem> getQueuedItems() {
        return null;
    }
    
    public final int getSearchInterval() {
        return 0;
    }
    
    public QueueStatusResponse() {
        super();
    }
    
    public final int component1() {
        return 0;
    }
    
    public final boolean component2() {
        return false;
    }
    
    public final long component3() {
        return 0L;
    }
    
    public final long component4() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.unifiedserver.dashboard.data.models.QueueItem> component5() {
        return null;
    }
    
    public final int component6() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.unifiedserver.dashboard.data.models.QueueStatusResponse copy(int queueLength, boolean processing, long lastSearchTime, long nextSearchIn, @org.jetbrains.annotations.NotNull()
    java.util.List<com.unifiedserver.dashboard.data.models.QueueItem> queuedItems, int searchInterval) {
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