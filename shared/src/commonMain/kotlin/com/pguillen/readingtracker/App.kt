package com.pguillen.readingtracker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.pguillen.readingtracker.presentation.app.AppViewModel
import com.pguillen.readingtracker.presentation.navigation.AppNavigation
import com.pguillen.readingtracker.presentation.theme.ReadingTrackerTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
	val appViewModel: AppViewModel = koinViewModel()
	val uiState by appViewModel.uiState.collectAsState()

	ReadingTrackerTheme(
		themePreference = uiState.themePreference
	) {
		AppNavigation()
	}
}