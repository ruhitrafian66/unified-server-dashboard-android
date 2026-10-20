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
    val snackbarHostState = remember { SnackbarHostState() }

    // Local editable state
    var serverUrl by rememberSaveable(uiState.serverUrl) { mutableStateOf(uiState.serverUrl) }
    var tmdbApiKey by remember(uiState.tmdbApiKey) { mutableStateOf(uiState.tmdbApiKey) }
    var omdbApiKey by remember(uiState.omdbApiKey) { mutableStateOf(uiState.omdbApiKey) }
    var tmdbKeyVisible by remember { mutableStateOf(false) }
    var autoStopEnabled by rememberSaveable(uiState.autoStopEnabled) { mutableStateOf(uiState.autoStopEnabled) }
    var seedingTime by rememberSaveable(uiState.autoStopDelayMinutes) { mutableStateOf(uiState.autoStopDelayMinutes.toString()) }
    var ratioLimit by rememberSaveable(uiState.autoStopRatio) { mutableStateOf("%.2f".format(uiState.autoStopRatio)) }
    var autoCheckEnabled by rememberSaveable(uiState.autoCheckEnabled) { mutableStateOf(uiState.autoCheckEnabled) }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = { com.unifiedserver.dashboard.ui.navigation.BackAction() },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Surface,
                    titleContentColor = OnSurface
                )
            )
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
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isSaving && serverUrl.isNotBlank()
                    ) {
                        Text(if (uiState.savingSection == "server") "Testing connection…" else "Test and save server")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // TMDB API Key
            SectionHeader("TMDB Integration")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Used for Movies, TV discovery, and tracked shows. Saved to both this device and your server.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = tmdbApiKey,
                        onValueChange = { tmdbApiKey = it },
                        label = { Text("TMDB API Key") },
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
                        onClick = { viewModel.saveTmdbApiKey(tmdbApiKey.trim()) },
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isSaving
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = OnPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Save TMDB Key")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Auto-Stop Seeding
            SectionHeader("Auto-Stop Seeding")
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
                                text = "Enable Auto-Stop",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurface
                            )
                            Text(
                                text = "Automatically stop seeding based on limits",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = autoStopEnabled,
                            enabled = !uiState.isLoading && !uiState.isSaving && uiState.configurationError == null,
                            onCheckedChange = { autoStopEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Primary,
                                checkedTrackColor = PrimaryVariant
                            )
                        )
                    }

                    if (autoStopEnabled) {
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Divider)
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = seedingTime,
                            onValueChange = { seedingTime = it.filter { c -> c.isDigit() } },
                            label = { Text("Stop Delay After Completion (minutes, 0 = immediate)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = settingsTextFieldColors()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = ratioLimit,
                            onValueChange = { ratioLimit = it },
                            label = { Text("Ratio Limit") },
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
                                delayText = seedingTime,
                                ratioText = ratioLimit
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading && !uiState.isSaving && uiState.configurationError == null
                    ) {
                        Text(if (uiState.savingSection == "autoStop") "Saving…" else "Save Auto-Stop Config")
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
                            enabled = !uiState.isLoading && !uiState.isSaving && uiState.configurationError == null,
                            onCheckedChange = { autoCheckEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Primary,
                                checkedTrackColor = PrimaryVariant
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.saveAutoCheckEnabled(autoCheckEnabled) },
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading && !uiState.isSaving && uiState.configurationError == null
                    ) {
                        Text(if (uiState.savingSection == "autoCheck") "Saving…" else "Save Auto-Check")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // About
            SectionHeader("Optional IMDb ratings")
            Column(Modifier.padding(horizontal = 16.dp)) {
                OutlinedTextField(value = omdbApiKey, onValueChange = { omdbApiKey = it },
                    label = { Text("OMDb API key (optional)") }, singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(), colors = settingsTextFieldColors())
                Button(onClick = { viewModel.saveOmdbApiKey(omdbApiKey) }, enabled = !uiState.isSaving) {
                    Text("Save optional key")
                }
            }
            SectionHeader("About")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingsInfoRow(label = "App Version", value = com.unifiedserver.dashboard.BuildConfig.VERSION_NAME)
                    Spacer(modifier = Modifier.height(8.dp))
                    SettingsInfoRow(label = "Connection", value = "Configured server")
                    Spacer(modifier = Modifier.height(8.dp))
                    SettingsInfoRow(
                        label = "Connected To",
                        value = uiState.serverUrl.ifBlank { "Not configured" }
                    )
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


