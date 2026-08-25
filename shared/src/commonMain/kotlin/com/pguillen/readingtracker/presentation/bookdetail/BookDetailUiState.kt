package com.pguillen.readingtracker.presentation.bookdetail

import com.pguillen.readingtracker.domain.model.Book
import com.pguillen.readingtracker.domain.model.BookNote
import com.pguillen.readingtracker.domain.model.ReadingSession

data class BookDetailUiState(
	val book: Book? = null,
	val recentSessions: List<ReadingSession> = emptyList(),
	val recentNotes: List<BookNote> = emptyList(),
	val isLoading: Boolean = true,
	val isDeleting: Boolean = false,
	val showDeleteDialog: Boolean = false,
	val errorMessage: String? = null,
	val isUpdatingCover: Boolean = false
)