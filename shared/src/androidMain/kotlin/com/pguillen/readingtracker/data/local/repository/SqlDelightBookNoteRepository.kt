package com.pguillen.readingtracker.data.local.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.pguillen.readingtracker.data.local.mapper.toDatabaseString
import com.pguillen.readingtracker.data.local.mapper.toEnumOrDefault
import com.pguillen.readingtracker.data.local.mapper.toLocalDateTimeFromDatabase
import com.pguillen.readingtracker.database.ReadingTrackerDatabase
import com.pguillen.readingtracker.domain.model.BookNote
import com.pguillen.readingtracker.domain.model.BookNoteType
import com.pguillen.readingtracker.domain.repository.BookNoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class SqlDelightBookNoteRepository(
	database: ReadingTrackerDatabase
) : BookNoteRepository {

	private val queries = database.readingTrackerQueries

	override fun observeNotesByBookId(bookId: String): Flow<List<BookNote>> {
		return queries
			.selectNotesByBookId(bookId, ::mapBookNote)
			.asFlow()
			.mapToList(Dispatchers.Default)
	}

	override suspend fun getNoteById(noteId: String): BookNote? {
		return queries
			.selectNoteById(noteId, ::mapBookNote)
			.executeAsOneOrNull()
	}

	override suspend fun insertNote(note: BookNote) {
		queries.insertBookNote(
			id = note.id,
			book_id = note.bookId,
			type = note.type.name,
			content = note.content,
			page = note.page?.toLong(),
			created_at = note.createdAt.toDatabaseString(),
			updated_at = note.updatedAt.toDatabaseString()
		)
	}

	override suspend fun updateNote(note: BookNote) {
		queries.updateBookNote(
			type = note.type.name,
			content = note.content,
			page = note.page?.toLong(),
			updated_at = note.updatedAt.toDatabaseString(),
			id = note.id
		)
	}

	override suspend fun deleteNote(noteId: String) {
		queries.deleteBookNote(noteId)
	}

	private fun mapBookNote(
		id: String,
		book_id: String,
		type: String,
		content: String,
		page: Long?,
		created_at: String,
		updated_at: String
	): BookNote {
		return BookNote(
			id = id,
			bookId = book_id,
			type = type.toEnumOrDefault(BookNoteType.NOTE),
			content = content,
			page = page?.toInt(),
			createdAt = created_at.toLocalDateTimeFromDatabase(),
			updatedAt = updated_at.toLocalDateTimeFromDatabase()
		)
	}
}