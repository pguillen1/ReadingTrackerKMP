package com.pguillen.readingtracker.presentation.bookedit

import com.pguillen.readingtracker.domain.model.ReadingStatus

data class AddEditBookUiState(
	val title: String = "",
	val author: String = "",
	val totalPages: String = "",
	val currentPage: String = "",
	val selectedStatus: ReadingStatus = ReadingStatus.WANT_TO_READ,
	val isSaving: Boolean = false,
	val errorMessage: String? = null
) {
	val canSave: Boolean
		get() = title.isNotBlank() && !isSaving
}