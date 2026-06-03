package com.pguillen.readingtracker.presentation.logsession

data class LogReadingSessionUiState(
	val bookTitle: String = "",
	val totalPages: Int? = null,
	val startPage: String = "",
	val endPage: String = "",
	val minutes: String = "",
	val note: String = "",
	val isLoading: Boolean = true,
	val isSaving: Boolean = false,
	val errorMessage: String? = null
) {
	val canSave: Boolean
		get() = endPage.isNotBlank() && !isSaving
}
