package com.pguillen.readingtracker.domain.usecase.settings

import com.pguillen.readingtracker.domain.model.ThemePreference
import com.pguillen.readingtracker.domain.repository.UserPreferencesRepository

class UpdateThemePreferenceUseCase(
	private val userPreferencesRepository: UserPreferencesRepository
) {
	suspend operator fun invoke(themePreference: ThemePreference) {
		userPreferencesRepository.updateThemePreference(themePreference)
	}
}