package com.unifiedserver.dashboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.unifiedserver.dashboard.data.PreferencesManager
import com.unifiedserver.dashboard.ui.navigation.AppNavigation
import com.unifiedserver.dashboard.ui.theme.Background
import com.unifiedserver.dashboard.ui.theme.UnifiedServerDashboardTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.unifiedserver.dashboard.ui.components.LoadingScreen
import com.unifiedserver.dashboard.data.repository.DownloadHandoff

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesManager: PreferencesManager
    @Inject lateinit var downloadHandoff: DownloadHandoff

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UnifiedServerDashboardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Background
                ) {
                    RootContent(preferencesManager, downloadHandoff)
                }
            }
        }
    }
}

@Composable
private fun RootContent(preferencesManager: PreferencesManager, handoff: DownloadHandoff) {
    val configured by preferencesManager.setupComplete.collectAsStateWithLifecycle<Boolean?>(initialValue = null)
    when (configured) {
        null -> LoadingScreen()
        else -> AppNavigation(isSetupComplete = configured == true, preferencesManager, handoff)
    }
}
