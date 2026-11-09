package com.unifiedserver.dashboard.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.unifiedserver.dashboard.data.models.DiskUsage
import com.unifiedserver.dashboard.data.models.DockerContainer
import com.unifiedserver.dashboard.data.models.SystemOverview
import com.unifiedserver.dashboard.data.models.TorrentClientStatus
import com.unifiedserver.dashboard.ui.theme.*
import com.unifiedserver.dashboard.ui.utils.formatBytes
import com.unifiedserver.dashboard.ui.utils.formatSpeed
import com.unifiedserver.dashboard.ui.utils.formatUptime
import kotlin.math.roundToInt

enum class Health { OK, WARN, BAD, IDLE }

fun healthColor(health: Health): Color = when (health) {
    Health.OK -> StatusGreen
    Health.WARN -> StatusYellow
    Health.BAD -> StatusRed
    Health.IDLE -> StatusGray
}

fun levelColor(percent: Float): Color = when {
    percent > 85f -> StatusRed
    percent > 65f -> StatusYellow
    else -> StatusGreen
}

fun containerHealth(container: DockerContainer): Health = when {
    !container.running -> Health.BAD
    container.healthy == false -> Health.WARN
    else -> Health.OK
}

fun torrentConnection(client: TorrentClientStatus): Pair<Health, String> = when (client.connection) {
    "connected" -> Health.OK to "Connected"
    "firewalled" -> Health.WARN to "Firewalled (no incoming peers)"
    "disconnected" -> Health.BAD to "Disconnected"
    else -> Health.WARN to client.connection.ifBlank { "Unknown" }
}

/** The VPN container that qBittorrent shares its network with. */
fun vpnContainer(overview: SystemOverview): DockerContainer? =
    overview.containers.orEmpty().firstOrNull { it.name == "gluetun" }

/** Human names for the sections the server could not read this time. */
private val SECTION_NAMES = mapOf(
    "memory" to "memory", "uptime" to "uptime", "load" to "load", "rootDisk" to "system disk",
    "storage" to "media storage", "services" to "services", "containers" to "containers",
    "torrentClient" to "qBittorrent"
)

fun unavailableSections(overview: SystemOverview): List<String> =
    overview.errors.orEmpty().keys.map { SECTION_NAMES[it] ?: it }

/** Media storage first, then the system disk if it is a different mount. */
fun overviewDisks(overview: SystemOverview): List<Pair<String, DiskUsage>> {
    val storage = overview.disks?.storage
    val root = overview.disks?.root
    return buildList {
        storage?.let { add("Media storage" to it) }
        if (root != null && root.mountPoint != storage?.mountPoint) add("System disk" to root)
    }
}

/** The system disk is usually the SD card on this server: warn before it fills up. */
fun systemDiskWarning(overview: SystemOverview): String? {
    val root = overview.disks?.root ?: return null
    if (root.percent <= 85f || root.mountPoint == overview.disks.storage?.mountPoint) return null
    return "System disk (SD card) is ${root.percent.roundToInt()}% full — ${formatBytes(root.available)} left."
}

@Composable
fun ResourceTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    percent: Float? = null,
    sub: String? = null
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, color = OnSurface)
            percent?.let { pct ->
                Spacer(Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { (pct / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(5.dp)
                        .semantics { contentDescription = "$label ${pct.roundToInt()} percent" },
                    color = levelColor(pct),
                    trackColor = SurfaceVariant
                )
            }
            sub?.let {
                Spacer(Modifier.height(4.dp))
                Text(it, style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

/** CPU / memory / media storage / temperature, as on the web dashboard. */
@Composable
fun ResourceTiles(overview: SystemOverview, modifier: Modifier = Modifier) {
    val cpu = overview.cpu?.percent
    val memory = overview.memory
    val storage = overview.disks?.storage
    val temperature = overview.temperature
    Column(modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ResourceTile("CPU", cpu?.let { "${it.roundToInt()}%" } ?: "—", Modifier.weight(1f), cpu,
                overview.load?.takeIf { it.isNotEmpty() }?.joinToString(" · ", prefix = "Load ") { "%.2f".format(it) })
            ResourceTile("Memory", memory?.let { "${it.percent.roundToInt()}%" } ?: "—", Modifier.weight(1f),
                memory?.percent, memory?.let { "${formatBytes(it.used)} of ${formatBytes(it.total)}" })
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ResourceTile("Media storage", storage?.let { formatBytes(it.available) } ?: "—", Modifier.weight(1f),
                storage?.percent, storage?.let { "free · ${it.percent.roundToInt()}% used of ${formatBytes(it.size)}" })
            if (temperature != null) {
                ResourceTile("Temperature", "${temperature.roundToInt()}°C", Modifier.weight(1f),
                    temperature.coerceAtMost(100f), overview.uptime?.let { "Up ${formatUptime(it)}" })
            } else {
                ResourceTile("Uptime", overview.uptime?.let { formatUptime(it) } ?: "—", Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun StatusRow(health: Health, title: String, sub: String?, trailing: (@Composable () -> Unit)? = null) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 3.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            StatusDot(color = healthColor(health))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyMedium, color = OnSurface)
                sub?.let {
                    Text(it, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            trailing?.invoke()
        }
    }
}

/** VPN tunnel + qBittorrent connection and live speeds. */
@Composable
fun DownloadsAndVpnRows(overview: SystemOverview) {
    val vpn = vpnContainer(overview)
    StatusRow(
        health = vpn?.let { containerHealth(it) } ?: Health.IDLE,
        title = "VPN tunnel (gluetun)",
        sub = vpn?.status ?: "Container not found"
    )
    val client = overview.torrentClient
    if (client == null) {
        StatusRow(Health.BAD, "qBittorrent", overview.errors?.get("torrentClient") ?: "Unavailable")
    } else {
        val (health, label) = torrentConnection(client)
        StatusRow(health, "qBittorrent", "$label · ${client.dhtNodes} DHT nodes") {
            Column(horizontalAlignment = Alignment.End) {
                Text("↓ ${formatSpeed(client.dlSpeed)}", style = MaterialTheme.typography.labelSmall, color = StatusGreen)
                Text("↑ ${formatSpeed(client.upSpeed)}", style = MaterialTheme.typography.labelSmall, color = StatusBlue)
            }
        }
    }
}

@Composable
fun InlineWarning(text: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = StatusYellow.copy(alpha = 0.12f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text, Modifier.padding(12.dp), style = MaterialTheme.typography.bodySmall, color = StatusYellow)
    }
}
