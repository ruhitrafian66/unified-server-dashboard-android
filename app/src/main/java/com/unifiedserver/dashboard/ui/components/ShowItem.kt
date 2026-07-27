package com.unifiedserver.dashboard.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
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
import com.unifiedserver.dashboard.data.models.Show
import com.unifiedserver.dashboard.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowItem(
    show: Show,
    onToggleStatus: () -> Unit,
    onDownloadSeason: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                false
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
                label = "show_swipe_bg"
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
        ShowCard(show = show, onToggleStatus = onToggleStatus, onDownloadSeason = onDownloadSeason)
    }
}

@Composable
fun ShowCard(
    show: Show,
    onToggleStatus: () -> Unit,
    onDownloadSeason: () -> Unit
) {
    val isActive = show.status.equals("active", ignoreCase = true)
    val statusColor = if (isActive) StatusGreen else StatusGray

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(0.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StatusDot(color = statusColor)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = show.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "S${show.currentSeason.toString().padStart(2, '0')}E${show.currentEpisode.toString().padStart(2, '0')}",
                        style = MaterialTheme.typography.labelMedium,
                        color = Primary
                    )
                    if (!show.nextEpisodeAirDate.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Next: ${show.nextEpisodeAirDate}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${show.downloadedEpisodes.size} episode(s) downloaded",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    IconButton(
                        onClick = onToggleStatus,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isActive) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (isActive) "Pause tracking" else "Resume tracking",
                            tint = if (isActive) StatusYellow else StatusGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    IconButton(
                        onClick = onDownloadSeason,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Download,
                            contentDescription = "Download season",
                            tint = Primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            StatusBadge(
                text = if (isActive) "Active" else "Paused",
                color = statusColor
            )
        }
        HorizontalDivider(color = Divider, thickness = 0.5.dp)
    }
}
