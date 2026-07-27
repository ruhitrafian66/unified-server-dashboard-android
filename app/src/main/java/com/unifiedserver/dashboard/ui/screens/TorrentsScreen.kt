package com.unifiedserver.dashboard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unifiedserver.dashboard.ui.components.*
import com.unifiedserver.dashboard.ui.theme.*
import com.unifiedserver.dashboard.ui.utils.formatSpeed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TorrentsScreen(
    viewModel: TorrentsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showAddDialog by remember { mutableStateOf(false) }
    var showSearchSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }
    var showSortMenu by remember { mutableStateOf(false) }

    // Show snackbar messages
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Torrents", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Surface,
                    titleContentColor = OnSurface
                ),
                actions = {
                    IconButton(onClick = { showSortMenu = true }) {
                        Icon(Icons.Filled.Sort, contentDescription = "Sort", tint = Primary)
                    }
                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false },
                        modifier = Modifier.background(Surface)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Name")
                                    if (uiState.torrentSortOption == TorrentSortOption.NAME) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            imageVector = if (uiState.torrentSortOrder == SortOrder.ASCENDING) 
                                                Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                                            contentDescription = null,
                                            tint = Primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            },
                            onClick = {
                                viewModel.setTorrentSort(TorrentSortOption.NAME)
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Size")
                                    if (uiState.torrentSortOption == TorrentSortOption.SIZE) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            imageVector = if (uiState.torrentSortOrder == SortOrder.ASCENDING) 
                                                Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                                            contentDescription = null,
                                            tint = Primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            },
                            onClick = {
                                viewModel.setTorrentSort(TorrentSortOption.SIZE)
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Seeds")
                                    if (uiState.torrentSortOption == TorrentSortOption.SEEDS) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            imageVector = if (uiState.torrentSortOrder == SortOrder.ASCENDING) 
                                                Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                                            contentDescription = null,
                                            tint = Primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            },
                            onClick = {
                                viewModel.setTorrentSort(TorrentSortOption.SEEDS)
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Progress")
                                    if (uiState.torrentSortOption == TorrentSortOption.PROGRESS) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            imageVector = if (uiState.torrentSortOrder == SortOrder.ASCENDING) 
                                                Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                                            contentDescription = null,
                                            tint = Primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            },
                            onClick = {
                                viewModel.setTorrentSort(TorrentSortOption.PROGRESS)
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Ratio")
                                    if (uiState.torrentSortOption == TorrentSortOption.RATIO) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            imageVector = if (uiState.torrentSortOrder == SortOrder.ASCENDING) 
                                                Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                                            contentDescription = null,
                                            tint = Primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            },
                            onClick = {
                                viewModel.setTorrentSort(TorrentSortOption.RATIO)
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Download Speed")
                                    if (uiState.torrentSortOption == TorrentSortOption.SPEED_DOWN) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            imageVector = if (uiState.torrentSortOrder == SortOrder.ASCENDING) 
                                                Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                                            contentDescription = null,
                                            tint = Primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            },
                            onClick = {
                                viewModel.setTorrentSort(TorrentSortOption.SPEED_DOWN)
                                showSortMenu = false
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            Row(
                modifier = Modifier.padding(end = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { showSearchSheet = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(56.dp)
                ) {
                    Icon(Icons.Filled.Search, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Search", style = MaterialTheme.typography.labelLarge, color = OnPrimary)
                }
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = Primary,
                    contentColor = OnPrimary
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add torrent")
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Background
    ) { innerPadding ->
        RefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.error != null && uiState.torrents.isEmpty() -> {
                    ErrorScreen(
                        message = uiState.error ?: "Failed to load torrents",
                        onRetry = { viewModel.refresh() }
                    )
                }
                uiState.torrents.isEmpty() && !uiState.isLoading -> {
                    EmptyTorrentsView()
                }
                else -> {
                    val sortedTorrents = viewModel.getSortedTorrents()
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            TorrentSummaryRow(
                                downloadSpeed = uiState.torrents.sumOf { it.dlspeed },
                                uploadSpeed = uiState.torrents.sumOf { it.upspeed },
                                count = uiState.torrents.size
                            )
                        }
                        items(
                            items = sortedTorrents,
                            key = { it.hash }
                        ) { torrent ->
                            TorrentItem(
                                torrent = torrent,
                                onPause = { viewModel.pauseTorrent(torrent.hash) },
                                onResume = { viewModel.resumeTorrent(torrent.hash) },
                                onDelete = { showDeleteDialog = torrent.hash }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }

    // Add Torrent Dialog
    if (showAddDialog) {
        AddTorrentDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { url ->
                viewModel.addTorrent(url)
                showAddDialog = false
            }
        )
    }

    // Delete Confirmation
    showDeleteDialog?.let { hash ->
        DeleteTorrentDialog(
            onDismiss = { showDeleteDialog = null },
            onDelete = { deleteFiles ->
                viewModel.deleteTorrent(hash, deleteFiles)
                showDeleteDialog = null
            }
        )
    }

    // Search Bottom Sheet
    if (showSearchSheet) {
        TorrentSearchSheet(
            uiState = uiState,
            onSearch = { viewModel.startSearch(it) },
            onStop = { viewModel.stopSearch() },
            onClear = { viewModel.clearSearch() },
            onAddUrl = { url ->
                viewModel.addTorrent(url)
                showSearchSheet = false
            },
            onDismiss = { showSearchSheet = false },
            viewModel = viewModel
        )
    }
}

@Composable
fun TorrentSummaryRow(
    downloadSpeed: Long,
    uploadSpeed: Long,
    count: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$count torrent(s)",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (downloadSpeed > 0) {
                Text(
                    text = "↓ ${formatSpeed(downloadSpeed)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = StateDownloading
                )
            }
            if (uploadSpeed > 0) {
                Text(
                    text = "↑ ${formatSpeed(uploadSpeed)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = StateUploading
                )
            }
        }
    }
    HorizontalDivider(color = Divider)
}

