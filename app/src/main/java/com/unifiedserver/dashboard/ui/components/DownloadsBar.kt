package com.unifiedserver.dashboard.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.unifiedserver.dashboard.data.models.Torrent
import com.unifiedserver.dashboard.ui.screens.DownloadsBarViewModel
import com.unifiedserver.dashboard.ui.theme.*
import com.unifiedserver.dashboard.ui.utils.*
import kotlinx.coroutines.delay

private const val ROTATE_MS = 4_000L
private const val TOUCH_HOLD_MS = 6_000L

/**
 * Shown above the navigation bar only while torrents are downloading. With several
 * downloads it cycles through them (paused while touched); tap to stop, start or remove.
 */
@Composable
fun DownloadsBar(viewModel: DownloadsBarViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val active = state.active
    var sheetOpen by remember { mutableStateOf(false) }
    var index by remember { mutableIntStateOf(0) }
    var holdUntil by remember { mutableLongStateOf(0L) }

    RefreshWhileVisible(if (active.isNotEmpty()) 5_000 else 15_000) { viewModel.refresh() }

    LaunchedEffect(active.size) { if (index >= active.size) index = 0 }
    LaunchedEffect(active.size, sheetOpen) {
        if (active.size < 2 || sheetOpen) return@LaunchedEffect
        while (true) {
            delay(ROTATE_MS)
            if (System.currentTimeMillis() >= holdUntil) index = (index + 1) % active.size
        }
    }

    if (active.isNotEmpty()) {
        val current = active[index.coerceIn(0, active.lastIndex)]
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceVariant)
                .pointerInput(Unit) {
                    awaitEachGesture {
                        awaitFirstDown(requireUnconsumed = false)
                        holdUntil = System.currentTimeMillis() + TOUCH_HOLD_MS
                    }
                }
                .clickable { sheetOpen = true }
                .clearAndSetSemantics {
                    contentDescription = "${active.size} download${if (active.size > 1) "s" else ""} in progress. " +
                        "${prettyTorrentName(current.name)} ${(current.progress * 100).toInt()} percent."
                    onClick(label = "Open downloads") { sheetOpen = true; true }
                }
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(28.dp).clip(CircleShape).background(Primary.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Download, contentDescription = null, tint = Primary, modifier = Modifier.size(16.dp))
            }
            Spacer(Modifier.width(12.dp))
            AnimatedContent(
                targetState = current,
                transitionSpec = { slideInVertically { it } togetherWith slideOutVertically { -it } },
                contentKey = { it.hash },
                modifier = Modifier.weight(1f),
                label = "download-slide"
            ) { torrent -> DownloadSummary(torrent, compact = true) }
            if (active.size > 1) {
                Spacer(Modifier.width(12.dp))
                Text("${index + 1}/${active.size}", style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }

    if (sheetOpen) DownloadsSheet(viewModel, onDismiss = { sheetOpen = false })
}

@Composable
private fun DownloadSummary(torrent: Torrent, compact: Boolean) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(prettyTorrentName(torrent.name), style = MaterialTheme.typography.bodyMedium, color = OnSurface,
                maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
            Spacer(Modifier.width(8.dp))
            Text(if (compact) "${(torrent.progress * 100).toInt()}%" else "%.1f%%".format(torrent.progress * 100),
                style = MaterialTheme.typography.labelMedium, color = Primary)
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { torrent.progress.coerceIn(0.02f, 1f) },
            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
            color = if (torrent.isStopped) StatusGray else Primary,
            trackColor = SurfaceContainer
        )
        Spacer(Modifier.height(4.dp))
        val status = when {
            torrent.isStopped -> "Stopped"
            torrent.dlspeed > 0 -> "↓ ${formatSpeed(torrent.dlspeed)} · ${formatDownloadEta(torrent.eta)} left"
            else -> torrentStateLabel(torrent.state)
        }
        val size = if (compact) formatBytes(torrent.size) else "${formatBytes(torrent.completed)} of ${formatBytes(torrent.size)}"
        Text(
            listOfNotNull(status, size, if (compact) null else "${torrent.num_seeds} seeds").joinToString(" · "),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DownloadsSheet(viewModel: DownloadsBarViewModel, onDismiss: () -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var confirmRemove by remember { mutableStateOf<Torrent?>(null) }
    val rows = state.active + state.stoppedIncomplete
    val totalSpeed = state.active.sumOf { it.dlspeed }

    ModalBottomSheet(onDismissRequest = { viewModel.clearMessage(); onDismiss() }, containerColor = Surface) {
        Column(Modifier.padding(horizontal = 16.dp).padding(bottom = 24.dp)) {
            Text("Downloads", style = MaterialTheme.typography.titleMedium, color = OnSurface)
            Text(
                "${state.active.size} active" + if (totalSpeed > 0) " · ↓ ${formatSpeed(totalSpeed)}" else "",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            state.message?.let {
                Text(it, style = MaterialTheme.typography.bodySmall,
                    color = if (it.startsWith("Error")) StatusRed else StatusGreen, modifier = Modifier.padding(top = 8.dp))
            }
            Spacer(Modifier.height(8.dp))
            if (rows.isEmpty()) {
                Text("Nothing downloading right now.", style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 16.dp))
            }
            LazyColumn(Modifier.heightIn(max = 480.dp)) {
                items(rows, key = { it.hash }) { torrent ->
                    val busy = state.pendingHash == torrent.hash
                    Column(Modifier.padding(vertical = 10.dp)) {
                        DownloadSummary(torrent, compact = false)
                        Row(Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { if (torrent.isStopped) viewModel.start(torrent) else viewModel.stop(torrent) },
                                enabled = state.pendingHash == null
                            ) { Text(if (torrent.isStopped) "Start" else "Stop") }
                            OutlinedButton(onClick = { confirmRemove = torrent }, enabled = state.pendingHash == null) {
                                Text("Remove", color = StatusRed)
                            }
                            if (busy) CircularProgressIndicator(Modifier.size(20.dp).align(Alignment.CenterVertically), strokeWidth = 2.dp)
                        }
                    }
                    HorizontalDivider(color = Divider)
                }
            }
            Text("Finished downloads are in Storage.", style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 12.dp))
        }
    }

    confirmRemove?.let { torrent ->
        var deleteFiles by remember { mutableStateOf(true) }
        AlertDialog(
            onDismissRequest = { confirmRemove = null },
            title = { Text("Remove download?") },
            text = {
                Column {
                    Text("Remove \"${prettyTorrentName(torrent.name)}\" from qBittorrent?")
                    Row(Modifier.padding(top = 8.dp).clickable { deleteFiles = !deleteFiles },
                        verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = deleteFiles, onCheckedChange = { deleteFiles = it })
                        Text("Also delete the partly downloaded files")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.remove(torrent, deleteFiles); confirmRemove = null }) {
                    Text(if (deleteFiles) "Remove + delete files" else "Remove", color = StatusRed)
                }
            },
            dismissButton = { TextButton(onClick = { confirmRemove = null }) { Text("Cancel") } }
        )
    }
}
