package com.unifiedserver.dashboard.ui.screens

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Queue
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VideoLibrary
import com.unifiedserver.dashboard.ui.navigation.AppTopBar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unifiedserver.dashboard.data.models.QueueStatusResponse
import com.unifiedserver.dashboard.data.models.Show
import com.unifiedserver.dashboard.data.models.TmdbSearchResult
import com.unifiedserver.dashboard.ui.components.*
import com.unifiedserver.dashboard.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowsScreen(
    viewModel: ShowsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showAddDialog by remember { mutableStateOf(false) }
    var showSeasonSheet by rememberSaveable { mutableStateOf(false) }
    var showQueueSheet by remember { mutableStateOf(false) }
    var deleteShow by remember { mutableStateOf<Show?>(null) }
    var confirmClearQueue by rememberSaveable { mutableStateOf(false) }

    RefreshWhileVisible(30_000) { viewModel.refresh() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        topBar = {
            AppTopBar("Media") {
                uiState.queueStatus?.takeIf { it.queueLength > 0 || it.processing }?.let { queue ->
                    IconButton(onClick = { showQueueSheet = true }) {
                        BadgedBox(badge = { Badge { Text(queue.queueLength.toString()) } }) {
                            Icon(Icons.Filled.Queue, contentDescription = "Search queue, ${queue.queueLength} waiting")
                        }
                    }
                }
                IconButton(onClick = { showSeasonSheet = true }) {
                    Icon(Icons.Filled.VideoLibrary, contentDescription = "Download a season")
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = Primary, contentColor = OnPrimary) {
                Icon(Icons.Filled.Add, contentDescription = "Track a show")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Background
    ) { innerPadding ->
        RefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            when {
                uiState.error != null && uiState.shows.isEmpty() ->
                    ErrorScreen(message = uiState.error ?: "Failed to load shows", onRetry = { viewModel.refresh() })
                else -> LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 88.dp)) {
                    item {
                        AutoCheckCard(
                            enabled = uiState.autoCheckEnabled,
                            ready = uiState.autoCheckReady,
                            error = uiState.automationError,
                            activeCount = uiState.shows.count { it.status == "active" },
                            totalCount = uiState.shows.size,
                            isChecking = uiState.isCheckingEpisodes,
                            onToggle = { viewModel.setAutoCheckEnabled(it) },
                            onCheckNow = { viewModel.checkEpisodes() },
                            onRetry = { viewModel.refreshAutomation() }
                        )
                    }
                    if (uiState.shows.isEmpty() && !uiState.isLoading) item { EmptyShowsView() }
                    items(uiState.shows, key = { it.id }) { show ->
                        ShowItem(
                            show = show,
                            onToggleStatus = { viewModel.toggleShowStatus(show) },
                            onDownloadSeason = { viewModel.downloadSeason(show) },
                            onDelete = { deleteShow = show }
                        )
                    }
                }
            }
        }
    }

    if (showSeasonSheet) {
        ModalBottomSheet(onDismissRequest = { showSeasonSheet = false }) {
            SeasonDownloadTab(
                uiState = uiState,
                onSearch = { viewModel.searchTmdb(it) },
                onDownload = { tmdbId, name, season -> viewModel.downloadSeasonPack(tmdbId, name, season) },
                onClearProgress = { viewModel.clearDownloadProgress() }
            )
        }
    }

    if (showAddDialog) {
        AddShowDialog(
            isTmdbConfigured = uiState.tmdbConfigured,
            isSearching = uiState.isSearchingTmdb,
            searchResults = uiState.tmdbSearchResults,
            searchError = uiState.tmdbSearchError,
            onSearch = { viewModel.searchTmdb(it) },
            onDismiss = {
                viewModel.clearTmdbSearch()
                showAddDialog = false
            },
            onAdd = { name, tmdbId, season, episode ->
                    viewModel.addShow(name, tmdbId, season, episode) {
                        viewModel.clearTmdbSearch()
                        showAddDialog = false
                    }
            }
        )
    }

    if (showQueueSheet) {
        QueueStatusSheet(
            queueStatus = uiState.queueStatus,
            onClear = {
                confirmClearQueue = true
            },
            onDismiss = { showQueueSheet = false }
        )
    }
    deleteShow?.let { show ->
        AlertDialog(onDismissRequest = { deleteShow = null }, title = { Text("Stop tracking this show?") },
            text = { Text("Remove \"${show.name}\" from tracked shows? Downloaded files are not deleted.") },
            confirmButton = { TextButton(onClick = { viewModel.deleteShow(show.id); deleteShow = null }) { Text("Remove") } },
            dismissButton = { TextButton(onClick = { deleteShow = null }) { Text("Cancel") } })
    }
    if (confirmClearQueue) {
        AlertDialog(onDismissRequest = { confirmClearQueue = false }, title = { Text("Clear download queue?") },
            text = { Text("Remove all queued episode requests? This does not remove existing downloads.") },
            confirmButton = { TextButton(onClick = {
                viewModel.clearQueue(); confirmClearQueue = false; showQueueSheet = false
            }) { Text("Clear queue") } },
            dismissButton = { TextButton(onClick = { confirmClearQueue = false }) { Text("Cancel") } })
    }
}

