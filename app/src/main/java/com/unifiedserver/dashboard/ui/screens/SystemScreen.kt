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
import com.unifiedserver.dashboard.data.models.DiskVolume
import com.unifiedserver.dashboard.data.models.DockerContainer
import com.unifiedserver.dashboard.data.models.ServiceStatus
import com.unifiedserver.dashboard.data.repository.HistoryMetric
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
            com.unifiedserver.dashboard.ui.navigation.AppTopBar("Server")
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
                    val overview = uiState.overview
                    overview?.let {
                        unavailableSections(it).takeIf { missing -> missing.isNotEmpty() }?.let { missing ->
                            item { InlineWarning("Some data is unavailable: ${missing.joinToString()}.") }
                        }
                        item { SectionHeader("Resources") }
                        item { ResourceTiles(it) }
                    }

                    item { SectionHeader("History", modifier = Modifier.padding(top = 8.dp)) }
                    // Temperature only on boards that report one, as on the web dashboard.
                    val metrics = HistoryMetric.entries.filter { m ->
                        m != HistoryMetric.TEMPERATURE || overview?.temperature != null
                    }
                    items(metrics, key = { m -> m.path }) { metric ->
                        val history = uiState.histories.getValue(metric)
                        HistoryCard(
                            scale = metric.scale(),
                            samples = history.samples.orEmpty(),
                            range = history.range,
                            onRangeChange = { range -> viewModel.selectRange(metric, range) },
                            loading = history.loading || history.samples == null,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }

                    overview?.let {
                        val volumes = overviewVolumes(it)
                        if (volumes.isNotEmpty()) {
                            item { SectionHeader("Storage", modifier = Modifier.padding(top = 8.dp)) }
                            items(volumes, key = { v -> v.id }) { volume -> DiskItem(volume) }
                        }
                        items(diskWarnings(it)) { warning -> InlineWarning(warning) }

                        item { SectionHeader("Downloads & VPN", modifier = Modifier.padding(top = 8.dp)) }
                        item { DownloadsAndVpnRows(it) }

                        val services = it.services.orEmpty()
                        if (services.isNotEmpty()) {
                            item { SectionHeader("Services", modifier = Modifier.padding(top = 8.dp)) }
                            items(services) { service -> ServiceItem(service = service) }
                        }

                        val containers = it.containers.orEmpty()
                        if (containers.isNotEmpty()) {
                            item { SectionHeader("Containers", modifier = Modifier.padding(top = 8.dp)) }
                            items(containers) { container -> DockerContainerItem(container = container) }
                        }
                    }

                    item {
                        SectionHeader("Power", modifier = Modifier.padding(top = 8.dp))
                        PowerControlsSection(
                            onReboot = { showPowerDialog = "reboot" },
                            onShutdown = { showPowerDialog = "shutdown" }
                        )
                    }

                    overview?.deploy?.commit?.let { commit ->
                        item {
                            Text("Dashboard backend build $commit", style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp))
                        }
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

/** One bar per physical volume: what it is, what it holds, and how full it is. */
@Composable
fun DiskItem(volume: DiskVolume) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 3.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val pct = volume.percent
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(volumeTitle(volume), style = MaterialTheme.typography.bodyMedium, color = OnSurface,
                    modifier = Modifier.weight(1f))
                Text(
                    text = "${pct.roundToInt()}% used",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (pct > 65f) levelColor(pct) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (pct / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = levelColor(pct),
                trackColor = SurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${formatBytes(volume.available)} free of ${formatBytes(volume.size)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            volumeDetail(volume).takeIf { it.isNotBlank() }?.let {
                Text(it, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
