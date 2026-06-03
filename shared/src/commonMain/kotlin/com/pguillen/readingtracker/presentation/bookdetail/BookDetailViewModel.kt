package com.pguillen.readingtracker.presentation.bookdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pguillen.readingtracker.domain.usecase.book.ObserveBookDetailUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class BookDetailViewModel(
	bookId: String,
	observeBookDetailUseCase: ObserveBookDetailUseCase
) : ViewModel() {

	val uiState: StateFlow<BookDetailUiState> =
		observeBookDetailUseCase(bookId)
			.map { detail ->
				if (detail == null) {
					BookDetailUiState(
						isLoading = false,
						errorMessage = "Book not found"
					)
				}
				else {
					BookDetailUiState(
						book = detail.book,
						recentSessions = detail.recentSessions,
						recentNotes = detail.recentNotes,
						isLoading = false
					)
				}
			}
			.stateIn(
				scope = viewModelScope,
				started = SharingStarted.WhileSubscribed(5_000),
				initialValue = BookDetailUiState()
			)
}