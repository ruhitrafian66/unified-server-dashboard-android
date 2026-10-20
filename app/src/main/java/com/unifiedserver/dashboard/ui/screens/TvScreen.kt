package com.unifiedserver.dashboard.ui.screens

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun TvScreen(viewModel: MediaViewModel = hiltViewModel()) = MediaBrowseScreen("tv", viewModel)
