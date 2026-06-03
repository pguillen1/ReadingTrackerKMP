package com.pguillen.readingtracker.presentation.booksessions

import com.pguillen.readingtracker.domain.model.ReadingSession

data class BookSessionsUiState(
	val bookTitle: String = "",
	val sessions: List<ReadingSession> = emptyList(),
	val isLoading: Boolean = true,
	val errorMessage: String? = null,
	val sessionPendingDelete: ReadingSession? = null
) {
	val isEmpty: Boolean
		get() = !isLoading && sessions.isEmpty()
}