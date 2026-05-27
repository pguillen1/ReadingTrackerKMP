package com.pguillen.readingtracker.data.repository

import com.pguillen.readingtracker.domain.model.BookNote
import com.pguillen.readingtracker.domain.repository.BookNoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeBookNoteRepository(
	initialNotes: List<BookNote> = emptyList()
) : BookNoteRepository {

	private val notesFlow = MutableStateFlow(initialNotes)

	override fun observeNotesByBookId(bookId: String): Flow<List<BookNote>> {
		return notesFlow.map { notes ->
			notes
				.filter { it.bookId == bookId }
				.sortedByDescending { it.createdAt }
		}
	}

	override suspend fun getNoteById(noteId: String): BookNote? {
		return notesFlow.value.firstOrNull { it.id == noteId }
	}

	override suspend fun insertNote(note: BookNote) {
		notesFlow.value += note
	}

	override suspend fun updateNote(note: BookNote) {
		notesFlow.value = notesFlow.value.map { currentNote ->
			if (currentNote.id == note.id) note else currentNote
		}
	}

	override suspend fun deleteNote(noteId: String) {
		notesFlow.value = notesFlow.value.filterNot { note ->
			note.id == noteId
		}
	}
}