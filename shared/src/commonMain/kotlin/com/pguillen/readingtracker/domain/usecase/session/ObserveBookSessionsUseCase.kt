package com.pguillen.readingtracker.domain.usecase.session

import com.pguillen.readingtracker.domain.model.ReadingSession
import com.pguillen.readingtracker.domain.repository.ReadingSessionRepository
import kotlinx.coroutines.flow.Flow

class ObserveBookSessionsUseCase(
	private val readingSessionRepository: ReadingSessionRepository
) {
	operator fun invoke(bookId: String): Flow<List<ReadingSession>> {
		return readingSessionRepository.observeSessionsByBookId(bookId)
	}
}