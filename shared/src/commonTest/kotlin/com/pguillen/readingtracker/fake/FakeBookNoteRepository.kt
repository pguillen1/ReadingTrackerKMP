package com.pguillen.readingtracker.fake

import com.pguillen.readingtracker.domain.model.BookNote
import com.pguillen.readingtracker.domain.repository.BookNoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeBookNoteRepository(
	initialNotes: List<BookNote> = emptyList()
) : BookNoteRepository {

	val notes = MutableStateFlow(initialNotes)

	override fun observeNotesByBookId(bookId: String): Flow<List<BookNote>> {
		return notes.map { notes ->
			notes
				.filter { it.bookId == bookId }
				.sortedByDescending { it.createdAt }
		}
	}

	override suspend fun getNoteById(noteId: String): BookNote? {
		return notes.value.firstOrNull { it.id == noteId }
	}

	override suspend fun insertNote(note: BookNote) {
		notes.value += note
	}

	override suspend fun updateNote(note: BookNote) {
		notes.value = notes.value.map { currentNote ->
			if (currentNote.id == note.id) note else currentNote
		}
	}

	override suspend fun deleteNote(noteId: String) {
		notes.value = notes.value.filterNot { note ->
			note.id == noteId
		}
	}
}