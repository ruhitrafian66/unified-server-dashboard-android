package com.unifiedserver.dashboard.ui.screens;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0005\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\f\u001a\u00020\rJ\u0010\u0010\u000e\u001a\u00020\r2\b\b\u0002\u0010\u000f\u001a\u00020\u0010J\u0006\u0010\u0011\u001a\u00020\rJ\u0006\u0010\u0012\u001a\u00020\rJ\u0006\u0010\u0013\u001a\u00020\rJ\b\u0010\u0014\u001a\u00020\rH\u0002R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u00a8\u0006\u0015"}, d2 = {"Lcom/unifiedserver/dashboard/ui/screens/SystemViewModel;", "Landroidx/lifecycle/ViewModel;", "repository", "Lcom/unifiedserver/dashboard/data/repository/SystemRepository;", "(Lcom/unifiedserver/dashboard/data/repository/SystemRepository;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/unifiedserver/dashboard/ui/screens/SystemUiState;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "clearSnackbar", "", "loadAll", "silent", "", "reboot", "refresh", "shutdown", "startAutoRefresh", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class SystemViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.unifiedserver.dashboard.data.repository.SystemRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.unifiedserver.dashboard.ui.screens.SystemUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.unifiedserver.dashboard.ui.screens.SystemUiState> uiState = null;
    
    @javax.inject.Inject()
    public SystemViewModel(@org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.data.repository.SystemRepository repository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.unifiedserver.dashboard.ui.screens.SystemUiState> getUiState() {
        return null;
    }
    
    private final void startAutoRefresh() {
    }
    
    public final void loadAll(boolean silent) {
    }
    
    public final void reboot() {
    }
    
    public final void shutdown() {
    }
    
    public final void clearSnackbar() {
    }
    
    public final void refresh() {
    }
}