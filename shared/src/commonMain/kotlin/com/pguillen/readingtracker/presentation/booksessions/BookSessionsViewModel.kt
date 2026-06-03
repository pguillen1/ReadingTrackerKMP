package com.pguillen.readingtracker.presentation.booksessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pguillen.readingtracker.core.error.DomainException
import com.pguillen.readingtracker.domain.model.ReadingSession
import com.pguillen.readingtracker.domain.usecase.book.ObserveBookByIdUseCase
import com.pguillen.readingtracker.domain.usecase.session.DeleteReadingSessionUseCase
import com.pguillen.readingtracker.domain.usecase.session.ObserveBookSessionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookSessionsViewModel(
	private val bookId: String,
	observeBookByIdUseCase: ObserveBookByIdUseCase,
	observeBookSessionsUseCase: ObserveBookSessionsUseCase,
	private val deleteReadingSessionUseCase: DeleteReadingSessionUseCase
) : ViewModel() {

	private val _uiState = MutableStateFlow(BookSessionsUiState())
	val uiState: StateFlow<BookSessionsUiState> = _uiState.asStateFlow()

	init {
		viewModelScope.launch {
			combine(
				observeBookByIdUseCase(bookId),
				observeBookSessionsUseCase(bookId)
			) { book, sessions ->
				if (book == null) {
					BookSessionsUiState(
						isLoading = false,
						errorMessage = "Book not found"
					)
				} else {
					BookSessionsUiState(
						bookTitle = book.title,
						sessions = sessions,
						isLoading = false
					)
				}
			}.collect { state ->
				_uiState.value = state.copy(
					sessionPendingDelete = _uiState.value.sessionPendingDelete
				)
			}
		}
	}

	fun onDeleteSessionClick(session: ReadingSession) {
		_uiState.update {
			it.copy(sessionPendingDelete = session)
		}
	}

	fun onDismissDeleteDialog() {
		_uiState.update {
			it.copy(sessionPendingDelete = null)
		}
	}

	fun onConfirmDeleteSession() {
		val session = _uiState.value.sessionPendingDelete ?: return

		viewModelScope.launch {
			try {
				deleteReadingSessionUseCase(session.id)

				_uiState.update {
					it.copy(sessionPendingDelete = null)
				}
			} catch (exception: DomainException) {
				_uiState.update {
					it.copy(
						sessionPendingDelete = null,
						errorMessage = exception.message ?: "Could not delete session"
					)
				}
			} catch (exception: Exception) {
				_uiState.update {
					it.copy(
						sessionPendingDelete = null,
						errorMessage = "Something went wrong"
					)
				}
			}
		}
	}
}