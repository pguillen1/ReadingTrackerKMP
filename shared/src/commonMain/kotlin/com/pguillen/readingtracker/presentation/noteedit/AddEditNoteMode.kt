package com.pguillen.readingtracker.presentation.noteedit

sealed interface AddEditNoteMode {
	data class Add(val bookId: String) : AddEditNoteMode
	data class Edit(val noteId: String) : AddEditNoteMode
}