@Composable
private fun AutoCheckCard(
    enabled: Boolean,
    ready: Boolean,
    error: String?,
    activeCount: Int,
    totalCount: Int,
    isChecking: Boolean,
    onToggle: (Boolean) -> Unit,
    onCheckNow: () -> Unit,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Auto-check new episodes", style = MaterialTheme.typography.bodyMedium, color = OnSurface)
                    Text(
                        if (totalCount == 0) "No shows tracked yet" else "$activeCount of $totalCount shows active",
                        style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(checked = enabled, enabled = ready, onCheckedChange = onToggle)
            }
            error?.let {
                Text("Auto-check unavailable: $it", style = MaterialTheme.typography.bodySmall, color = StatusYellow,
                    modifier = Modifier.padding(top = 8.dp))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isChecking) {
                    CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                    Text("Checking all active shows…", style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                } else {
                    Spacer(Modifier.weight(1f))
                    if (error != null) TextButton(onClick = onRetry) { Text("Retry") }
                    TextButton(onClick = onCheckNow, enabled = activeCount > 0) {
                        Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Check now")
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyShowsView() {
    Column(Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("No shows tracked", style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(8.dp))
        Text("Tap + to track a TV show. New episodes download automatically.",
            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddShowDialog(
    isTmdbConfigured: Boolean,
    isSearching: Boolean,
    searchResults: List<TmdbSearchResult>,
    searchError: String?,
    onSearch: (String) -> Unit,
    onDismiss: () -> Unit,
    onAdd: (name: String, tmdbId: Int?, season: Int, episode: Int) -> Unit
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var manualName by rememberSaveable { mutableStateOf("") }
    var season by rememberSaveable { mutableStateOf("1") }
    var episode by rememberSaveable { mutableStateOf("1") }
    var selectedResult by remember { mutableStateOf<TmdbSearchResult?>(null) }
    var useManual by remember { mutableStateOf(!isTmdbConfigured) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("Add TV Show", style = MaterialTheme.typography.titleMedium, color = OnSurface)
            Spacer(modifier = Modifier.height(12.dp))

            if (isTmdbConfigured) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = !useManual,
                        onCheckedChange = { useManual = !it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Primary, checkedTrackColor = PrimaryVariant)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (useManual) "Manual entry" else "Search TMDB",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (!useManual && isTmdbConfigured) {
                // TMDB Search
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search show name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        if (searchQuery.isNotBlank()) onSearch(searchQuery)
                    }),
                    trailingIcon = {
                        if (isSearching) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Primary, strokeWidth = 2.dp)
                        } else if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Filled.Close, contentDescription = "Clear show search", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = Divider,
                        focusedLabelColor = Primary,
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { if (searchQuery.isNotBlank()) onSearch(searchQuery) },
                    enabled = searchQuery.isNotBlank() && !isSearching,
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("Search")
                }
                if (searchError != null) {
                    Text(searchError, color = StatusRed, style = MaterialTheme.typography.bodySmall)
                }
                if (searchResults.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                        items(searchResults) { result ->
                            TmdbResultItem(
                                result = result,
                                isSelected = selectedResult?.id == result.id,
                                onClick = { selectedResult = result }
                            )
                        }
                    }
                }
            } else {
                // Manual entry
                OutlinedTextField(
                    value = manualName,
                    onValueChange = { manualName = it },
                    label = { Text("Show name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = Divider,
                        focusedLabelColor = Primary,
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            val nameForDisplay = if (useManual) manualName else selectedResult?.name.orEmpty()
            if (nameForDisplay.isNotBlank()) {
                Text(
                    text = "Show: $nameForDisplay",
                    style = MaterialTheme.typography.bodySmall,
                    color = Primary
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = season,
                    onValueChange = { season = it.filter { c -> c.isDigit() } },
                    label = { Text("Season") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = Divider,
                        focusedLabelColor = Primary,
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface
                    )
                )
                OutlinedTextField(
                    value = episode,
                    onValueChange = { episode = it.filter { c -> c.isDigit() } },
                    label = { Text("Episode") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = Divider,
                        focusedLabelColor = Primary,
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.width(8.dp))
                val finalName = if (useManual) manualName else selectedResult?.name.orEmpty()
                Button(
                    onClick = {
                        onAdd(
                            finalName,
                            if (useManual) null else selectedResult?.id,
                            season.toIntOrNull() ?: 1,
                            episode.toIntOrNull() ?: 1
                        )
                    },
                    enabled = finalName.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("Add Show")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun TmdbResultItem(
    result: TmdbSearchResult,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PrimaryVariant.copy(alpha = 0.2f) else SurfaceVariant
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = result.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSelected) Primary else OnSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (result.firstAirDate?.isNotBlank() == true) {
                    Text(
                        text = result.firstAirDate.take(4),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (isSelected) {
                StatusDot(color = Primary, size = 10.dp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueStatusSheet(
    queueStatus: QueueStatusResponse?,
    onClear: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Surface) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Download Queue", style = MaterialTheme.typography.titleMedium, color = OnSurface)
            Spacer(modifier = Modifier.height(12.dp))

            if (queueStatus == null) {
                Text("No queue data", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                Text(
                    text = "${queueStatus.queueLength} item(s) queued",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurface
                )
                if (queueStatus.processing) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Primary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Processing...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                if (queueStatus.queuedItems.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 300.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(queueStatus.queuedItems) { item ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(item.showName, color = OnSurface, style = MaterialTheme.typography.bodySmall)
                                    Text(
                                        "S${item.season}E${item.episode} · ${item.searchType}",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    }
                }
                if (queueStatus.queueLength > 0) {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = onClear,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusRed)
                    ) {
                        Text("Clear Queue")
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SeasonDownloadTab(
    uiState: ShowsUiState,
    onSearch: (String) -> Unit,
    onDownload: (tmdbId: Int, name: String, season: Int) -> Unit,
    onClearProgress: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedResult by remember { mutableStateOf<TmdbSearchResult?>(null) }
    var season by remember { mutableStateOf("1") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
    ) {
        Text("Download a season", style = MaterialTheme.typography.titleMedium, color = OnSurface)
        Text(
            text = "Find a show and get a whole season, as a pack when one exists.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search show name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                if (searchQuery.isNotBlank()) onSearch(searchQuery)
            }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = Divider,
                focusedLabelColor = Primary,
                focusedTextColor = OnSurface,
                unfocusedTextColor = OnSurface
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { if (searchQuery.isNotBlank()) onSearch(searchQuery) },
            enabled = searchQuery.isNotBlank() && !uiState.isSearchingTmdb,
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Text("Search")
        }

        if (uiState.tmdbSearchResults.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn(
                modifier = Modifier.weight(1f, fill = false),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(uiState.tmdbSearchResults) { result ->
                    TmdbResultItem(
                        result = result,
                        isSelected = selectedResult?.id == result.id,
                        onClick = { selectedResult = result }
                    )
                }
            }
        }

        selectedResult?.let { show ->
            Spacer(modifier = Modifier.height(12.dp))
            Text("Selected: ${show.name}", color = Primary, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = season,
                onValueChange = { season = it.filter { c -> c.isDigit() } },
                label = { Text("Season number") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = Divider,
                    focusedLabelColor = Primary,
                    focusedTextColor = OnSurface,
                    unfocusedTextColor = OnSurface
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    onDownload(show.id, show.name, season.toIntOrNull() ?: 1)
                },
                enabled = !uiState.isDownloadingSeason,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                if (uiState.isDownloadingSeason) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = OnPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Downloading...")
                } else {
                    Text("Download Season")
                }
            }
        }

        uiState.downloadProgress?.let { progress ->
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = progress.message ?: progress.status,
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { (progress.progress / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth(),
                        color = Primary,
                        trackColor = SurfaceVariant
                    )
                    if (!uiState.isDownloadingSeason) {
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = onClearProgress) {
                            Text("Dismiss", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
