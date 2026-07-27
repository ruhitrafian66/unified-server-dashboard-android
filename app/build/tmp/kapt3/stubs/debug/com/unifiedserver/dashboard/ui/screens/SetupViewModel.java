package com.unifiedserver.dashboard.ui.screens;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u000e\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r\u00a8\u0006\u0012"}, d2 = {"Lcom/unifiedserver/dashboard/ui/screens/SetupViewModel;", "Landroidx/lifecycle/ViewModel;", "preferencesManager", "Lcom/unifiedserver/dashboard/data/PreferencesManager;", "dashboardRepository", "Lcom/unifiedserver/dashboard/data/repository/DashboardRepository;", "(Lcom/unifiedserver/dashboard/data/PreferencesManager;Lcom/unifiedserver/dashboard/data/repository/DashboardRepository;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/unifiedserver/dashboard/ui/screens/SetupUiState;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "testAndSave", "", "url", "", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class SetupViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.unifiedserver.dashboard.data.PreferencesManager preferencesManager = null;
    @org.jetbrains.annotations.NotNull()
    private final com.unifiedserver.dashboard.data.repository.DashboardRepository dashboardRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.unifiedserver.dashboard.ui.screens.SetupUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.unifiedserver.dashboard.ui.screens.SetupUiState> uiState = null;
    
    @javax.inject.Inject()
    public SetupViewModel(@org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.data.PreferencesManager preferencesManager, @org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.data.repository.DashboardRepository dashboardRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.unifiedserver.dashboard.ui.screens.SetupUiState> getUiState() {
        return null;
    }
    
    public final void testAndSave(@org.jetbrains.annotations.NotNull()
    java.lang.String url) {
    }
}