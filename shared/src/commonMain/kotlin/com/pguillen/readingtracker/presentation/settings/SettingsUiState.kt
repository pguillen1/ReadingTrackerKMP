package com.pguillen.readingtracker.presentation.settings

import com.pguillen.readingtracker.domain.model.BookSortOption
import com.pguillen.readingtracker.domain.model.ThemePreference

data class SettingsUiState(
	val themePreference: ThemePreference = ThemePreference.SYSTEM,
	val defaultSortOption: BookSortOption = BookSortOption.RECENTLY_UPDATED,
	val isLoading: Boolean = true
)