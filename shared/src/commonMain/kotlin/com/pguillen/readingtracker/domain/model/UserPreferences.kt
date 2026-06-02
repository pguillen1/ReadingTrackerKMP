package com.pguillen.readingtracker.domain.model

data class UserPreferences(
	val themePreference: ThemePreference = ThemePreference.SYSTEM,
	val libraryViewMode: LibraryViewMode = LibraryViewMode.LIST,
	val defaultSortOption: BookSortOption = BookSortOption.RECENTLY_UPDATED
)

enum class ThemePreference {
	SYSTEM,
	LIGHT,
	DARK
}

enum class LibraryViewMode {
	LIST,
	GRID
}

enum class BookSortOption {
	RECENTLY_UPDATED,
	RECENTLY_ADDED,
	TITLE,
	AUTHOR,
	PROGRESS
}
