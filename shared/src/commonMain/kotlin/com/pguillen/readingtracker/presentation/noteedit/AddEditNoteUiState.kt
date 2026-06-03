package com.pguillen.readingtracker.presentation.noteedit

import com.pguillen.readingtracker.domain.model.BookNoteType

data class AddEditNoteUiState(
	val bookTitle: String = "",
	val type: BookNoteType = BookNoteType.NOTE,
	val content: String = "",
	val page: String = "",
	val isEditMode: Boolean = false,
	val isLoading: Boolean = false,
	val isSaving: Boolean = false,
	val errorMessage: String? = null
) {
	val canSave: Boolean
		get() = content.isNotBlank() && !isSaving && !isLoading

	val title: String
		get() = if (isEditMode) "Edit note" else "Add note"

	val saveButtonText: String
		get() = if (isSaving) "Saving..." else if (isEditMode) "Save changes" else "Save note"
}