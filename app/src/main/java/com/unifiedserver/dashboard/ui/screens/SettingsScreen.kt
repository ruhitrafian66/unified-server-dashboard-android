package com.unifiedserver.dashboard.ui.screens

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unifiedserver.dashboard.ui.components.SectionHeader
import com.unifiedserver.dashboard.ui.theme.*
import com.unifiedserver.dashboard.ui.utils.settingsTextFieldColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.refreshConfiguration() }
    val snackbarHostState = remember { SnackbarHostState() }

    // Local editable state
    var serverUrl by rememberSaveable(uiState.serverUrl) { mutableStateOf(uiState.serverUrl) }
    var tmdbApiKey by remember { mutableStateOf("") }
    var omdbApiKey by remember(uiState.omdbApiKey) { mutableStateOf(uiState.omdbApiKey) }
    var tmdbKeyVisible by remember { mutableStateOf(false) }
    var autoStopEnabled by rememberSaveable(uiState.autoStopEnabled) { mutableStateOf(uiState.autoStopEnabled) }
    var seedingTime by rememberSaveable(uiState.autoStopMinutes) { mutableStateOf(uiState.autoStopMinutes?.toString() ?: "") }
    var ratioLimit by rememberSaveable(uiState.autoStopRatio) { mutableStateOf(formatRatio(uiState.autoStopRatio)) }
    var autoCheckEnabled by rememberSaveable(uiState.autoCheckEnabled) { mutableStateOf(uiState.autoCheckEnabled) }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        topBar = {
            com.unifiedserver.dashboard.ui.navigation.AppTopBar("Settings", showBack = true, showSettings = false)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            if (uiState.isLoading) LinearProgressIndicator(Modifier.fillMaxWidth())
            uiState.configurationError?.let {
                Text(it, color = StatusYellow, modifier = Modifier.padding(16.dp))
                TextButton(onClick = { viewModel.refreshConfiguration() }, enabled = !uiState.isLoading) {
                    Text("Retry loading settings")
                }
            }
            // Server URL
            SectionHeader("Server")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = serverUrl,
                        onValueChange = { serverUrl = it },
                        label = { Text("Server URL") },
                        placeholder = { Text("https://server.example.com:3001") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                        colors = settingsTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.updateServerUrl(serverUrl.trim()) },
                                                modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isSaving && serverUrl.isNotBlank()
                    ) {
                        Text(if (uiState.savingSection == "server") "Testing connection…" else "Test and save server")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // TMDB API Key
            SectionHeader("TMDB (movie & TV data)")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    when (uiState.tmdbConfigured) {
                        true -> Text("✓ Configured on the server — works on every device.",
                            style = MaterialTheme.typography.bodySmall, color = StatusGreen)
                        false -> Text("Not configured — Media and TV tracking need it.",
                            style = MaterialTheme.typography.bodySmall, color = StatusYellow)
                        null -> {}
                    }
                    Text(
                        text = "Stored only on your server, which makes all TMDB requests. Enter a key to replace it.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = tmdbApiKey,
                        onValueChange = { tmdbApiKey = it },
                        label = { Text("TMDB API key (v3)") },
                        placeholder = { Text(if (uiState.tmdbConfigured == true) "•••••••• (saved)" else "Your TMDB v3 API key") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = if (tmdbKeyVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { tmdbKeyVisible = !tmdbKeyVisible }) {
                                Icon(
                                    imageVector = if (tmdbKeyVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = if (tmdbKeyVisible) "Hide" else "Show",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        colors = settingsTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.saveTmdbApiKey(tmdbApiKey.trim()) { tmdbApiKey = "" } },
                                                modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isSaving && tmdbApiKey.isNotBlank()
                    ) {
                        Text(if (uiState.savingSection == "tmdb") "Verifying…" else "Verify and save")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Auto-Stop Seeding
            SectionHeader("Seeding limits")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Stop seeding automatically",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurface
                            )
                            Text(
                                text = "Saved in qBittorrent, so it applies even when this app is closed",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = autoStopEnabled,
                            enabled = !uiState.isLoading && !uiState.isSaving && uiState.autoStopReady,
                            onCheckedChange = { autoStopEnabled = it }
                        )
                    }

                    if (autoStopEnabled) {
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Divider)
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = seedingTime,
                            onValueChange = { seedingTime = it.filter { c -> c.isDigit() } },
                            label = { Text("Seed for (minutes)") },
                            placeholder = { Text("Empty = no time limit") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = settingsTextFieldColors()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = ratioLimit,
                            onValueChange = { ratioLimit = it },
                            label = { Text("Or until ratio") },
                            placeholder = { Text("e.g. 1.0") },
                            supportingText = { Text("Leave both empty to stop as soon as a download completes.") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = settingsTextFieldColors()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            viewModel.saveAutoStopConfig(
                                enabled = autoStopEnabled,
                                minutesText = seedingTime,
                                ratioText = ratioLimit
                            )
                        },
                                                modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading && !uiState.isSaving && uiState.autoStopReady
                    ) {
                        Text(if (uiState.savingSection == "autoStop") "Saving…" else "Save seeding limits")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Auto-Check Episodes
            SectionHeader("TV Show Auto-Check")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Auto-check Episodes",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurface
                            )
                            Text(
                                text = "Automatically search for new episodes on a schedule",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = autoCheckEnabled,
                            enabled = !uiState.isLoading && !uiState.isSaving && uiState.autoCheckReady,
                            onCheckedChange = { autoCheckEnabled = it }
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.saveAutoCheckEnabled(autoCheckEnabled) },
                                                modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading && !uiState.isSaving && uiState.autoCheckReady
                    ) {
                        Text(if (uiState.savingSection == "autoCheck") "Saving…" else "Save Auto-Check")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            SectionHeader("IMDb ratings (optional)")
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "A personal OMDb key adds IMDb ratings to movie and TV details. Stored on this device only.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = omdbApiKey, onValueChange = { omdbApiKey = it },
                        label = { Text("OMDb API key") }, singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(), colors = settingsTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.saveOmdbApiKey(omdbApiKey) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isSaving && omdbApiKey != uiState.omdbApiKey
                    ) {
                        Text(if (uiState.savingSection == "omdb") "Saving…" else "Save OMDb key")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            SectionHeader("About")
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingsInfoRow(label = "App version", value = com.unifiedserver.dashboard.BuildConfig.VERSION_NAME)
                }
            }
        }
    }
}

@Composable
fun SettingsInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurface
        )
    }
}


