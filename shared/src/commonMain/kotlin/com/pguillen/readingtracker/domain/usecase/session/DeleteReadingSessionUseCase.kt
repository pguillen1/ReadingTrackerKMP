package com.pguillen.readingtracker.domain.usecase.session

import com.pguillen.readingtracker.domain.repository.ReadingSessionRepository

class DeleteReadingSessionUseCase(
	private val readingSessionRepository: ReadingSessionRepository
) {
	suspend operator fun invoke(sessionId: String) {
		readingSessionRepository.deleteSession(sessionId)
	}
}