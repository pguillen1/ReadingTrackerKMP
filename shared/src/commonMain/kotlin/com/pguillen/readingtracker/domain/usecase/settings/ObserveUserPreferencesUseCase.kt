package com.pguillen.readingtracker.domain.usecase.settings

import com.pguillen.readingtracker.domain.model.UserPreferences
import com.pguillen.readingtracker.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow

class ObserveUserPreferencesUseCase(
	private val userPreferencesRepository: UserPreferencesRepository
) {
	operator fun invoke(): Flow<UserPreferences> {
		return userPreferencesRepository.observeUserPreferences()
	}
}