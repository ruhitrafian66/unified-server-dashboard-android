package com.unifiedserver.dashboard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.unifiedserver.dashboard.data.models.LibraryItem
import com.unifiedserver.dashboard.data.models.LibraryResponse
import com.unifiedserver.dashboard.ui.components.*
import com.unifiedserver.dashboard.ui.navigation.AppTopBar
import com.unifiedserver.dashboard.ui.theme.*
import com.unifiedserver.dashboard.ui.utils.*
import java.text.DateFormat
import java.time.Instant
import java.util.Date
import kotlin.math.roundToInt

/** The server's download folder as a media library: what's stored, what's wasting space. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageScreen(viewModel: StorageViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var confirmCleanup by remember { mutableStateOf(false) }
    RefreshWhileVisible(60_000) { viewModel.load() }

    LaunchedEffect(state.snackbarMessage) {
        state.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        topBar = {
            AppTopBar("Storage") {
                IconButton(onClick = { viewModel.load(rescan = true) }, enabled = !state.isRescanning) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Rescan library")
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Background
    ) { padding ->
        val library = state.library
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                library == null && state.error != null ->
                    ErrorScreen("Couldn't read the media library: ${state.error}") { viewModel.load(rescan = true) }
                library == null -> LoadingScreen()
                else -> LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
                    if (state.isRescanning) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
                    state.error?.let { item { InlineWarning("Showing the last scan — $it") } }
                    item { LibraryOverviewCard(library, isCleaning = state.isCleaning, onCleanup = { confirmCleanup = true }) }
                    item {
                        LibraryControls(
                            query = state.query, onQuery = viewModel::setQuery,
                            filter = state.filter, onFilter = viewModel::setFilter,
                            sort = state.sort, onSort = viewModel::setSort
                        )
                    }
                    val items = state.visibleItems
                    if (items.isEmpty()) {
                        item {
                            Text("Nothing matches.", modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    items(items, key = { it.id }) { item ->
                        LibraryRow(item) { viewModel.open(item) }
                        HorizontalDivider(color = Divider, modifier = Modifier.padding(start = 76.dp))
                    }
                }
            }
        }
    }

    state.selected?.let { summary ->
        LibraryItemSheet(
            summary = summary,
            detail = state.detail,
            detailError = state.detailError,
            allItems = state.library?.items.orEmpty(),
            isDeleting = state.isDeleting,
            onDelete = { viewModel.delete(summary) },
            onDismiss = { viewModel.close() }
        )
    }

    if (confirmCleanup) {
        val junk = state.library?.junk
        AlertDialog(
            onDismissRequest = { confirmCleanup = false },
            title = { Text("Clean up junk files?") },
            text = {
                val examples = junk?.items.orEmpty().take(4).joinToString { it.name.substringAfterLast('/') }
                Text("Deletes ${junk?.count ?: 0} item(s) (${formatBytes(junk?.size ?: 0)}): $examples" +
                    "${if ((junk?.count ?: 0) > 4) "…" else ""}. Media files are not touched.")
            },
            confirmButton = { TextButton(onClick = { confirmCleanup = false; viewModel.cleanup() }) { Text("Clean up") } },
            dismissButton = { TextButton(onClick = { confirmCleanup = false }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun LibraryOverviewCard(library: LibraryResponse, isCleaning: Boolean, onCleanup: () -> Unit) {
    val movies = library.items.count { it.kind == "movie" }
    val shows = library.items.count { it.kind == "tv" }
    val mediaSize = library.items.filter { it.isMedia }.sumOf { it.size }
    val duplicateSize = library.items.filter { it.duplicateOf.isNotEmpty() }.sumOf { it.size }

    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            library.disk?.let { disk ->
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(formatBytes(disk.available), style = MaterialTheme.typography.headlineSmall, color = OnSurface)
                    Text(" free", style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 3.dp))
                    Spacer(Modifier.weight(1f))
                    Text("${formatBytes(disk.used)} of ${formatBytes(disk.used + disk.available)} used",
                        style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 3.dp))
                }
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { disk.percent / 100f },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = levelColor(disk.percent),
                    trackColor = SurfaceVariant
                )
                Spacer(Modifier.height(12.dp))
            }
            Text(
                "$movies movie${if (movies == 1) "" else "s"} · $shows show${if (shows == 1) "" else "s"} · " +
                    "${formatBytes(mediaSize)} of media",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (duplicateSize > 0) {
                Text("Duplicate copies use ${formatBytes(duplicateSize)}", style = MaterialTheme.typography.bodySmall,
                    color = StatusYellow)
            }
            if (library.junk.count > 0) {
                Spacer(Modifier.height(12.dp))
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(StatusBlue.copy(alpha = 0.1f))
                        .padding(start = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${library.junk.count} junk file${if (library.junk.count == 1) "" else "s"} " +
                            "(${formatBytes(library.junk.size)}): system metadata, empty folders, orphaned partial files.",
                        style = MaterialTheme.typography.bodySmall, color = OnSurface,
                        modifier = Modifier.weight(1f).padding(vertical = 10.dp)
                    )
                    TextButton(onClick = onCleanup, enabled = !isCleaning) { Text(if (isCleaning) "Cleaning…" else "Clean up") }
                }
            }
            if (!library.torrentsAvailable) {
                Spacer(Modifier.height(8.dp))
                Text("qBittorrent is unreachable, so download status isn't shown.",
                    style = MaterialTheme.typography.bodySmall, color = StatusYellow)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryControls(
    query: String, onQuery: (String) -> Unit,
    filter: LibraryFilter, onFilter: (LibraryFilter) -> Unit,
    sort: LibrarySort, onSort: (LibrarySort) -> Unit
) {
    var sortMenu by remember { mutableStateOf(false) }
    Column(Modifier.padding(horizontal = 16.dp)) {
        OutlinedTextField(
            value = query, onValueChange = onQuery,
            label = { Text("Filter by title") },
            singleLine = true,
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            trailingIcon = {
                if (query.isNotEmpty()) IconButton(onClick = { onQuery("") }) {
                    Icon(Icons.Filled.Close, contentDescription = "Clear filter")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Row(Modifier.fillMaxWidth().padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Row(Modifier.weight(1f).horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LibraryFilter.entries.forEach {
                    FilterChip(selected = filter == it, onClick = { onFilter(it) }, label = { Text(it.label) })
                }
            }
            Box {
                TextButton(onClick = { sortMenu = true }) {
                    Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(sort.label)
                }
                DropdownMenu(expanded = sortMenu, onDismissRequest = { sortMenu = false }) {
                    LibrarySort.entries.forEach {
                        DropdownMenuItem(
                            text = { Text(it.label) },
                            onClick = { onSort(it); sortMenu = false },
                            trailingIcon = { if (it == sort) Icon(Icons.Filled.Check, contentDescription = "Selected") }
                        )
                    }
                }
            }
        }
    }
}

private fun kindIcon(kind: String): ImageVector = when (kind) {
    "movie" -> Icons.Filled.Movie
    "tv" -> Icons.Filled.Tv
    "leftover" -> Icons.Filled.FolderOff
    else -> Icons.Filled.Folder
}

@Composable
private fun LibraryPoster(item: LibraryItem, width: Dp) {
    val modifier = Modifier.width(width).aspectRatio(2f / 3f).clip(RoundedCornerShape(6.dp)).background(SurfaceVariant)
    val placeholder = @Composable {
        Box(modifier, contentAlignment = Alignment.Center) {
            Icon(kindIcon(item.kind), contentDescription = null, tint = StatusGray, modifier = Modifier.size(width / 2))
        }
    }
    val url = item.posterUrl
    if (url == null) placeholder() else SubcomposeAsyncImage(
        model = url, contentDescription = null, contentScale = ContentScale.Crop, modifier = modifier,
        loading = { placeholder() }, error = { placeholder() }
    )
}

@Composable
private fun Chip(text: String, color: Color = StatusGray) = StatusBadge(text = text, color = color)

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LibraryChips(item: LibraryItem, maxQuality: Int = 3) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        item.quality.take(maxQuality).forEach { Chip(it) }
        libraryTorrentStatus(item.torrent)?.let { status ->
            Chip(status, when {
                status.startsWith("↓") -> StatusBlue
                status.startsWith("Stopped") -> StatusYellow
                status == "Seeding" -> StatusGreen
                else -> StatusGray
            })
        }
        if (item.duplicateOf.isNotEmpty()) Chip("Duplicate", StatusYellow)
        if (item.missingSeasons.isNotEmpty()) Chip("Partial", StatusYellow)
    }
}

@Composable
private fun LibraryRow(item: LibraryItem, onOpen: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(role = Role.Button, onClick = onOpen).padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LibraryPoster(item, 46.dp)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                item.displayTitle + if (item.isMedia && item.year != null) " (${item.year})" else "",
                style = MaterialTheme.typography.bodyMedium, color = OnSurface,
                maxLines = 2, overflow = TextOverflow.Ellipsis
            )
            Text(librarySummary(item), style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(4.dp))
            LibraryChips(item)
        }
        Spacer(Modifier.width(12.dp))
        Text(formatBytes(item.size), style = MaterialTheme.typography.labelMedium, color = OnSurface)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryItemSheet(
    summary: LibraryItem,
    detail: LibraryItem?,
    detailError: String?,
    allItems: List<LibraryItem>,
    isDeleting: Boolean,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    val item = detail ?: summary
    var confirmDelete by remember { mutableStateOf(false) }
    var openSeason by remember { mutableStateOf<Int?>(null) }
    val others = summary.duplicateOf.mapNotNull { id -> allItems.firstOrNull { it.id == id } }
    val title = item.displayTitle + if (item.isMedia && item.year != null) " (${item.year})" else ""

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Surface) {
        Column(Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 16.dp).padding(bottom = 24.dp)) {
            Row {
                LibraryPoster(item, 92.dp)
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(title, style = MaterialTheme.typography.titleMedium, color = OnSurface)
                    Text(librarySummary(item), style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(formatBytes(item.size), style = MaterialTheme.typography.titleSmall, color = OnSurface,
                        modifier = Modifier.padding(vertical = 6.dp))
                    LibraryChips(item, maxQuality = 6)
                }
            }

            item.torrent?.let {
                Text("qBittorrent: ${torrentStateLabel(it.state)} · ratio ${"%.2f".format(it.ratio)}",
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 12.dp))
            }
            if (item.missingSeasons.isNotEmpty()) {
                val named = seasonRange(item.missingSeasons + item.allSeasonNumbers)
                val verb = if (item.missingSeasons.size == 1) "is" else "are"
                Text("The release is named for $named, but ${seasonRange(item.missingSeasons)} $verb not on disk.",
                    style = MaterialTheme.typography.bodySmall, color = StatusYellow, modifier = Modifier.padding(top = 12.dp))
            }
            if (others.isNotEmpty()) {
                val copies = others.joinToString("; ") { "${it.quality.joinToString(" ").ifBlank { it.name }} · ${formatBytes(it.size)}" }
                Text("Another copy is stored: $copies. Delete one to free space.", style = MaterialTheme.typography.bodySmall,
                    color = StatusYellow, modifier = Modifier.padding(top = 12.dp))
            }

            if (detail == null && detailError == null) LinearProgressIndicator(Modifier.fillMaxWidth().padding(top = 16.dp))
            detailError?.let { Text(it, color = StatusRed, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 12.dp)) }

            if (detail?.kind == "tv") {
                Spacer(Modifier.height(12.dp))
                detail.seasons.orEmpty().forEach { season ->
                    val expanded = openSeason == season.season
                    Row(
                        Modifier.fillMaxWidth().clickable(role = Role.Button) { openSeason = if (expanded) null else season.season }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Season ${season.season}", style = MaterialTheme.typography.bodyMedium, color = OnSurface,
                            modifier = Modifier.weight(1f))
                        Text("${season.episodes.size} ep · ${formatBytes(season.size)}", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Icon(if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                            contentDescription = if (expanded) "Collapse" else "Expand")
                    }
                    if (expanded) season.episodes.forEach { ep ->
                        Row(Modifier.fillMaxWidth().padding(start = 8.dp, bottom = 6.dp)) {
                            Text("E${ep.episode.toString().padStart(2, '0')}", style = MaterialTheme.typography.labelMedium,
                                color = Primary, modifier = Modifier.width(40.dp))
                            Text(episodeTitle(ep.file).ifBlank { ep.file.substringAfterLast('/') },
                                style = MaterialTheme.typography.bodySmall, color = OnSurface,
                                maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                            Text(formatBytes(ep.size), style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    HorizontalDivider(color = Divider)
                }
                val extras = detail.extras.orEmpty().size
                if (extras > 0) Text("+ $extras extra video${if (extras == 1) "" else "s"} (featurettes, specials)",
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp))
            }

            if (detail?.kind == "movie") detail.mainFile?.let { main ->
                Row(Modifier.fillMaxWidth().padding(top = 16.dp)) {
                    Text(main.file.substringAfterLast('/'), style = MaterialTheme.typography.bodySmall, color = OnSurface,
                        modifier = Modifier.weight(1f))
                    Text(formatBytes(main.size), style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (detail.subtitles > 0) Text("${detail.subtitles} subtitle file${if (detail.subtitles == 1) "" else "s"}",
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Text("On disk", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 20.dp))
            Text(item.name + if (item.isFile) "" else "/", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                color = OnSurface)
            val modified = item.modified?.let { runCatching { DateFormat.getDateInstance().format(Date.from(Instant.parse(it))) }.getOrNull() }
            Text("${"%,d".format(item.fileCount)} file${if (item.fileCount == 1) "" else "s"}" +
                (modified?.let { " · modified $it" } ?: ""),
                style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(Modifier.height(20.dp))
            if (item.deletable) {
                Button(
                    onClick = { confirmDelete = true }, enabled = !isDeleting, modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = StatusRed, contentColor = Color.White)
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(if (isDeleting) "Deleting…" else "Delete · free ${formatBytes(item.size)}")
                }
            } else {
                Text("Not media — read-only here. Manage it over the file share.", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Delete ${item.displayTitle}?") },
            text = {
                Text("This permanently deletes ${formatBytes(item.size)} from the server" +
                    (if (item.torrent != null) " and removes the torrent from qBittorrent" else "") +
                    ". Plex will no longer show it.")
            },
            confirmButton = {
                TextButton(onClick = { confirmDelete = false; onDelete() }) { Text("Delete", color = StatusRed) }
            },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Cancel") } }
        )
    }
}
