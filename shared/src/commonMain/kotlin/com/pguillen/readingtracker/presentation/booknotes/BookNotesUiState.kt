package com.pguillen.readingtracker.presentation.booknotes

import com.pguillen.readingtracker.domain.model.BookNote

data class BookNotesUiState(
	val bookTitle: String = "",
	val notes: List<BookNote> = emptyList(),
	val selectedFilter: BookNoteFilter = BookNoteFilter.ALL,
	val isLoading: Boolean = true,
	val errorMessage: String? = null,
	val notePendingDelete: BookNote? = null
) {
	val filteredNotes: List<BookNote>
		get() {
			val type = selectedFilter.toBookNoteTypeOrNull()
			return if (type == null) {
				notes
			}
			else {
				notes.filter { it.type == type }
			}
		}

	val isEmpty: Boolean
		get() = !isLoading && filteredNotes.isEmpty()
}