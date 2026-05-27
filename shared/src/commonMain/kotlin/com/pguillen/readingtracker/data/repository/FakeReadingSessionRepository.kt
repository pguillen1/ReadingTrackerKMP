package com.pguillen.readingtracker.data.repository

import com.pguillen.readingtracker.domain.model.ReadingSession
import com.pguillen.readingtracker.domain.repository.ReadingSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeReadingSessionRepository(
	initialSessions: List<ReadingSession> = emptyList()
) : ReadingSessionRepository {

	private val sessionsFlow = MutableStateFlow(initialSessions)

	override fun observeAllSessions(): Flow<List<ReadingSession>> {
		return sessionsFlow
	}

	override fun observeSessionsByBookId(bookId: String): Flow<List<ReadingSession>> {
		return sessionsFlow.map { sessions ->
			sessions
				.filter { it.bookId == bookId }
				.sortedWith(
					compareByDescending<ReadingSession> { it.date }
						.thenByDescending { it.createdAt }
				)
		}
	}

	override suspend fun getSessionById(sessionId: String): ReadingSession? {
		return sessionsFlow.value.firstOrNull { it.id == sessionId }
	}

	override suspend fun insertSession(session: ReadingSession) {
		sessionsFlow.value += session
	}

	override suspend fun updateSession(session: ReadingSession) {
		sessionsFlow.value = sessionsFlow.value.map { currentSession ->
			if (currentSession.id == session.id) session else currentSession
		}
	}

	override suspend fun deleteSession(sessionId: String) {
		sessionsFlow.value = sessionsFlow.value.filterNot { session ->
			session.id == sessionId
		}
	}
}