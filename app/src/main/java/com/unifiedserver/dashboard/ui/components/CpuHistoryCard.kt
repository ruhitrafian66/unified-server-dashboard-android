package com.unifiedserver.dashboard.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.unifiedserver.dashboard.data.models.CpuDataPoint
import com.unifiedserver.dashboard.ui.theme.*
import java.text.DateFormat
import java.util.Date
import kotlin.math.roundToInt

/** CPU over the last hour; the live value is on the CPU tile. */
@Composable
fun CpuHistoryCard(
    averageUsage: Float,
    maxUsage: Float,
    minUsage: Float,
    dataPoints: List<CpuDataPoint>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Last hour", style = MaterialTheme.typography.titleSmall, color = OnSurface)
            Text(
                "Average ${averageUsage.roundToInt()}% · peak ${maxUsage.roundToInt()}% · low ${minUsage.roundToInt()}%",
                style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))
            if (dataPoints.size < 2) {
                Text("Not enough samples yet.", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                CpuChart(dataPoints, Modifier.fillMaxWidth().height(140.dp))
                val time = DateFormat.getTimeInstance(DateFormat.SHORT)
                Row(Modifier.fillMaxWidth().padding(start = 36.dp, top = 4.dp)) {
                    Text(time.format(Date(dataPoints.first().timestamp)), style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                    Text(time.format(Date(dataPoints.last().timestamp)), style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun CpuChart(points: List<CpuDataPoint>, modifier: Modifier) {
    val measurer = rememberTextMeasurer()
    val labelStyle = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
    Canvas(modifier.semantics {
        contentDescription = "CPU history, ${points.size} samples, latest ${points.last().usage.roundToInt()} percent"
    }) {
        val gutter = 36.dp.toPx()
        val top = 6.dp.toPx()
        val bottom = size.height - 6.dp.toPx()
        val width = size.width - gutter
        fun y(value: Float) = bottom - (bottom - top) * (value.coerceIn(0f, 100f) / 100f)

        listOf(0f, 50f, 100f).forEach { level ->
            drawLine(SurfaceVariant, Offset(gutter, y(level)), Offset(size.width, y(level)), strokeWidth = 1.dp.toPx())
            val label = measurer.measure("${level.toInt()}%", labelStyle)
            drawText(label, topLeft = Offset(gutter - label.size.width - 6.dp.toPx(), y(level) - label.size.height / 2f))
        }

        val start = points.first().timestamp
        val span = (points.last().timestamp - start).coerceAtLeast(1L).toFloat()
        val coords = points.map { Offset(gutter + width * ((it.timestamp - start) / span), y(it.usage)) }
        val line = Path().apply {
            moveTo(coords.first().x, coords.first().y)
            coords.drop(1).forEach { lineTo(it.x, it.y) }
        }
        val area = Path().apply {
            addPath(line)
            lineTo(coords.last().x, bottom)
            lineTo(coords.first().x, bottom)
            close()
        }
        drawPath(area, Primary.copy(alpha = 0.15f))
        drawPath(line, Primary, style = Stroke(width = 2.dp.toPx()))
        drawCircle(Primary, radius = 3.5.dp.toPx(), center = coords.last())
    }
}
