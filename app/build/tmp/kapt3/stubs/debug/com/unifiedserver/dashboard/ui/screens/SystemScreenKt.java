package com.unifiedserver.dashboard.ui.screens;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000>\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\u001a\u0010\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\u0007\u001a\u0010\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0006H\u0007\u001a\u0010\u0010\u0007\u001a\u00020\u00012\u0006\u0010\b\u001a\u00020\tH\u0007\u001a\u0018\u0010\n\u001a\u00020\u00012\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\fH\u0007\u001a,\u0010\u000e\u001a\u00020\u00012\u0006\u0010\u000f\u001a\u00020\f2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00010\u00112\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00010\u0011H\u0007\u001a$\u0010\u0013\u001a\u00020\u00012\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00010\u00112\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00010\u0011H\u0007\u001a\u0010\u0010\u0016\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u0018H\u0007\u001a\u0010\u0010\u0019\u001a\u00020\u00012\u0006\u0010\b\u001a\u00020\tH\u0007\u001a\u0012\u0010\u001a\u001a\u00020\u00012\b\b\u0002\u0010\u001b\u001a\u00020\u001cH\u0007\u00a8\u0006\u001d"}, d2 = {"DiskItem", "", "disk", "Lcom/unifiedserver/dashboard/data/models/DiskDevice;", "DockerContainerItem", "container", "Lcom/unifiedserver/dashboard/data/models/DockerContainer;", "FlatSystemInfoSection", "info", "Lcom/unifiedserver/dashboard/data/models/SystemInfo;", "InfoCard", "label", "", "value", "PowerConfirmDialog", "action", "onConfirm", "Lkotlin/Function0;", "onDismiss", "PowerControlsSection", "onReboot", "onShutdown", "ServiceItem", "service", "Lcom/unifiedserver/dashboard/data/models/ServiceStatus;", "SystemResourcesSection", "SystemScreen", "viewModel", "Lcom/unifiedserver/dashboard/ui/screens/SystemViewModel;", "app_debug"})
public final class SystemScreenKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void SystemScreen(@org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.ui.screens.SystemViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void FlatSystemInfoSection(@org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.data.models.SystemInfo info) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void InfoCard(@org.jetbrains.annotations.NotNull()
    java.lang.String label, @org.jetbrains.annotations.NotNull()
    java.lang.String value) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void SystemResourcesSection(@org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.data.models.SystemInfo info) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void ServiceItem(@org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.data.models.ServiceStatus service) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void DiskItem(@org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.data.models.DiskDevice disk) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void DockerContainerItem(@org.jetbrains.annotations.NotNull()
    com.unifiedserver.dashboard.data.models.DockerContainer container) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void PowerControlsSection(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onReboot, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onShutdown) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void PowerConfirmDialog(@org.jetbrains.annotations.NotNull()
    java.lang.String action, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onConfirm, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss) {
    }
}