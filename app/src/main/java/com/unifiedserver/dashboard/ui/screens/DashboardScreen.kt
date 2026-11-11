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
import com.unifiedserver.dashboard.ui.components.*
import com.unifiedserver.dashboard.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val openStorage = com.unifiedserver.dashboard.ui.navigation.LocalOpenStorage.current
    RefreshWhileVisible(10_000) { viewModel.loadData(silent = uiState.health != null) }

    Scaffold(
        topBar = {
            com.unifiedserver.dashboard.ui.navigation.AppTopBar("Home")
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
                    item {
                        HealthBanner(
                            isOnline = if (uiState.error?.contains("Connection") == true) null else uiState.health?.status == "ok",
                            lastUpdated = uiState.lastUpdated,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    // Errors only: the banner already shows when data was last refreshed.
                    item { RefreshStatus(uiState.error, lastUpdated = null) { viewModel.refresh() } }

                    uiState.overview?.let { overview ->
                        unavailableSections(overview).takeIf { it.isNotEmpty() }?.let { missing ->
                            item { InlineWarning("Some data is unavailable: ${missing.joinToString()}.") }
                        }
                        item { SectionHeader("System", modifier = Modifier.padding(top = 8.dp)) }
                        item { ResourceTiles(overview) }
                        items(diskWarnings(overview)) { warning -> InlineWarning(warning, Modifier.padding(top = 8.dp)) }
                        item {
                            Row(
                                Modifier.fillMaxWidth().padding(top = 8.dp, end = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SectionHeader("Downloads & VPN")
                                TextButton(onClick = openStorage) { Text("Storage") }
                            }
                        }
                        item { DownloadsAndVpnRows(overview) }
                    }

                }
            }
        }
    }
}

@Composable
fun HealthBanner(
    isOnline: Boolean?,
    lastUpdated: Long? = null,
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
                color = color,
                modifier = Modifier.weight(1f)
            )
            lastUpdated?.let {
                Text(
                    text = "Updated ${java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT).format(java.util.Date(it))}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
