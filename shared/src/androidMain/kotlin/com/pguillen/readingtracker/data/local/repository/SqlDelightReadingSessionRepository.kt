package com.pguillen.readingtracker.data.local.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.pguillen.readingtracker.data.local.mapper.toDatabaseString
import com.pguillen.readingtracker.data.local.mapper.toLocalDateFromDatabase
import com.pguillen.readingtracker.data.local.mapper.toLocalDateTimeFromDatabase
import com.pguillen.readingtracker.database.ReadingTrackerDatabase
import com.pguillen.readingtracker.domain.model.ReadingSession
import com.pguillen.readingtracker.domain.repository.ReadingSessionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class SqlDelightReadingSessionRepository(
	database: ReadingTrackerDatabase
) : ReadingSessionRepository {

	private val queries = database.readingTrackerQueries

	override fun observeAllSessions(): Flow<List<ReadingSession>> {
		return queries
			.selectAllSessions(::mapReadingSession)
			.asFlow()
			.mapToList(Dispatchers.Default)
	}

	override fun observeSessionsByBookId(bookId: String): Flow<List<ReadingSession>> {
		return queries
			.selectSessionsByBookId(bookId, ::mapReadingSession)
			.asFlow()
			.mapToList(Dispatchers.Default)
	}

	override suspend fun getSessionById(sessionId: String): ReadingSession? {
		return queries
			.selectSessionById(sessionId, ::mapReadingSession)
			.executeAsOneOrNull()
	}

	override suspend fun insertSession(session: ReadingSession) {
		queries.insertReadingSession(
			id = session.id,
			book_id = session.bookId,
			date = session.date.toDatabaseString(),
			start_page = session.startPage?.toLong(),
			end_page = session.endPage.toLong(),
			minutes = session.minutes?.toLong(),
			note = session.note,
			created_at = session.createdAt.toDatabaseString()
		)
	}

	override suspend fun updateSession(session: ReadingSession) {
		queries.updateReadingSession(
			date = session.date.toDatabaseString(),
			start_page = session.startPage?.toLong(),
			end_page = session.endPage.toLong(),
			minutes = session.minutes?.toLong(),
			note = session.note,
			id = session.id
		)
	}

	override suspend fun deleteSession(sessionId: String) {
		queries.deleteReadingSession(sessionId)
	}

	private fun mapReadingSession(
		id: String,
		book_id: String,
		date: String,
		start_page: Long?,
		end_page: Long,
		minutes: Long?,
		note: String?,
		created_at: String
	): ReadingSession {
		return ReadingSession(
			id = id,
			bookId = book_id,
			date = date.toLocalDateFromDatabase(),
			startPage = start_page?.toInt(),
			endPage = end_page.toInt(),
			minutes = minutes?.toInt(),
			note = note,
			createdAt = created_at.toLocalDateTimeFromDatabase()
		)
	}
}