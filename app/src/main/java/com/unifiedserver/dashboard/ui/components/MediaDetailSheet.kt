package com.unifiedserver.dashboard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.unifiedserver.dashboard.data.models.*
import com.unifiedserver.dashboard.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaDetailSheet(
    item: TmdbMediaItem,
    movieDetail: TmdbMovieDetail?,
    tvDetail: TmdbTvDetail?,
    imdbRating: ImdbRatingResponse?,
    isLoading: Boolean,
    isDownloading: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onDownload: () -> Unit
) {
    val isMovie = (item.mediaType ?: "movie") == "movie"

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Background,
        sheetState = rememberModalBottomSheetState(),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
        ) {
            // ── Backdrop / Poster header ──────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                val backdropUrl = movieDetail?.backdropUrl ?: tvDetail?.backdropUrl ?: item.backdropUrl
                val posterUrl = movieDetail?.posterUrl ?: tvDetail?.posterUrl ?: item.posterUrl

                if (backdropUrl != null) {
                    AsyncImage(
                        model = backdropUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(SurfaceVariant)
                    )
                }

                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Background),
                                startY = 80f
                            )
                        )
                )

                // Close button
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(50))
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
                }

                // Poster + Title overlay at bottom
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (posterUrl != null) {
                        AsyncImage(
                            model = posterUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .width(70.dp)
                                .height(105.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        val title = movieDetail?.title ?: tvDetail?.name ?: item.displayTitle
                        val year = movieDetail?.year ?: tvDetail?.year ?: item.year
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (year.isNotBlank()) {
                            Text(
                                text = year,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            // ── Scrollable content ────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Primary)
                    }
                } else if (error != null) {
                    Text(
                        text = "Error: $error",
                        color = StatusRed,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(8.dp)
                    )
                } else {
                    // ── Rating badges row ─────────────────────────────────────
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // TMDB rating
                        val tmdbScore = (movieDetail?.voteAverage ?: tvDetail?.voteAverage ?: item.voteAverage)
                        if (tmdbScore > 0f) {
                            RatingBadge(
                                label = "TMDB",
                                value = "%.1f".format(tmdbScore),
                                color = StatusBlue
                            )
                        }

                        // IMDB rating
                        if (imdbRating != null && imdbRating.imdbRating != "N/A" && imdbRating.imdbRating.isNotBlank()) {
                            RatingBadge(
                                label = "IMDB",
                                value = imdbRating.imdbRating,
                                color = StatusYellow
                            )
                        }

                        // Genre chips
                        val genres = movieDetail?.genres ?: tvDetail?.genres
                        genres?.take(2)?.forEach { genre ->
                            GenreChip(genre.name)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // ── Meta info ─────────────────────────────────────────────
                    val metaItems = buildList {
                        movieDetail?.runtime?.let { add("${it}min") }
                        imdbRating?.rated?.takeIf { it != "N/A" }?.let { add(it) }
                        tvDetail?.let {
                            add("${it.numberOfSeasons} season(s)")
                            add("${it.numberOfEpisodes} episodes")
                        }
                        movieDetail?.status?.takeIf { it.isNotBlank() }?.let { add(it) }
                        tvDetail?.status?.takeIf { it.isNotBlank() }?.let { add(it) }
                    }
                    if (metaItems.isNotEmpty()) {
                        Text(
                            text = metaItems.joinToString(" · "),
                            style = MaterialTheme.typography.bodySmall,
                            color = StatusGray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    // ── Tagline ───────────────────────────────────────────────
                    val tagline = movieDetail?.tagline ?: tvDetail?.tagline
                    if (!tagline.isNullOrBlank()) {
                        Text(
                            text = "\"$tagline\"",
                            style = MaterialTheme.typography.bodyMedium.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                            color = Secondary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    // ── Overview ─────────────────────────────────────────────
                    val overview = movieDetail?.overview ?: tvDetail?.overview ?: item.overview
                    if (overview.isNotBlank()) {
                        Text(
                            text = "Overview",
                            style = MaterialTheme.typography.labelLarge,
                            color = StatusGray,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = overview,
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurface,
                            lineHeight = 22.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // ── IMDB extra info ───────────────────────────────────────
                    if (imdbRating != null && imdbRating.response == "True") {
                        if (imdbRating.director.isNotBlank() && imdbRating.director != "N/A") {
                            DetailInfoRow("Director", imdbRating.director)
                        }
                        if (imdbRating.actors.isNotBlank() && imdbRating.actors != "N/A") {
                            DetailInfoRow("Cast", imdbRating.actors)
                        }
                        if (imdbRating.imdbVotes.isNotBlank() && imdbRating.imdbVotes != "N/A") {
                            DetailInfoRow("IMDB Votes", imdbRating.imdbVotes)
                        }
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }

            // ── Download button ───────────────────────────────────────────────
            Surface(
                color = Background,
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = onDownload,
                    enabled = !isDownloading && !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isDownloading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = OnPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Searching...", color = OnPrimary)
                    } else {
                        Icon(Icons.Filled.Download, contentDescription = null, tint = OnPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        val title = (movieDetail?.title ?: tvDetail?.name ?: item.displayTitle).let {
                            if (it.length > 20) it.take(20) + "…" else it
                        }
                        val year = movieDetail?.year ?: tvDetail?.year ?: item.year
                        val query = if (year.isNotBlank()) "$title $year" else title
                        Text("Download  \"$query\"", color = OnPrimary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun RatingBadge(label: String, value: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            Icons.Filled.Star,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$label $value",
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun GenreChip(name: String) {
    Text(
        text = name,
        style = MaterialTheme.typography.labelSmall,
        color = StatusGray,
        modifier = Modifier
            .background(SurfaceVariant, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
private fun DetailInfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = StatusGray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = OnSurface
        )
    }
}
