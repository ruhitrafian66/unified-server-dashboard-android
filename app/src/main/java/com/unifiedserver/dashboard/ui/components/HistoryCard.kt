package com.unifiedserver.dashboard.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.unifiedserver.dashboard.data.models.HistorySample
import com.unifiedserver.dashboard.data.repository.HistoryMetric
import com.unifiedserver.dashboard.ui.theme.*
import java.text.DateFormat
import java.util.Date
import kotlin.math.roundToInt

/** Range tabs, as on the web dashboard. The server keeps 4 days. */
enum class HistoryRange(val label: String, val hours: Int) {
    H6("6h", 6), H12("12h", 12), H24("24h", 24), D2("2d", 48), D4("4d", 96)
}

/**
 * How a metric is drawn: a fixed [min]..[max] scale with gridlines at [ticks], and the
 * line coloured green / yellow / red above [warnAt] and [badAt].
 */
data class HistoryScale(
    val title: String,
    val min: Float,
    val max: Float,
    val ticks: List<Float>,
    val warnAt: Float,
    val badAt: Float,
    val tick: (Float) -> String,
    val value: (Float) -> String,
    val legend: List<String>
)

val CPU_SCALE = HistoryScale(
    title = "CPU", min = 0f, max = 100f, ticks = listOf(0f, 50f, 100f), warnAt = 60f, badAt = 80f,
    tick = { "${it.roundToInt()}%" }, value = { "${it.roundToInt()}%" },
    legend = listOf("Normal <60%", "High 60–80%", "Critical >80%")
)

// RK3566 SoC: comfortable below 60°C, warm above, throttles around 85°C.
val TEMPERATURE_SCALE = HistoryScale(
    title = "Temperature", min = 30f, max = 90f, ticks = listOf(30f, 60f, 90f), warnAt = 60f, badAt = 75f,
    tick = { "${it.roundToInt()}°" }, value = { "%.1f°C".format(it) },
    legend = listOf("Normal <60°C", "Warm 60–75°C", "Hot >75°C")
)

fun HistoryMetric.scale(): HistoryScale = when (this) {
    HistoryMetric.CPU -> CPU_SCALE
    HistoryMetric.TEMPERATURE -> TEMPERATURE_SCALE
}

data class HistoryStats(val now: Float, val average: Float, val peak: Float)

fun historyStats(samples: List<HistorySample>): HistoryStats? {
    if (samples.isEmpty()) return null
    return HistoryStats(samples.last().v, samples.map { it.v }.average().toFloat(), samples.maxOf { it.v })
}

/** The server samples once a minute; a longer silence than this means it was down. */
const val HISTORY_GAP_S = 5 * 60L

/**
 * The samples as drawable runs: split wherever more than [HISTORY_GAP_S] passed without
 * data, so the chart never draws a line through downtime, then thinned to roughly
 * [maxPoints] points in total by averaging (4 days is 5760 samples, far more than a
 * phone-width chart can show). Each run keeps its newest sample as-is, so the last run
 * ends on "now".
 */
