package com.unifiedserver.dashboard.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay
import java.text.DateFormat
import java.util.Date

/** Refresh only while this navigation destination is resumed, never in a hidden tab. */
@Composable
fun RefreshWhileVisible(intervalMillis: Long, refresh: () -> Unit) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val currentRefresh by rememberUpdatedState(refresh)
    LaunchedEffect(lifecycle, intervalMillis) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            while (true) {
                currentRefresh()
                delay(intervalMillis)
            }
        }
    }
}

@Composable
fun RefreshStatus(error: String?, lastUpdated: Long?, retry: () -> Unit) {
    if (error == null && lastUpdated == null) return
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        lastUpdated?.let {
            Text("Last updated ${DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(it))}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        error?.let {
            Text("Refresh incomplete — $it", color = MaterialTheme.colorScheme.error)
            TextButton(onClick = retry) { Text("Retry") }
        }
    }
}
