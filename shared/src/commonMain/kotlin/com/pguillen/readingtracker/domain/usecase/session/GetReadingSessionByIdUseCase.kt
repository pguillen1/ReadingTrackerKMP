package com.pguillen.readingtracker.domain.usecase.session

import com.pguillen.readingtracker.domain.model.ReadingSession
import com.pguillen.readingtracker.domain.repository.ReadingSessionRepository

class GetReadingSessionByIdUseCase(
	private val readingSessionRepository: ReadingSessionRepository
) {
	suspend operator fun invoke(sessionId: String): ReadingSession? {
		return readingSessionRepository.getSessionById(sessionId)
	}
}