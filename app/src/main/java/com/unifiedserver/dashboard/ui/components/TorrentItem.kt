package com.unifiedserver.dashboard.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.unifiedserver.dashboard.data.models.Torrent
import com.unifiedserver.dashboard.ui.theme.*
import com.unifiedserver.dashboard.ui.utils.formatBytes
import com.unifiedserver.dashboard.ui.utils.formatEta
import com.unifiedserver.dashboard.ui.utils.formatSpeed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TorrentItem(
    torrent: Torrent,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                false // keep item; let ViewModel handle removal
            }
            false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val color by animateColorAsState(
                targetValue = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart)
                    StatusRed else Color.Transparent,
                label = "swipe_bg"
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(end = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = Color.White
                    )
                }
            }
        },
        enableDismissFromStartToEnd = false,
        modifier = modifier
    ) {
        TorrentCard(torrent = torrent, onPause = onPause, onResume = onResume)
    }
}

@Composable
fun TorrentCard(
    torrent: Torrent,
    onPause: () -> Unit,
    onResume: () -> Unit
) {
    val stateColor = torrentStateColor(torrent.state)
    val stateLabel = torrentStateLabel(torrent.state)
    val isPaused = torrent.state.contains("paused", ignoreCase = true)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(0.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                StatusDot(color = stateColor)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = torrent.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = if (isPaused) onResume else onPause,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                        contentDescription = if (isPaused) "Resume" else "Pause",
                        tint = Primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { torrent.progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = stateColor,
                trackColor = SurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatusBadge(text = stateLabel, color = stateColor)
                Text(
                    text = "${(torrent.progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Size info
                Text(
                    text = formatBytes(torrent.completed) + " / " + formatBytes(torrent.size),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                // Ratio
                Text(
                    text = "Ratio: %.2f".format(torrent.ratio),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (torrent.dlspeed > 0 || torrent.upspeed > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (torrent.dlspeed > 0) {
                        Text(
                            text = "↓ ${formatSpeed(torrent.dlspeed)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = StateDownloading
                        )
                    }
                    if (torrent.upspeed > 0) {
                        Text(
                            text = "↑ ${formatSpeed(torrent.upspeed)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = StateUploading
                        )
                    }
                    if (torrent.eta > 0 && torrent.eta < 360000) {
                        Text(
                            text = "ETA: ${formatEta(torrent.eta)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        HorizontalDivider(color = Divider, thickness = 0.5.dp)
    }
}

fun torrentStateColor(state: String): Color = when (state.lowercase()) {
    "downloading" -> StateDownloading
    "uploading" -> StateUploading
    "pauseddl", "pausedup" -> StatePaused
    "error", "missingfiles" -> StateError
    "queueddl", "queuedup" -> StateQueued
    "checkingdl", "checkingup", "checkingresumdata" -> StateChecking
    "stalleddl" -> StateStalledDL
    "stalledup" -> StateStalledUP
    else -> StatePaused
}

fun torrentStateLabel(state: String): String = when (state.lowercase()) {
    "downloading" -> "Downloading"
    "uploading" -> "Seeding"
    "pauseddl" -> "Paused"
    "pausedup" -> "Paused (UP)"
    "error" -> "Error"
    "missingfiles" -> "Missing Files"
    "queueddl" -> "Queued"
    "queuedup" -> "Queued (UP)"
    "checkingdl" -> "Checking"
    "checkingup" -> "Checking (UP)"
    "checkingresumdata" -> "Checking Resume"
    "stalleddl" -> "Stalled"
    "stalledup" -> "Stalled (UP)"
    else -> state
}
