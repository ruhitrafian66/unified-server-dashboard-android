package com.unifiedserver.dashboard.ui.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.unifiedserver.dashboard.ui.theme.OnSurface
import com.unifiedserver.dashboard.ui.theme.Surface
import com.unifiedserver.dashboard.ui.theme.SurfaceVariant

/**
 * Format bytes to human-readable string (B, KB, MB, GB, TB)
 */
fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()
    return "%.1f %s".format(bytes / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups.coerceAtMost(4)])
}

/**
 * Format bytes per second to speed string (B/s, KB/s, MB/s, etc.)
 */
fun formatSpeed(bytesPerSec: Long): String {
    return "${formatBytes(bytesPerSec)}/s"
}

/**
 * Format seconds to human-readable ETA string
 * Returns "∞" for unlimited/no time remaining
 */
fun formatEta(seconds: Long): String {
    if (seconds <= 0 || seconds == Long.MAX_VALUE || seconds > 360000) return "∞"
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return when {
        h > 0 -> "${h}h ${m}m"
        m > 0 -> "${m}m ${s}s"
        else -> "${s}s"
    }
}

/**
 * Format uptime in seconds to human-readable uptime string
 * Examples: "2d 5h 30m", "3h 45m", "25m"
 */
fun formatUptime(seconds: Long): String {
    val d = seconds / 86400
    val h = (seconds % 86400) / 3600
    val m = (seconds % 3600) / 60
    return when {
        d > 0 -> "${d}d ${h}h ${m}m"
        h > 0 -> "${h}h ${m}m"
        else -> "${m}m"
    }
}

/**
 * Compose material text field colors for settings inputs
 */
@Composable
fun settingsTextFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = Surface,
    unfocusedContainerColor = Surface,
    focusedTextColor = OnSurface,
    unfocusedTextColor = OnSurface,
    cursorColor = MaterialTheme.colorScheme.primary,
    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
    unfocusedIndicatorColor = SurfaceVariant,
    disabledIndicatorColor = SurfaceVariant,
    disabledTextColor = SurfaceVariant
)