fun historyRuns(samples: List<HistorySample>, maxPoints: Int): List<List<HistorySample>> {
    if (samples.isEmpty()) return emptyList()
    val runs = mutableListOf(mutableListOf(samples.first()))
    for (i in 1 until samples.size) {
        if (samples[i].t - samples[i - 1].t > HISTORY_GAP_S) runs += mutableListOf<HistorySample>()
        runs.last() += samples[i]
    }
    val perPoint = (samples.size + maxPoints - 1) / maxPoints.coerceAtLeast(1)
    if (perPoint <= 1) return runs
    return runs.map { run ->
        run.dropLast(1).chunked(perPoint) { chunk ->
            HistorySample(chunk.sumOf { it.t } / chunk.size, chunk.map { it.v }.average().toFloat())
        } + run.last()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryCard(
    scale: HistoryScale,
    samples: List<HistorySample>,
    range: HistoryRange,
    onRangeChange: (HistoryRange) -> Unit,
    modifier: Modifier = Modifier,
    loading: Boolean = false
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(scale.title, style = MaterialTheme.typography.titleSmall, color = OnSurface,
                    modifier = Modifier.weight(1f))
                historyStats(samples)?.let { s ->
                    Text("now ${scale.value(s.now)} · avg ${scale.value(s.average)} · peak ${scale.value(s.peak)}",
                        style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(8.dp))
            SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                HistoryRange.entries.forEachIndexed { i, r ->
                    SegmentedButton(
                        selected = r == range, onClick = { onRangeChange(r) },
                        shape = SegmentedButtonDefaults.itemShape(i, HistoryRange.entries.size),
                        icon = {}, label = { Text(r.label, style = MaterialTheme.typography.labelMedium) }
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            when {
                samples.size >= 2 -> {
                    val runs = remember(samples) { historyRuns(samples, 360) }
                    HistoryChart(scale, runs, Modifier.fillMaxWidth().height(140.dp))
                    val time = if (range.hours > 24) DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                        else DateFormat.getTimeInstance(DateFormat.SHORT)
                    Row(Modifier.fillMaxWidth().padding(start = 36.dp, top = 4.dp)) {
                        Text(time.format(Date(samples.first().t * 1000)), style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                        Text(time.format(Date(samples.last().t * 1000)), style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        listOf(StatusGreen, StatusYellow, StatusRed).zip(scale.legend).forEach { (color, text) ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                StatusDot(color = color)
                                Spacer(Modifier.width(4.dp))
                                Text(text, style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
                loading -> LinearProgressIndicator(Modifier.fillMaxWidth().padding(vertical = 8.dp))
                else -> Text("Not enough samples yet. The server records one per minute.",
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun HistoryChart(scale: HistoryScale, runs: List<List<HistorySample>>, modifier: Modifier) {
    val measurer = rememberTextMeasurer()
    val labelStyle = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
    val first = runs.first().first()
    val last = runs.last().last()
    Canvas(modifier.semantics {
        contentDescription = "${scale.title} history, latest ${scale.value(last.v)}"
    }) {
        val gutter = 36.dp.toPx()
        val top = 6.dp.toPx()
        val bottom = size.height - 6.dp.toPx()
        val width = size.width - gutter
        fun y(value: Float) = bottom - (bottom - top) * ((value.coerceIn(scale.min, scale.max) - scale.min) / (scale.max - scale.min))

        scale.ticks.forEach { level ->
            drawLine(SurfaceVariant, Offset(gutter, y(level)), Offset(size.width, y(level)), strokeWidth = 1.dp.toPx())
            val label = measurer.measure(scale.tick(level), labelStyle)
            drawText(label, topLeft = Offset(gutter - label.size.width - 6.dp.toPx(), y(level) - label.size.height / 2f))
        }

        // Hard colour stops at the band thresholds, so the line is red wherever the value is high.
        val stop = { v: Float -> (y(v) / size.height).coerceIn(0f, 1f) }
        val bands = { alpha: Float ->
            Brush.verticalGradient(
                0f to StatusRed.copy(alpha), stop(scale.badAt) to StatusRed.copy(alpha),
                stop(scale.badAt) to StatusYellow.copy(alpha), stop(scale.warnAt) to StatusYellow.copy(alpha),
                stop(scale.warnAt) to StatusGreen.copy(alpha), 1f to StatusGreen.copy(alpha),
                startY = 0f, endY = size.height
            )
        }

        val span = (last.t - first.t).coerceAtLeast(1L).toFloat()
        fun point(sample: HistorySample) = Offset(gutter + width * ((sample.t - first.t) / span), y(sample.v))
        // One line per run: the space between runs is time the server recorded nothing.
        for (run in runs) {
            val coords = run.map(::point)
            if (coords.size == 1) {
                // A lone sample between two gaps.
                drawCircle(bands(1f), radius = 1.5.dp.toPx(), center = coords.first())
                continue
            }
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
            drawPath(area, bands(0.15f))
            drawPath(line, bands(1f), style = Stroke(width = 2.dp.toPx()))
        }
        drawCircle(OnSurface, radius = 3.5.dp.toPx(), center = point(last))
    }
}
