package com.unifiedserver.dashboard.ui.screens

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unifiedserver.dashboard.data.models.SystemInfo
import com.unifiedserver.dashboard.data.models.DiskDevice
import com.unifiedserver.dashboard.ui.components.*
import com.unifiedserver.dashboard.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val openDownloads = com.unifiedserver.dashboard.ui.navigation.LocalOpenDownloads.current
    RefreshWhileVisible(10_000) { viewModel.loadData(silent = uiState.health != null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home", style = MaterialTheme.typography.titleLarge) },
                actions = { com.unifiedserver.dashboard.ui.navigation.SettingsAction() },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Surface,
                    titleContentColor = OnSurface
                )
            )
        },
        containerColor = Background
    ) { innerPadding ->
        RefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.health == null && uiState.error == null) {
                LoadingScreen()
            } else if (uiState.error != null && uiState.health == null) {
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
                    // Health status banner
                    item {
                        HealthBanner(
                            isOnline = if (uiState.error?.contains("Connection") == true) null else uiState.health?.status == "ok",
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    // Quick stats
                    item { SectionHeader("Quick Stats") }
                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                StatCard(
                                    title = "Total",
                                    value = uiState.torrents.size.toString(),
                                    subtitle = "torrents",
                                    color = Primary,
                                    modifier = Modifier.width(140.dp)
                                )
                            }
                            item {
                                StatCard(
                                    title = "Downloading",
                                    value = uiState.torrents.count {
                                        it.state.equals("downloading", ignoreCase = true)
                                    }.toString(),
                                    color = StateDownloading,
                                    modifier = Modifier.width(140.dp)
                                )
                            }
                            item {
                                StatCard(
                                    title = "Seeding",
                                    value = uiState.torrents.count {
                                        it.state.equals("uploading", ignoreCase = true)
                                    }.toString(),
                                    color = StateUploading,
                                    modifier = Modifier.width(140.dp)
                                )
                            }
                            item {
                                StatCard(
                                    title = "Paused",
                                    value = uiState.torrents.count {
                                        it.state.contains("paused", ignoreCase = true)
                                    }.toString(),
                                    color = StatePaused,
                                    modifier = Modifier.width(140.dp)
                                )
                            }
                        }
                    }

                    // System info (flat strings from backend)
                    uiState.systemInfo?.let { info ->
                        item {
                            SectionHeader(
                                title = "System",
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        item {
                            SystemInfoCards(info = info, disks = uiState.disks)
                        }
                    }

                    // Active torrents preview
                    if (uiState.torrents.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = "Active Torrents",
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        val active = uiState.torrents
                            .filter { it.state.equals("downloading", ignoreCase = true) || it.state.equals("uploading", ignoreCase = true) }
                            .take(5)
                            .ifEmpty { uiState.torrents.take(3) }
                        items(active) { torrent ->
                            TorrentCard(
                                torrent = torrent,
                                onPause = openDownloads,
                                onResume = openDownloads,
                                readOnly = true,
                                onOpen = openDownloads
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SystemInfoCards(info: SystemInfo, disks: List<DiskDevice> = emptyList()) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // CPU
        if (info.cpu.isNotBlank()) {
            SystemInfoRow(label = "CPU", value = "${info.cpu}%")
        }
        // Memory: format from backend is "512/1024 MB (50.00%)"
        if (info.memory.isNotBlank()) {
            SystemInfoRow(label = "Memory", value = info.memory)
        }
        // Disk: prioritize external SSDs/storage over boot drive (mmcblk0)
        if (disks.isNotEmpty()) {
            val ssdDisk = com.unifiedserver.dashboard.ui.utils.orderedDisks(disks).firstOrNull()
            
            if (ssdDisk != null) {
                SystemInfoRow(
                    label = "Disk (${ssdDisk.device})",
                    value = "${ssdDisk.used}/${ssdDisk.size} (${ssdDisk.usePercent})"
                )
            }
        } else if (info.disk.isNotBlank()) {
            SystemInfoRow(label = "Disk (root)", value = info.disk)
        }
        // Uptime
        if (info.uptime.isNotBlank()) {
            SystemInfoRow(label = "Uptime", value = info.uptime)
        }
    }
}

@Composable
fun SystemInfoRow(label: String, value: String) {
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
fun HealthBanner(
    isOnline: Boolean?,
    modifier: Modifier = Modifier
) {
    val color = when (isOnline) { true -> StatusGreen; false -> StatusRed; null -> StatusYellow }
    val text = when (isOnline) { true -> "Server online"; false -> "Server offline"; null -> "Connection unavailable — status may be stale" }
    val icon = if (isOnline == true) Icons.Filled.CheckCircle else Icons.Filled.Error

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall,
                color = color
            )
        }
    }
}
