package com.pguillen.readingtracker.domain.model

data class UserPreferences(
	val themePreference: ThemePreference = ThemePreference.SYSTEM,
	val defaultSortOption: BookSortOption = BookSortOption.RECENTLY_UPDATED
)

enum class ThemePreference {
	SYSTEM,
	LIGHT,
	DARK
}

enum class BookSortOption {
	RECENTLY_UPDATED,
	RECENTLY_ADDED,
	TITLE,
	AUTHOR,
	PROGRESS
}
