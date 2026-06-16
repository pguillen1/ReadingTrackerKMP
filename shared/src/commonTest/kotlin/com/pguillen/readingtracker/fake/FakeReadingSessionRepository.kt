package com.pguillen.readingtracker.fake

import com.pguillen.readingtracker.domain.model.ReadingSession
import com.pguillen.readingtracker.domain.repository.ReadingSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeReadingSessionRepository : ReadingSessionRepository {

	val sessions = MutableStateFlow(emptyList<ReadingSession>())

	override fun observeAllSessions(): Flow<List<ReadingSession>> {
		return sessions
	}

	override fun observeSessionsByBookId(bookId: String): Flow<List<ReadingSession>> {
		return sessions.map { list ->
			list.filter { it.bookId == bookId }
		}
	}

	override suspend fun getSessionById(sessionId: String): ReadingSession? {
		return sessions.value.firstOrNull { it.id == sessionId }
	}

	override suspend fun insertSession(session: ReadingSession) {
		sessions.value += session
	}

	override suspend fun updateSession(session: ReadingSession) {
		sessions.value = sessions.value.map {
			if (it.id == session.id) session else it
		}
	}

	override suspend fun deleteSession(sessionId: String) {
		sessions.value = sessions.value.filterNot { it.id == sessionId }
	}
}