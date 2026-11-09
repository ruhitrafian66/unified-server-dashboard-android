package com.unifiedserver.dashboard.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.unifiedserver.dashboard.ui.components.*
import com.unifiedserver.dashboard.ui.navigation.AppTopBar
import com.unifiedserver.dashboard.ui.theme.Background
import com.unifiedserver.dashboard.ui.theme.Surface

@Composable
fun MoviesScreen(viewModel: MediaViewModel = hiltViewModel()) = MediaBrowseScreen("movie", viewModel)

/** Movies and TV share one interaction model rather than diverging screen copies. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaBrowseScreen(type: String, viewModel: MediaViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var searching by rememberSaveable { mutableStateOf(false) }
    val title = if (type == "tv") "TV shows" else "movies"
    val trending = if (type == "tv") state.trendingTv else state.trendingMovies
    // Refreshing would reset "Load more" paging, so only auto-refresh the first page.
    if (!searching && (type == "tv" || state.moviesPage <= 1)) RefreshWhileVisible(60_000) { viewModel.loadTrending() }
    BackHandler(searching) {
        searching = false
        viewModel.clearSearch()
    }
    Scaffold(
        containerColor = Background,
        topBar = {
            AppTopBar("Media") {
                IconButton(onClick = {
                    searching = !searching
                    if (!searching) viewModel.clearSearch()
                }) {
                    Icon(if (searching) Icons.Default.Close else Icons.Default.Search,
                        contentDescription = if (searching) "Close search" else "Search $title")
                }
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            if (searching) {
                OutlinedTextField(value = state.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it, type) },
                    label = { Text("Search $title") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    trailingIcon = {
                        if (state.searchQuery.isNotBlank()) IconButton(onClick = { viewModel.clearSearch() }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear search")
                        }
                    })
                if (state.searchResults.isEmpty()) {
                    when {
                        state.isSearching -> LoadingScreen()
                        state.searchError != null -> ErrorScreen(state.searchError!!) {
                            viewModel.onSearchQueryChange(state.searchQuery, type)
                        }
                        else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(if (state.searchQuery.isBlank()) "Search by title" else "No matches. Try another title.")
                        }
                    }
                } else {
                    LazyColumn(Modifier.fillMaxSize()) {
                        item {
                            Text("${state.searchResults.size} of ${state.totalResults} results",
                                modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.labelMedium)
                        }
                        items(state.searchResults, key = { "${it.mediaType}:${it.id}" }) {
                            MediaListItem(it) { viewModel.openDetail(it) }
                        }
                        item {
                            state.searchError?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp)) }
                            if (state.searchPage < state.totalPages) TextButton(
                                onClick = { viewModel.loadMore() }, enabled = !state.isSearching) {
                                Text(if (state.isSearching) "Loading…" else "Load more")
                            }
                        }
                    }
                }
            } else {
                // Refreshes automatically while visible.
                SectionHeader(if (type == "tv") "Trending this week" else "New on streaming",
                    modifier = Modifier.padding(top = 8.dp))
                state.trendingError?.let {
                    Text(it, modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.error)
                }
                when {
                    state.isLoadingTrending && trending.isEmpty() -> LoadingScreen()
                    trending.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No titles available. TMDB must be configured on the server (Settings).",
                            modifier = Modifier.padding(16.dp))
                    }
                    else -> LazyVerticalGrid(GridCells.Adaptive(132.dp),
                        contentPadding = PaddingValues(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(trending, key = { it.id }) { item ->
                            MediaCard(item) { viewModel.openDetail(item) }
                        }
                        if (type == "movie" && (state.moviesPage < state.moviesTotalPages || state.moviesLoadMoreError != null)) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    state.moviesLoadMoreError?.let {
                                        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                                    }
                                    TextButton(onClick = { viewModel.loadMoreMovies() }, enabled = !state.isLoadingMoreMovies) {
                                        Text(if (state.isLoadingMoreMovies) "Loading…" else "Load more")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    state.selectedItem?.let {
        MediaDetailSheet(
            item = it, movieDetail = state.movieDetail, tvDetail = state.tvDetail, imdbRating = state.imdbRating,
            isLoading = state.isLoadingDetail, isDownloading = state.isDownloading,
            showSeasonPicker = state.showSeasonPicker, showAutomaticOptions = state.showAutomaticOptions,
            downloadSeason = state.downloadSeason, automaticRequested = state.automaticRequested,
            error = state.detailError, onDismiss = { viewModel.closeDetail() },
            onDownload = { viewModel.downloadFromDetail() },
            onSeasonSelected = { season -> viewModel.downloadWithSeason(season) },
            onSeasonPickerDismiss = { viewModel.dismissSeasonPicker() },
            onAutoDownload = { viewModel.downloadFromDetail(autoPick = true) },
            onConfirmAutomatic = { policy -> viewModel.confirmAutomaticDownload(policy) },
            onDismissAutomatic = { viewModel.dismissAutomaticOptions() })
    }
}
