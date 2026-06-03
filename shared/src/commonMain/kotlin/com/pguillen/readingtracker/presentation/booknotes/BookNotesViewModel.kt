package com.pguillen.readingtracker.presentation.booknotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pguillen.readingtracker.core.error.DomainException
import com.pguillen.readingtracker.domain.model.BookNote
import com.pguillen.readingtracker.domain.usecase.book.ObserveBookByIdUseCase
import com.pguillen.readingtracker.domain.usecase.note.DeleteBookNoteUseCase
import com.pguillen.readingtracker.domain.usecase.note.ObserveBookNotesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookNotesViewModel(
	private val bookId: String,
	observeBookByIdUseCase: ObserveBookByIdUseCase,
	observeBookNotesUseCase: ObserveBookNotesUseCase,
	private val deleteBookNoteUseCase: DeleteBookNoteUseCase
) : ViewModel() {

	private val _uiState = MutableStateFlow(BookNotesUiState())
	val uiState: StateFlow<BookNotesUiState> = _uiState.asStateFlow()

	init {
		viewModelScope.launch {
			combine(
				observeBookByIdUseCase(bookId),
				observeBookNotesUseCase(bookId)
			) { book, notes ->
				if (book == null) {
					BookNotesUiState(
						isLoading = false,
						errorMessage = "Book not found"
					)
				}
				else {
					BookNotesUiState(
						bookTitle = book.title,
						notes = notes,
						selectedFilter = _uiState.value.selectedFilter,
						isLoading = false,
						notePendingDelete = _uiState.value.notePendingDelete
					)
				}
			}.collect { state ->
				_uiState.value = state
			}
		}
	}

	fun onFilterSelected(filter: BookNoteFilter) {
		_uiState.update {
			it.copy(
				selectedFilter = if (it.selectedFilter == filter) {
					BookNoteFilter.ALL
				}
				else {
					filter
				}
			)
		}
	}

	fun onDeleteNoteClick(note: BookNote) {
		_uiState.update {
			it.copy(notePendingDelete = note)
		}
	}

	fun onDismissDeleteDialog() {
		_uiState.update {
			it.copy(notePendingDelete = null)
		}
	}

	fun onConfirmDeleteNote() {
		val note = _uiState.value.notePendingDelete ?: return

		viewModelScope.launch {
			try {
				deleteBookNoteUseCase(note.id)

				_uiState.update {
					it.copy(notePendingDelete = null)
				}
			}
			catch (exception: DomainException) {
				_uiState.update {
					it.copy(
						notePendingDelete = null,
						errorMessage = exception.message ?: "Could not delete note"
					)
				}
			}
			catch (exception: Exception) {
				_uiState.update {
					it.copy(
						notePendingDelete = null,
						errorMessage = "Something went wrong"
					)
				}
			}
		}
	}
}