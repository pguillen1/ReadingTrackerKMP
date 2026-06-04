package com.pguillen.readingtracker.presentation.bookedit

import com.pguillen.readingtracker.domain.model.ReadingStatus

data class AddEditBookUiState(
	val title: String = "",
	val author: String = "",
	val totalPages: String = "",
	val currentPage: String = "",
	val selectedStatus: ReadingStatus = ReadingStatus.WANT_TO_READ,
	val isEditMode: Boolean = false,
	val isLoading: Boolean = false,
	val isSaving: Boolean = false,
	val errorMessage: String? = null
) {
	val canSave: Boolean
		get() = title.isNotBlank() && !isSaving && !isLoading

	val screenTitle: String
		get() = if (isEditMode) "Edit book" else "Add book"

	val saveButtonText: String
		get() = if (isSaving) {
			"Saving..."
		} else if (isEditMode) {
			"Save changes"
		} else {
			"Save book"
		}
}