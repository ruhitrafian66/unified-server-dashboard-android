package com.unifiedserver.dashboard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unifiedserver.dashboard.data.models.DiskDevice
import com.unifiedserver.dashboard.data.models.DockerContainer
import com.unifiedserver.dashboard.data.models.ServiceStatus
import com.unifiedserver.dashboard.data.models.SystemInfo
import com.unifiedserver.dashboard.ui.components.*
import com.unifiedserver.dashboard.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemScreen(
    viewModel: SystemViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showPowerDialog by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("System", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Surface,
                    titleContentColor = OnSurface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Background
    ) { innerPadding ->
        RefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.error != null && uiState.systemInfo == null) {
                ErrorScreen(
                    message = uiState.error ?: "Could not reach server",
                    onRetry = { viewModel.refresh() }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    // System Info
                    uiState.systemInfo?.let { info ->
                        item { SectionHeader("System Resources") }
                        item { FlatSystemInfoSection(info = info) }
                    }

                    // Services
                    if (uiState.services.isNotEmpty()) {
                        item { SectionHeader("Services", modifier = Modifier.padding(top = 8.dp)) }
                        items(uiState.services) { service ->
                            ServiceItem(service = service)
                        }
                    }

                    // Disks
                    if (uiState.disks.isNotEmpty()) {
                        item { SectionHeader("Storage", modifier = Modifier.padding(top = 8.dp)) }
                        items(uiState.disks) { disk ->
                            DiskItem(disk = disk)
                        }
                    }

                    // Docker
                    if (uiState.dockerContainers.isNotEmpty()) {
                        item { SectionHeader("Docker Containers", modifier = Modifier.padding(top = 8.dp)) }
                        items(uiState.dockerContainers) { container ->
                            DockerContainerItem(container = container)
                        }
                    }

                    // Power Controls
                    item {
                        SectionHeader("Power", modifier = Modifier.padding(top = 8.dp))
                        PowerControlsSection(
                            onReboot = { showPowerDialog = "reboot" },
                            onShutdown = { showPowerDialog = "shutdown" }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }

    // Power confirmation dialog
    showPowerDialog?.let { action ->
        PowerConfirmDialog(
            action = action,
            onConfirm = {
                if (action == "reboot") viewModel.reboot() else viewModel.shutdown()
                showPowerDialog = null
            },
            onDismiss = { showPowerDialog = null }
        )
    }
}

@Composable
fun FlatSystemInfoSection(info: SystemInfo) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (info.cpu.isNotBlank()) {
            InfoCard(label = "CPU Usage", value = "${info.cpu}%")
        }
        if (info.memory.isNotBlank()) {
            InfoCard(label = "Memory", value = info.memory)
        }
        if (info.disk.isNotBlank()) {
            InfoCard(label = "Disk (root)", value = info.disk)
        }
        if (info.uptime.isNotBlank()) {
            InfoCard(label = "Uptime", value = info.uptime)
        }
    }
}

@Composable
fun InfoCard(label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurface
            )
        }
    }
}

@Composable
fun SystemResourcesSection(info: SystemInfo) {
    FlatSystemInfoSection(info = info)
}

@Composable
fun ServiceItem(service: ServiceStatus) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 3.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatusDot(color = if (service.running) StatusGreen else StatusRed)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = service.service,
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurface
                )
                Text(
                    text = service.status,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            StatusBadge(
                text = if (service.running) "Running" else "Stopped",
                color = if (service.running) StatusGreen else StatusRed
            )
        }
    }
}

@Composable
fun DiskItem(disk: DiskDevice) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 3.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = disk.mountPoint,
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurface
                )
                Text(
                    text = disk.device,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            val pct = disk.usedPercent
            LinearProgressIndicator(
                progress = { (pct / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = when {
                    pct > 90f -> StatusRed
                    pct > 70f -> StatusYellow
                    else -> Secondary
                },
                trackColor = SurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${disk.used} used of ${disk.size}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = disk.usePercent,
                    style = MaterialTheme.typography.labelSmall,
                    color = when {
                        pct > 90f -> StatusRed
                        pct > 70f -> StatusYellow
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}

@Composable
fun DockerContainerItem(container: DockerContainer) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 3.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatusDot(color = if (container.running) StatusGreen else StatusGray)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = container.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurface
                )
                Text(
                    text = container.image,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                Text(
                    text = container.status,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            StatusBadge(
                text = container.state,
                color = when (container.state.lowercase()) {
                    "running" -> StatusGreen
                    "exited", "dead" -> StatusRed
                    "paused" -> StatusYellow
                    else -> StatusGray
                }
            )
        }
    }
}

@Composable
fun PowerControlsSection(
    onReboot: () -> Unit,
    onShutdown: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onReboot,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusYellow),
            border = androidx.compose.foundation.BorderStroke(1.dp, StatusYellow.copy(alpha = 0.5f))
        ) {
            Icon(Icons.Filled.RestartAlt, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Reboot")
        }
        OutlinedButton(
            onClick = onShutdown,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusRed),
            border = androidx.compose.foundation.BorderStroke(1.dp, StatusRed.copy(alpha = 0.5f))
        ) {
            Icon(Icons.Filled.PowerSettingsNew, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Shutdown")
        }
    }
}

@Composable
fun PowerConfirmDialog(
    action: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val isReboot = action == "reboot"
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        title = {
            Text(
                if (isReboot) "Reboot Server?" else "Shutdown Server?",
                color = OnSurface
            )
        },
        text = {
            Text(
                text = if (isReboot)
                    "This will restart the server. Are you sure?"
                else
                    "This will shut down the server. You will need physical access to turn it back on. Are you sure?",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    if (isReboot) "Reboot" else "Shutdown",
                    color = if (isReboot) StatusYellow else StatusRed
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}
