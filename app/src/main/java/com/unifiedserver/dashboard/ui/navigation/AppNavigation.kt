package com.unifiedserver.dashboard.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.unifiedserver.dashboard.data.PreferencesManager
import com.unifiedserver.dashboard.data.repository.DownloadHandoff
import com.unifiedserver.dashboard.ui.screens.*
import com.unifiedserver.dashboard.ui.theme.Background
import com.unifiedserver.dashboard.ui.theme.Surface

private data class Destination(val route: String, val title: String, val icon: ImageVector)
private val destinations = listOf(
    Destination("dashboard", "Home", Icons.Default.Dashboard),
    Destination("tv", "Media", Icons.Default.Movie),
    Destination("torrents", "Downloads", Icons.Default.Download),
    Destination("system", "Server", Icons.Default.Memory)
)
private val mediaRoutes = listOf("movies", "tv", "shows")
val LocalOpenSettings = staticCompositionLocalOf<() -> Unit> { {} }
val LocalOpenDownloads = staticCompositionLocalOf<() -> Unit> { {} }
val LocalNavigateBack = staticCompositionLocalOf<() -> Unit> { {} }

@Composable
fun SettingsAction() {
    val open = LocalOpenSettings.current
    IconButton(onClick = open) { Icon(Icons.Default.Settings, contentDescription = "Settings") }
}

@Composable
fun BackAction() {
    val back = LocalNavigateBack.current
    IconButton(onClick = back) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
}

@Composable
fun AppNavigation(isSetupComplete: Boolean, preferences: PreferencesManager, handoff: DownloadHandoff) {
    val url by preferences.serverUrl.collectAsStateWithLifecycle<String?>(initialValue = null)
    if (url == null) {
        com.unifiedserver.dashboard.ui.components.LoadingScreen()
        return
    }
    // A confirmed server switch must not leave cached data or jobs from the old server visible.
    key(url) {
        val navController = rememberNavController()
        val entry by navController.currentBackStackEntryAsState()
        val route = entry?.destination?.route
        val pendingQuery by handoff.pendingQuery.collectAsStateWithLifecycle()
        fun navigateTop(destination: String) {
            navController.navigate(destination) {
                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
        LaunchedEffect(pendingQuery, isSetupComplete) {
            if (isSetupComplete && pendingQuery != null) navigateTop("torrents")
        }
        CompositionLocalProvider(
            LocalOpenSettings provides { navController.navigate("settings") { launchSingleTop = true } },
            LocalOpenDownloads provides { navigateTop("torrents") },
            LocalNavigateBack provides { navController.popBackStack() }
        ) {
            Scaffold(
                containerColor = Background,
                bottomBar = {
                    if (isSetupComplete) NavigationBar(containerColor = Surface) {
                        destinations.forEach { item ->
                            NavigationBarItem(
                                selected = route == item.route || (item.route == "tv" && route in mediaRoutes),
                                onClick = { navigateTop(item.route) },
                                icon = { Icon(item.icon, contentDescription = null) },
                                label = { Text(item.title) }
                            )
                        }
                    }
                }
            ) { padding ->
                Column(Modifier.padding(padding).consumeWindowInsets(padding)) {
                    if (route in mediaRoutes) {
                        TabRow(selectedTabIndex = mediaRoutes.indexOf(route), containerColor = Surface) {
                            listOf("Movies", "TV", "Tracked").forEachIndexed { index, label ->
                                Tab(selected = route == mediaRoutes[index], text = { Text(label) },
                                    onClick = { navigateTop(mediaRoutes[index]) })
                            }
                        }
                    }
                    NavHost(navController, if (isSetupComplete) "dashboard" else "setup", Modifier.weight(1f)) {
                        composable("setup") {
                            SetupScreen(onConfigured = {
                                navController.navigate("dashboard") { popUpTo("setup") { inclusive = true } }
                            })
                        }
                        composable("dashboard") { DashboardScreen() }
                        composable("torrents") { TorrentsScreen(handoff = handoff) }
                        composable("tv") { TvScreen() }
                        composable("movies") { MoviesScreen() }
                        composable("system") { SystemScreen() }
                        composable("settings") { SettingsScreen() }
                        composable("shows") { ShowsScreen() }
                    }
                }
            }
        }
    }
}
