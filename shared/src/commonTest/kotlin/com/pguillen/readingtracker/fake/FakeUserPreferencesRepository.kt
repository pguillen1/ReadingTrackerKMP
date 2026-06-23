package com.pguillen.readingtracker.fake

import com.pguillen.readingtracker.domain.model.BookSortOption
import com.pguillen.readingtracker.domain.model.ThemePreference
import com.pguillen.readingtracker.domain.model.UserPreferences
import com.pguillen.readingtracker.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeUserPreferencesRepository : UserPreferencesRepository {

	val preferences = MutableStateFlow(UserPreferences())

	override fun observeUserPreferences(): Flow<UserPreferences> {
		return preferences
	}

	override suspend fun updateThemePreference(themePreference: ThemePreference) {
		preferences.value = preferences.value.copy(
			themePreference = themePreference
		)
	}

	override suspend fun updateDefaultSortOption(sortOption: BookSortOption) {
		preferences.value = preferences.value.copy(
			defaultSortOption = sortOption
		)
	}
}