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
import com.unifiedserver.dashboard.data.models.episodeCode
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
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

private fun airDate(value: String): String = runCatching {
    LocalDate.parse(value).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
}.getOrDefault(value)

@Composable
fun ShowCard(
    show: Show,
    onToggleStatus: () -> Unit,
    onDownloadSeason: () -> Unit
) {
    val isActive = show.status.equals("active", ignoreCase = true)
    Column(Modifier.fillMaxWidth().background(Background)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 4.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = show.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isActive) OnSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (!isActive) {
                        Spacer(Modifier.width(8.dp))
                        StatusBadge(text = "Paused", color = StatusGray)
                    }
                }
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(episodeCode(show.currentSeason, show.currentEpisode),
                        style = MaterialTheme.typography.labelMedium, color = Primary)
                    Text(
                        listOfNotNull(
                            show.nextEpisodeAirDate?.takeIf { it.isNotBlank() }?.let { "next ${airDate(it)}" },
                            "${show.downloadedEpisodes.size} downloaded"
                        ).joinToString(" · ", prefix = " · "),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                }
                show.pendingEpisode?.let { pending ->
                    Spacer(Modifier.height(6.dp))
                    StatusBadge(text = "${pending.code} aired · waiting for a release", color = StatusYellow)
                }
                show.missedEpisodes?.takeIf { it.isNotEmpty() }?.let { missed ->
                    Spacer(Modifier.height(6.dp))
                    StatusBadge(text = "Missed ${missed.joinToString { it.code }} · search manually", color = StatusRed)
                }
            }
            IconButton(onClick = onToggleStatus) {
                Icon(
                    imageVector = if (isActive) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isActive) "Pause tracking ${show.name}" else "Resume tracking ${show.name}",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDownloadSeason) {
                Icon(
                    imageVector = Icons.Filled.Download,
                    contentDescription = "Download season ${show.currentSeason} of ${show.name}",
                    tint = Primary
                )
            }
        }
        HorizontalDivider(color = Divider, modifier = Modifier.padding(start = 16.dp))
    }
}
