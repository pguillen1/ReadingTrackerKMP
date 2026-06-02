package com.pguillen.readingtracker.domain.repository

import com.pguillen.readingtracker.domain.model.BookSortOption
import com.pguillen.readingtracker.domain.model.LibraryViewMode
import com.pguillen.readingtracker.domain.model.ThemePreference
import com.pguillen.readingtracker.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {

	fun observeUserPreferences(): Flow<UserPreferences>

	suspend fun updateThemePreference(themePreference: ThemePreference)

	suspend fun updateLibraryViewMode(libraryViewMode: LibraryViewMode)

	suspend fun updateDefaultSortOption(sortOption: BookSortOption)
}