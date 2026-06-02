package com.pguillen.readingtracker.presentation.settings

import com.pguillen.readingtracker.domain.model.BookSortOption
import com.pguillen.readingtracker.domain.model.LibraryViewMode
import com.pguillen.readingtracker.domain.model.ThemePreference

data class SettingsUiState(
	val themePreference: ThemePreference = ThemePreference.SYSTEM,
	val libraryViewMode: LibraryViewMode = LibraryViewMode.LIST,
	val defaultSortOption: BookSortOption = BookSortOption.RECENTLY_UPDATED,
	val isLoading: Boolean = true
)