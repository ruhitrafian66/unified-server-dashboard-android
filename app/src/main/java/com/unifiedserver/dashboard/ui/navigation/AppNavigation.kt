package com.unifiedserver.dashboard.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.unifiedserver.dashboard.ui.screens.DashboardScreen
import com.unifiedserver.dashboard.ui.screens.MoviesScreen
import com.unifiedserver.dashboard.ui.screens.SettingsScreen
import com.unifiedserver.dashboard.ui.screens.SetupScreen
import com.unifiedserver.dashboard.ui.screens.ShowsScreen
import com.unifiedserver.dashboard.ui.screens.SystemScreen
import com.unifiedserver.dashboard.ui.screens.TorrentsScreen
import com.unifiedserver.dashboard.ui.screens.TvScreen
import com.unifiedserver.dashboard.ui.theme.Background
import com.unifiedserver.dashboard.ui.theme.Primary
import com.unifiedserver.dashboard.ui.theme.StatusGray
import com.unifiedserver.dashboard.ui.theme.Surface

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Setup    : Screen("setup",    "Setup")
    object Dashboard: Screen("dashboard","Dashboard", Icons.Filled.Dashboard)
    object Torrents : Screen("torrents", "Torrents",  Icons.Filled.Download)
    object Tv       : Screen("tv",       "TV",        Icons.Filled.Tv)
    object Movies   : Screen("movies",   "Movies",    Icons.Filled.Movie)
    object System   : Screen("system",   "System",    Icons.Filled.Memory)
    object Settings : Screen("settings", "Settings",  Icons.Filled.Settings)
    // Shows is now accessible from within TV / Settings only, removed from bottom nav
    object Shows    : Screen("shows",    "Shows")
}

val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.Torrents,
    Screen.Tv,
    Screen.Movies,
    Screen.System,
    Screen.Settings,
)

@Composable
fun AppNavigation(isSetupComplete: Boolean) {
    val navController = rememberNavController()

    Scaffold(
        containerColor = Background,
        bottomBar = {
            if (isSetupComplete) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                NavigationBar(
                    containerColor = Surface,
                    tonalElevation = 0.dp
                ) {
                    bottomNavItems.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = screen.icon!!,
                                    contentDescription = screen.title
                                )
                            },
                            label = {
                                Text(
                                    screen.title,
                                    style = androidx.compose.material3.MaterialTheme.typography.labelSmall
                                )
                            },
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Primary,
                                selectedTextColor = Primary,
                                unselectedIconColor = StatusGray,
                                unselectedTextColor = StatusGray,
                                indicatorColor = Surface
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (isSetupComplete) Screen.Dashboard.route else Screen.Setup.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Setup.route) {
                SetupScreen(
                    onConfigured = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Setup.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Dashboard.route) { DashboardScreen() }
            composable(Screen.Torrents.route)  { TorrentsScreen() }
            composable(Screen.Tv.route)        { TvScreen() }
            composable(Screen.Movies.route)    { MoviesScreen() }
            composable(Screen.System.route)    { SystemScreen() }
            composable(Screen.Settings.route)  { SettingsScreen() }
            composable(Screen.Shows.route)     { ShowsScreen() }
        }
    }
}
