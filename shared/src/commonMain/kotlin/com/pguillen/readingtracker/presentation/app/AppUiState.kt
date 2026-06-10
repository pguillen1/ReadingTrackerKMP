package com.pguillen.readingtracker.presentation.app

import com.pguillen.readingtracker.domain.model.ThemePreference

data class AppUiState(
	val themePreference: ThemePreference = ThemePreference.SYSTEM
)