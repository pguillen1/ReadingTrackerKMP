package com.pguillen.readingtracker.domain.repository

import com.pguillen.readingtracker.domain.model.ReadingSession
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface ReadingSessionRepository {

	fun observeAllSessions(): Flow<List<ReadingSession>>

	fun observeSessionsByBookId(bookId: String): Flow<List<ReadingSession>>

	suspend fun getSessionById(sessionId: String): ReadingSession?

	suspend fun insertSession(session: ReadingSession)

	suspend fun updateSession(session: ReadingSession)

	suspend fun deleteSession(sessionId: String)
}