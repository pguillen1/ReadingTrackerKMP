package com.pguillen.readingtracker.data.repository

import com.pguillen.readingtracker.domain.model.BookSortOption
import com.pguillen.readingtracker.domain.model.LibraryViewMode
import com.pguillen.readingtracker.domain.model.ThemePreference
import com.pguillen.readingtracker.domain.model.UserPreferences
import com.pguillen.readingtracker.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeUserPreferencesRepository(
	initialPreferences: UserPreferences = UserPreferences()
) : UserPreferencesRepository {

	private val preferencesFlow = MutableStateFlow(initialPreferences)

	override fun observeUserPreferences(): Flow<UserPreferences> {
		return preferencesFlow
	}

	override suspend fun updateThemePreference(themePreference: ThemePreference) {
		preferencesFlow.value = preferencesFlow.value.copy(
			themePreference = themePreference
		)
	}

	override suspend fun updateLibraryViewMode(libraryViewMode: LibraryViewMode) {
		preferencesFlow.value = preferencesFlow.value.copy(
			libraryViewMode = libraryViewMode
		)
	}

	override suspend fun updateDefaultSortOption(sortOption: BookSortOption) {
		preferencesFlow.value = preferencesFlow.value.copy(
			defaultSortOption = sortOption
		)
	}
}