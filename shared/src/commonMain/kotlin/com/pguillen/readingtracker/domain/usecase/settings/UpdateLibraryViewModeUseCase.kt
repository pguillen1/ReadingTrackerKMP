package com.pguillen.readingtracker.domain.usecase.settings

import com.pguillen.readingtracker.domain.model.LibraryViewMode
import com.pguillen.readingtracker.domain.repository.UserPreferencesRepository

class UpdateLibraryViewModeUseCase(
	private val userPreferencesRepository: UserPreferencesRepository
) {
	suspend operator fun invoke(libraryViewMode: LibraryViewMode) {
		userPreferencesRepository.updateLibraryViewMode(libraryViewMode)
	}
}