@Composable
fun EmptyTorrentsView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "No torrents yet",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Add a torrent using the button or search for one",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AddTorrentDialog(
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit
) {
    var url by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        title = { Text("Add Torrent", color = OnSurface) },
        text = {
            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("Magnet link or URL") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = Divider,
                    focusedLabelColor = Primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    cursorColor = Primary,
                    focusedTextColor = OnSurface,
                    unfocusedTextColor = OnSurface
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = { if (url.isNotBlank()) onAdd(url.trim()) },
                enabled = url.isNotBlank()
            ) {
                Text("Add", color = Primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

@Composable
fun DeleteTorrentDialog(
    onDismiss: () -> Unit,
    onDelete: (Boolean) -> Unit
) {
    var deleteFiles by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        title = { Text("Delete Torrent", color = OnSurface) },
        text = {
            Column {
                Text(
                    "Are you sure you want to delete this torrent?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = deleteFiles,
                        onCheckedChange = { deleteFiles = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = StatusRed,
                            uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Also delete files", color = OnSurface)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onDelete(deleteFiles) }) {
                Text("Delete", color = StatusRed)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TorrentSearchSheet(
    uiState: TorrentsUiState,
    onSearch: (String) -> Unit,
    onStop: () -> Unit,
    onClear: () -> Unit,
    onAddUrl: (String) -> Unit,
    onDismiss: () -> Unit,
    viewModel: TorrentsViewModel
) {
    var query by remember { mutableStateOf(uiState.searchQuery) }
    var showSortMenu by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = {
            onClear()
            onDismiss()
        },
        containerColor = Surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Search Torrents",
                    style = MaterialTheme.typography.titleMedium,
                    color = OnSurface,
                    modifier = Modifier.weight(1f)
                )
                if (uiState.searchResults.isNotEmpty()) {
                    IconButton(onClick = { showSortMenu = true }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.Sort, contentDescription = "Sort results", tint = Primary, modifier = Modifier.size(20.dp))
                    }
                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false },
                        modifier = Modifier.background(Surface)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Name")
                                    if (uiState.searchResultsSortOption == TorrentSortOption.NAME) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(Icons.Filled.ArrowDownward, contentDescription = null, tint = Primary, modifier = Modifier.size(14.dp))
                                    }
                                }
                            },
                            onClick = {
                                viewModel.setSearchResultsSort(TorrentSortOption.NAME)
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Size")
                                    if (uiState.searchResultsSortOption == TorrentSortOption.SIZE) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(Icons.Filled.ArrowDownward, contentDescription = null, tint = Primary, modifier = Modifier.size(14.dp))
                                    }
                                }
                            },
                            onClick = {
                                viewModel.setSearchResultsSort(TorrentSortOption.SIZE)
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Seeders")
                                    if (uiState.searchResultsSortOption == TorrentSortOption.SEEDS) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(Icons.Filled.ArrowDownward, contentDescription = null, tint = Primary, modifier = Modifier.size(14.dp))
                                    }
                                }
                            },
                            onClick = {
                                viewModel.setSearchResultsSort(TorrentSortOption.SEEDS)
                                showSortMenu = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Search query") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    if (query.isNotBlank()) onSearch(query)
                }),
                trailingIcon = {
                    if (query.isNotBlank()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Filled.Close, contentDescription = "Clear", tint = MaterialTheme.colorScheme.onSurfaceVariant)
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

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { if (query.isNotBlank()) onSearch(query) },
                    enabled = query.isNotBlank() && !uiState.isSearching,
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Icon(Icons.Filled.Search, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Search")
                }
                if (uiState.isSearching) {
                    OutlinedButton(onClick = onStop) {
                        Text("Stop", color = StatusRed)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.isSearching) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Primary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Searching... ${uiState.searchStatus}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (uiState.searchError != null) {
                Text(
                    text = "Error: ${uiState.searchError}",
                    style = MaterialTheme.typography.bodySmall,
                    color = StatusRed
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (uiState.searchResults.isNotEmpty()) {
                Text(
                    text = "${uiState.searchResults.size} results",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                val sortedResults = viewModel.getSortedSearchResults()
                LazyColumn(
                    modifier = Modifier.heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(sortedResults) { result ->
                        SearchResultItem(
                            result = result,
                            onAdd = { onAddUrl(result.fileUrl) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
fun SearchResultItem(
    result: com.unifiedserver.dashboard.data.models.SearchResult,
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
            IconButton(onClick = onAdd) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add",
                    tint = Primary
                )
            }
        }
    }
}
