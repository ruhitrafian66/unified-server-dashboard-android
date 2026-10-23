package com.unifiedserver.dashboard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.unifiedserver.dashboard.ui.theme.*
import com.unifiedserver.dashboard.ui.utils.formatBytes
import com.unifiedserver.dashboard.ui.utils.formatEta
import com.unifiedserver.dashboard.ui.utils.formatSpeed
import com.unifiedserver.dashboard.data.models.CpuDataPoint
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Primary)
    }
}

@Composable
fun ErrorScreen(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.ErrorOutline,
            contentDescription = "Error",
            tint = StatusRed,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Unable to load",
            style = MaterialTheme.typography.titleMedium,
            color = OnSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Icon(Icons.Filled.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Retry")
        }
    }
}

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String? = null,
    color: Color = Primary,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = color
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun StatusBadge(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
fun StatusDot(
    color: Color,
    size: Dp = 8.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
fun LinearProgressCard(
    label: String,
    value: Float,
    used: String,
    total: String,
    color: Color = Primary,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$used / $total",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { value.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = when {
                    value > 0.9f -> StatusRed
                    value > 0.7f -> StatusYellow
                    else -> color
                },
                trackColor = SurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${(value * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Deprecated: use formatBytes from FormatUtils instead
fun formatBytes(bytes: Long): String {
    return com.unifiedserver.dashboard.ui.utils.formatBytes(bytes)
}

// Deprecated: use formatSpeed from FormatUtils instead
fun formatSpeed(bytesPerSec: Long): String {
    return com.unifiedserver.dashboard.ui.utils.formatSpeed(bytesPerSec)
}

// Deprecated: use formatEta from FormatUtils instead
fun formatEta(seconds: Long): String {
    return com.unifiedserver.dashboard.ui.utils.formatEta(seconds)
}

// Deprecated: use formatUptime from FormatUtils instead
fun formatUptime(seconds: Long): String {
    return com.unifiedserver.dashboard.ui.utils.formatUptime(seconds)
}

@Composable
fun CpuGraphCard(
    currentUsage: Float,
    averageUsage: Float,
    maxUsage: Float,
    minUsage: Float,
    dataPoints: List<CpuDataPoint>,
    cpuCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with title and current usage
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CPU Usage",
                        style = MaterialTheme.typography.titleMedium,
                        color = OnSurface
                    )
                    if (cpuCount > 0) {
                        Text(
                            text = "$cpuCount cores",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "${currentUsage.toInt()}%",
                        style = MaterialTheme.typography.titleLarge,
                        color = when {
                            currentUsage > 80f -> StatusRed
                            currentUsage > 60f -> StatusYellow
                            else -> Primary
                        }
                    )
                    Text(
                        text = "Current",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress bar for current usage
            LinearProgressIndicator(
                progress = { (currentUsage / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = when {
                    currentUsage > 80f -> StatusRed
                    currentUsage > 60f -> StatusYellow
                    else -> Primary
                },
                trackColor = SurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mini graph
            if (dataPoints.isNotEmpty()) {
                CpuMiniGraph(dataPoints = dataPoints, modifier = Modifier.fillMaxWidth().height(180.dp))
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatBox(label = "Avg", value = "${averageUsage.toInt()}%", modifier = Modifier.weight(1f))
                StatBox(label = "Max", value = "${maxUsage.toInt()}%", modifier = Modifier.weight(1f))
                StatBox(label = "Min", value = "${minUsage.toInt()}%", modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun StatBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(SurfaceVariant, RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            color = OnSurface
        )
    }
}

@Composable
fun CpuMiniGraph(
    dataPoints: List<CpuDataPoint>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .semantics {
                contentDescription = if (dataPoints.isEmpty()) "No CPU history available"
                else "CPU history: ${dataPoints.size} samples, latest ${dataPoints.last().usage.toInt()} percent"
            }
            .background(SurfaceVariant, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        if (dataPoints.isEmpty()) {
            Text(
                text = "No data yet",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            CpuLineGraph(dataPoints = dataPoints)
        }
    }
}

@Composable
fun CpuLineGraph(
    dataPoints: List<CpuDataPoint>,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            if (dataPoints.isEmpty()) return@Canvas

            val padding = 24.dp.toPx()
            val graphWidth = size.width - (padding * 2)
            val graphHeight = (size.height - (padding * 2)).coerceAtLeast(1f)

            val minValue = 0f
            val maxValue = 100f
            val range = maxValue - minValue

            // Draw grid background
            val gridLines = 4
            for (i in 0..gridLines) {
                val y = padding + (graphHeight / gridLines) * i
                drawLine(
                    color = Surface.copy(alpha = 0.3f),
                    start = androidx.compose.ui.geometry.Offset(padding, y),
                    end = androidx.compose.ui.geometry.Offset(size.width - padding, y),
                    strokeWidth = 1f
                )
            }

            // Draw vertical grid lines
            val verticalGridLines = minOf(dataPoints.size - 1, 10)
            if (verticalGridLines > 0) {
                for (i in 0..verticalGridLines) {
                    val x = padding + (graphWidth / verticalGridLines) * i
                    drawLine(
                        color = Surface.copy(alpha = 0.2f),
                        start = androidx.compose.ui.geometry.Offset(x, padding),
                        end = androidx.compose.ui.geometry.Offset(x, size.height - padding),
                        strokeWidth = 0.5f
                    )
                }
            }

            // Draw axes
            drawLine(
                color = OnSurface,
                start = androidx.compose.ui.geometry.Offset(padding, padding),
                end = androidx.compose.ui.geometry.Offset(padding, size.height - padding),
                strokeWidth = 2f
            )
            drawLine(
                color = OnSurface,
                start = androidx.compose.ui.geometry.Offset(padding, size.height - padding),
                end = androidx.compose.ui.geometry.Offset(size.width - padding, size.height - padding),
                strokeWidth = 2f
            )

            // Draw Y-axis labels (0, 25, 50, 75, 100)
            // Plot line
            val firstTime = dataPoints.first().timestamp
            val duration = (dataPoints.last().timestamp - firstTime).coerceAtLeast(1L)
            val points = dataPoints.map { sample ->
                val x = padding + graphWidth * ((sample.timestamp - firstTime).toDouble() / duration).toFloat()
                val normalizedValue = (sample.usage.coerceIn(0f, 100f) - minValue) / range
                val y = (size.height - padding) - (normalizedValue * graphHeight)
                androidx.compose.ui.geometry.Offset(x, y)
            }

            // Draw line segments
            for (i in 0 until points.size - 1) {
                drawLine(
                    color = when {
                        dataPoints[i].usage > 80f -> StatusRed
                        dataPoints[i].usage > 60f -> StatusYellow
                        else -> Primary
                    },
                    start = points[i],
                    end = points[i + 1],
                    strokeWidth = 2.5f
                )
            }

            // Draw data point circles
            points.forEachIndexed { index, point ->
                drawCircle(
                    color = when {
                        dataPoints[index].usage > 80f -> StatusRed
                        dataPoints[index].usage > 60f -> StatusYellow
                        else -> Primary
                    },
                    radius = 3f,
                    center = point
                )
            }

            // Draw current point highlight
            if (points.isNotEmpty()) {
                drawCircle(
                    color = when {
                        dataPoints.last().usage > 80f -> StatusRed
                        dataPoints.last().usage > 60f -> StatusYellow
                        else -> Primary
                    },
                    radius = 5f,
                    center = points.last()
                )
            }
        }

        // Y-axis labels
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(32.dp)
                .padding(top = 8.dp, bottom = 36.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.End
        ) {
            listOf(100, 75, 50, 25, 0).forEach { value ->
                Text(
                    text = "$value%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = MaterialTheme.typography.labelSmall.fontSize * 0.8f
                )
            }
        }

        // X-axis label (time)
        Text(
            text = if (dataPoints.isEmpty()) "Time" else {
                val format = java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT)
                "${format.format(java.util.Date(dataPoints.first().timestamp))} – ${format.format(java.util.Date(dataPoints.last().timestamp))}"
            },
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = MaterialTheme.typography.labelSmall.fontSize * 0.8f,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 4.dp)
        )
    }
}

