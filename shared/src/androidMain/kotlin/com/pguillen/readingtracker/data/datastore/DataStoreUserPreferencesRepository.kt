package com.pguillen.readingtracker.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.pguillen.readingtracker.domain.model.BookSortOption
import com.pguillen.readingtracker.domain.model.ThemePreference
import com.pguillen.readingtracker.domain.model.UserPreferences
import com.pguillen.readingtracker.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreUserPreferencesRepository(
	private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {

	override fun observeUserPreferences(): Flow<UserPreferences> {
		return dataStore.data.map { preferences ->
			UserPreferences(
				themePreference = preferences[Keys.THEME_PREFERENCE]
					?.toThemePreference()
					?: ThemePreference.SYSTEM,
				defaultSortOption = preferences[Keys.DEFAULT_SORT_OPTION]
					?.toBookSortOption()
					?: BookSortOption.RECENTLY_UPDATED
			)
		}
	}

	override suspend fun updateThemePreference(themePreference: ThemePreference) {
		dataStore.edit { preferences ->
			preferences[Keys.THEME_PREFERENCE] = themePreference.name
		}
	}

	override suspend fun updateDefaultSortOption(sortOption: BookSortOption) {
		dataStore.edit { preferences ->
			preferences[Keys.DEFAULT_SORT_OPTION] = sortOption.name
		}
	}

	private object Keys {
		val THEME_PREFERENCE = stringPreferencesKey("theme_preference")
		val DEFAULT_SORT_OPTION = stringPreferencesKey("default_sort_option")
	}
}

private fun String.toThemePreference(): ThemePreference {
	return enumValueOrDefault(default = ThemePreference.SYSTEM)
}

private fun String.toBookSortOption(): BookSortOption {
	return enumValueOrDefault(default = BookSortOption.RECENTLY_UPDATED)
}

private inline fun <reified T : Enum<T>> String.enumValueOrDefault(default: T): T {
	return runCatching {
		enumValueOf<T>(this)
	}.getOrDefault(default)
}