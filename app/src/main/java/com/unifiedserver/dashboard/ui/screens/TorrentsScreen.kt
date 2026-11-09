package com.unifiedserver.dashboard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.unifiedserver.dashboard.data.repository.DownloadHandoff
import com.unifiedserver.dashboard.ui.components.*
import com.unifiedserver.dashboard.ui.navigation.SettingsAction
import com.unifiedserver.dashboard.ui.theme.*
import com.unifiedserver.dashboard.ui.utils.formatBytes

/**
 * Media › Torrents: search qBittorrent's plugins and pick a source, or paste a magnet link.
 * "Find sources" and "Auto-download" from a title's details land here. Running downloads
 * are in the downloads bar; finished ones are in Storage.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TorrentsScreen(
    handoff: DownloadHandoff,
    viewModel: TorrentsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val incoming by handoff.pendingQuery.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val keyboard = LocalSoftwareKeyboardController.current
    var query by rememberSaveable(uiState.searchQuery) { mutableStateOf(uiState.searchQuery) }
    var magnet by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(incoming) {
        incoming?.let { request ->
            if (request.autoPick) viewModel.autoPickAndAdd(request) else viewModel.startSearch(request.query)
            handoff.consume()
        }
    }
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }
    fun search() {
        if (query.isBlank()) return
        keyboard?.hide()
        viewModel.startSearch(query)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Torrents", style = MaterialTheme.typography.titleLarge) },
                actions = { SettingsAction() },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface, titleContentColor = OnSurface)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding).imePadding(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Column(Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        label = { Text("Search torrents") },
                        placeholder = { Text("Title, year or SxxEyy") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { search() }),
                        trailingIcon = {
                            if (query.isNotBlank()) IconButton(onClick = { query = ""; viewModel.clearSearch() }) {
                                Icon(Icons.Filled.Close, contentDescription = "Clear search")
                            }
                        }
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { search() }, enabled = query.isNotBlank() && !uiState.isSearching) {
                            Icon(Icons.Filled.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Search")
                        }
                        if (uiState.isSearching) {
                            OutlinedButton(onClick = { viewModel.stopSearch() }) { Text("Stop", color = StatusRed) }
                        }
                    }
                }
            }

            uiState.operation?.let { operation ->
                item {
                    AutomaticOperationCard(operation, onCancel = { viewModel.stopSearch() },
                        onRetry = { viewModel.retryAutomaticDownload() },
                        onReview = { viewModel.dismissOperation() }, onDismiss = { viewModel.dismissOperation() },
                        onRefresh = { viewModel.dismissOperation() })
                }
            }

            if (uiState.isSearching) {
                item {
                    Row(Modifier.padding(horizontal = 16.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp))
                        Text("Searching… ${uiState.searchTotal} sources so far", style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            uiState.searchError?.let {
                item { Text(it, color = StatusRed, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(16.dp)) }
            }

            if (uiState.searchResults.isNotEmpty()) {
                item {
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${uiState.searchResults.size} of ${uiState.searchTotal}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                        listOf(TorrentSortOption.SEEDS to "Seeders", TorrentSortOption.SIZE to "Size",
                            TorrentSortOption.NAME to "Name").forEach { (option, label) ->
                            FilterChip(selected = uiState.searchResultsSortOption == option,
                                onClick = { viewModel.setSearchResultsSort(option) }, label = { Text(label) })
                        }
                    }
                }
                items(viewModel.getSortedSearchResults(), key = { it.fileUrl }) { result ->
                    Box(Modifier.padding(horizontal = 16.dp, vertical = 3.dp)) {
                        SearchResultItem(result, enabled = !uiState.isMutating, onAdd = { viewModel.addTorrent(result.fileUrl) })
                    }
                }
                if (!uiState.isSearching && uiState.searchOffset < uiState.searchTotal) {
                    item {
                        TextButton(onClick = { viewModel.loadMoreResults() }, enabled = !uiState.isLoadingMore,
                            modifier = Modifier.padding(horizontal = 8.dp)) {
                            Text(if (uiState.isLoadingMore) "Loading…" else "Load more results")
                        }
                    }
                }
            } else if (!uiState.isSearching && uiState.searchId != null && uiState.searchError == null) {
                item { Text("No sources found. Try a different title or year.", modifier = Modifier.padding(16.dp)) }
            }

            item {
                SectionHeader("Add magnet link", modifier = Modifier.padding(top = 16.dp))
                Column(Modifier.padding(horizontal = 16.dp)) {
                    OutlinedTextField(
                        value = magnet,
                        onValueChange = { magnet = it },
                        label = { Text("Magnet link or torrent URL") },
                        placeholder = { Text("magnet:?xt=urn:btih:…") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { viewModel.addTorrent(magnet.trim()) { magnet = "" } },
                        enabled = magnet.isNotBlank() && !uiState.isMutating) {
                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(if (uiState.isMutating) "Adding…" else "Add download")
                    }
                    Text("Downloads in order, so playback can start early.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
    }
}

@Composable
private fun AutomaticOperationCard(
    operation: DownloadOperation,
    onCancel: () -> Unit,
    onRetry: () -> Unit,
    onReview: () -> Unit,
    onDismiss: () -> Unit,
    onRefresh: () -> Unit
) {
    Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text(operation.request.query, style = MaterialTheme.typography.titleSmall,
                maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(operation.message, style = MaterialTheme.typography.bodyMedium)
            operation.source?.let {
                Text(it.fileName, style = MaterialTheme.typography.bodySmall,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text("${it.nbSeeders} seeders · ${formatBytes(it.fileSize)}",
                    style = MaterialTheme.typography.labelMedium)
            }
            if (operation.stage in listOf(DownloadStage.SEARCHING, DownloadStage.SELECTING, DownloadStage.ADDING)) {
                LinearProgressIndicator(Modifier.fillMaxWidth().padding(top = 8.dp))
            }
            Row {
                when (operation.stage) {
                    DownloadStage.SEARCHING, DownloadStage.SELECTING -> TextButton(onClick = onCancel) { Text("Cancel") }
                    DownloadStage.FAILED, DownloadStage.CANCELLED -> {
                        if (operation.canRetry) TextButton(onClick = onRetry) { Text("Retry") }
                        else TextButton(onClick = onRefresh) { Text("OK") }
                        if (operation.canRetry) TextButton(onClick = onReview) { Text("Choose manually") }
                    }
                    else -> Unit
                }
            }
            if (operation.stage in listOf(DownloadStage.SUCCEEDED, DownloadStage.FAILED, DownloadStage.CANCELLED)) {
                TextButton(onClick = onDismiss) { Text("Dismiss") }
            }
        }
    }
}

@Composable
fun SearchResultItem(
    result: com.unifiedserver.dashboard.data.models.SearchResult,
    enabled: Boolean = true,
    onAdd: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = result.fileName,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (com.unifiedserver.dashboard.data.repository.isSuspiciousSourceName(result.fileName)) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "⚠ Looks like malware (executable or password-protected archive). Don't download.",
                        style = MaterialTheme.typography.labelSmall,
                        color = StatusRed
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (result.fileSize > 0) {
                        Text(
                            text = formatBytes(result.fileSize),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "S:${result.nbSeeders} L:${result.nbLeechers}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = result.engineName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onAdd, enabled = enabled) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add",
                    tint = Primary
                )
            }
        }
    }
}
