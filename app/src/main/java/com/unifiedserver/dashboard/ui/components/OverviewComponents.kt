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
import com.unifiedserver.dashboard.data.models.DiskVolume
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
    "storage" to "media storage", "volumes" to "disk details", "services" to "services", "containers" to "containers",
    "torrentClient" to "qBittorrent"
)

fun unavailableSections(overview: SystemOverview): List<String> =
    overview.errors.orEmpty().keys.map { SECTION_NAMES[it] ?: it }

/**
 * Physical volumes as the server detected them. Older servers only send the root and
 * media readings: those become one volume each (or one, if they are the same mount).
 */
fun overviewVolumes(overview: SystemOverview): List<DiskVolume> {
    val disks = overview.disks ?: return emptyList()
    disks.volumes?.takeIf { it.isNotEmpty() }?.let { return it }
    fun DiskUsage.asVolume(id: String, role: String) =
        DiskVolume(id = id, mounts = listOf(mountPoint), roles = listOf(role),
            size = size, used = used, available = available, percent = percent)
    return buildList {
        disks.storage?.let { add(it.asVolume("storage", "Media")) }
        disks.root?.takeIf { it.mountPoint != disks.storage?.mountPoint }?.let { add(it.asVolume("root", "System")) }
    }
}

private val DiskVolume.isSystem get() = "System" in roles
private val DiskVolume.isMedia get() = "Media" in roles

/** The volume downloads land on; on a single-disk server that is the system volume. */
fun mediaVolume(volumes: List<DiskVolume>): DiskVolume? =
    volumes.firstOrNull { it.isMedia } ?: volumes.firstOrNull { it.isSystem }

/** "USB SSD · System + Media" */
fun volumeTitle(volume: DiskVolume): String =
    listOf(volume.kind, volume.roles.joinToString(" + ")).filter { it.isNotBlank() }.joinToString(" · ")

/** "B SATA SSD · /dev/sda1 · /, /mnt/storage" */
fun volumeDetail(volume: DiskVolume): String =
    listOfNotNull(volume.model, volume.device, volume.mounts.joinToString().ifBlank { null }).joinToString(" · ")

/** One warning per system or media volume above 85%, naming the real device. */
fun diskWarnings(overview: SystemOverview): List<String> =
    overviewVolumes(overview).filter { it.percent > 85f && (it.isSystem || it.isMedia) }.map { v ->
        val full = "${v.percent.roundToInt()}% full — ${formatBytes(v.available)} left."
        val device = if (v.kind == "Disk") "" else " (${v.kind})"
        when {
            v.isSystem && v.isMedia -> {
                val disk = if (v.kind == "Disk") "disk" else v.kind
                val reserve = if (v.reserved > 0) "; system services keep a ${formatBytes(v.reserved)} reserve" else ""
                "The $disk holding both the system and your media is $full Downloads stop when it fills$reserve."
            }
            v.isSystem -> "System disk$device is $full"
            else -> "Media storage$device is $full"
        }
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
    val volumes = overviewVolumes(overview)
    val storage = mediaVolume(volumes)
    // One disk for everything: say which, since "media storage" is the system disk too.
    val storageLabel = if (storage != null && "System" in storage.roles && "Media" in storage.roles && storage.kind != "Disk")
        "Storage · ${storage.kind}" else "Media storage"
    val temperature = overview.temperature
    Column(modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ResourceTile("CPU", cpu?.let { "${it.roundToInt()}%" } ?: "—", Modifier.weight(1f), cpu,
                overview.load?.takeIf { it.isNotEmpty() }?.joinToString(" · ", prefix = "Load ") { "%.2f".format(it) })
            ResourceTile("Memory", memory?.let { "${it.percent.roundToInt()}%" } ?: "—", Modifier.weight(1f),
                memory?.percent, memory?.let { "${formatBytes(it.used)} of ${formatBytes(it.total)}" })
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ResourceTile(storageLabel, storage?.let { formatBytes(it.available) } ?: "—", Modifier.weight(1f),
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
