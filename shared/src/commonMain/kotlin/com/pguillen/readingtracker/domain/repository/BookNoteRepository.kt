package com.pguillen.readingtracker.domain.repository

import com.pguillen.readingtracker.domain.model.BookNote
import kotlinx.coroutines.flow.Flow

interface BookNoteRepository {

	fun observeNotesByBookId(bookId: String): Flow<List<BookNote>>

	suspend fun getNoteById(noteId: String): BookNote?

	suspend fun insertNote(note: BookNote)

	suspend fun updateNote(note: BookNote)

	suspend fun deleteNote(noteId: String)
}