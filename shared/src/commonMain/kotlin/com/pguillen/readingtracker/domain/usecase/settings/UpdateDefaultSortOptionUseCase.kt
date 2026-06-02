package com.pguillen.readingtracker.domain.usecase.settings

import com.pguillen.readingtracker.domain.model.BookSortOption
import com.pguillen.readingtracker.domain.repository.UserPreferencesRepository

class UpdateDefaultSortOptionUseCase(
	private val userPreferencesRepository: UserPreferencesRepository
) {
	suspend operator fun invoke(sortOption: BookSortOption) {
		userPreferencesRepository.updateDefaultSortOption(sortOption)
	}
}