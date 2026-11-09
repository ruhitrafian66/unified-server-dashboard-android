package com.unifiedserver.dashboard.ui.screens

import androidx.lifecycle.compose.collectAsStateWithLifecycle

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
import com.unifiedserver.dashboard.data.models.DiskUsage
import com.unifiedserver.dashboard.data.models.DockerContainer
import com.unifiedserver.dashboard.data.models.ServiceStatus
import com.unifiedserver.dashboard.ui.utils.formatBytes
import kotlin.math.roundToInt
import com.unifiedserver.dashboard.ui.components.*
import com.unifiedserver.dashboard.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemScreen(
    viewModel: SystemViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    RefreshWhileVisible(15_000) { viewModel.loadAll(silent = uiState.overview != null) }
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
                title = { Text("Server", style = MaterialTheme.typography.titleLarge) },
                actions = { com.unifiedserver.dashboard.ui.navigation.SettingsAction() },
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
            if (uiState.overview == null && uiState.error == null) {
                LoadingScreen()
            } else if (uiState.error != null && uiState.overview == null) {
                ErrorScreen(
                    message = uiState.error ?: "Could not reach server",
                    onRetry = { viewModel.refresh() }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item { RefreshStatus(uiState.error, uiState.lastUpdated) { viewModel.refresh() } }
                    // CPU Monitoring
                    uiState.cpuMetrics?.let { metrics ->
                        item { SectionHeader("CPU Monitoring") }
                        item {
                            CpuGraphCard(
                                currentUsage = metrics.currentUsage,
                                averageUsage = metrics.averageUsage,
                                maxUsage = metrics.maxUsage,
                                minUsage = metrics.minUsage,
                                dataPoints = metrics.dataPoints,
                                cpuCount = metrics.cpuCount,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }

                    uiState.overview?.let { overview ->
                        unavailableSections(overview).takeIf { it.isNotEmpty() }?.let { missing ->
                            item { InlineWarning("Some data is unavailable: ${missing.joinToString()}.") }
                        }
                        item { SectionHeader("System Resources", modifier = Modifier.padding(top = 8.dp)) }
                        item { ResourceTiles(overview) }
                        overview.deploy?.commit?.let { commit ->
                            item {
                                Text("Dashboard build $commit", style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                            }
                        }

                        val disks = overviewDisks(overview)
                        if (disks.isNotEmpty()) {
                            item { SectionHeader("Storage", modifier = Modifier.padding(top = 8.dp)) }
                            items(disks, key = { it.first }) { (label, disk) -> DiskItem(label = label, disk = disk) }
                        }
                        systemDiskWarning(overview)?.let { item { InlineWarning(it) } }

                        item { SectionHeader("Downloads & VPN", modifier = Modifier.padding(top = 8.dp)) }
                        item { DownloadsAndVpnRows(overview) }

                        val services = overview.services.orEmpty()
                        if (services.isNotEmpty()) {
                            item { SectionHeader("Services", modifier = Modifier.padding(top = 8.dp)) }
                            items(services) { service -> ServiceItem(service = service) }
                        }

                        val containers = overview.containers.orEmpty()
                        if (containers.isNotEmpty()) {
                            item { SectionHeader("Docker Containers", modifier = Modifier.padding(top = 8.dp)) }
                            items(containers) { container -> DockerContainerItem(container = container) }
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
                    text = listOfNotNull(service.status, service.sub).joinToString(" · "),
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
fun DiskItem(label: String, disk: DiskUsage) {
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
                Text(text = label, style = MaterialTheme.typography.bodyMedium, color = OnSurface)
                Text(
                    text = disk.mountPoint,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            val pct = disk.percent
            LinearProgressIndicator(
                progress = { (pct / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = levelColor(pct),
                trackColor = SurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${formatBytes(disk.available)} free of ${formatBytes(disk.size)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${pct.roundToInt()}% used",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (pct > 65f) levelColor(pct) else MaterialTheme.colorScheme.onSurfaceVariant
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
            StatusDot(color = healthColor(containerHealth(container)))
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
                text = if (container.running && container.healthy == false) "unhealthy" else container.state,
                color = when (container.state.lowercase()) {
                    "running" -> if (container.healthy == false) StatusYellow else StatusGreen
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
