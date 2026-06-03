package com.pguillen.readingtracker.presentation.sessionedit

import kotlinx.datetime.LocalDate

data class AddEditReadingSessionUiState(
	val bookTitle: String = "",
	val totalPages: Int? = null,
	val date: LocalDate? = null,
	val startPage: String = "",
	val endPage: String = "",
	val minutes: String = "",
	val note: String = "",
	val isEditMode: Boolean = false,
	val isLoading: Boolean = false,
	val isSaving: Boolean = false,
	val errorMessage: String? = null
) {
	val canSave: Boolean
		get() = endPage.isNotBlank() && !isSaving && !isLoading

	val title: String
		get() = if (isEditMode) "Edit session" else "Log session"

	val saveButtonText: String
		get() = if (isSaving) {
			"Saving..."
		}
		else if (isEditMode) {
			"Save changes"
		}
		else {
			"Save session"
		}
}