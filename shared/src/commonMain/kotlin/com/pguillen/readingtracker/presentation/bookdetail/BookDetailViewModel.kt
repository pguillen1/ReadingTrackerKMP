package com.pguillen.readingtracker.presentation.bookdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pguillen.readingtracker.core.error.DomainException
import com.pguillen.readingtracker.domain.usecase.book.DeleteBookUseCase
import com.pguillen.readingtracker.domain.usecase.book.ObserveBookDetailUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookDetailViewModel(
	private val bookId: String,
	observeBookDetailUseCase: ObserveBookDetailUseCase,
	private val deleteBookUseCase: DeleteBookUseCase
) : ViewModel() {

	private val _uiState = MutableStateFlow(BookDetailUiState())
	val uiState: StateFlow<BookDetailUiState> = _uiState.asStateFlow()

	private val effectChannel = Channel<BookDetailUiEffect>()
	val effects = effectChannel.receiveAsFlow()

	init {
		viewModelScope.launch {
			observeBookDetailUseCase(bookId)
				.collect { detail ->
					if (detail == null) {
						_uiState.update {
							it.copy(
								book = null,
								recentSessions = emptyList(),
								recentNotes = emptyList(),
								isLoading = false,
								errorMessage = "Book not found"
							)
						}
					}
					else {
						_uiState.update {
							it.copy(
								book = detail.book,
								recentSessions = detail.recentSessions,
								recentNotes = detail.recentNotes,
								isLoading = false,
								errorMessage = null
							)
						}
					}
				}
		}
	}

	fun onDeleteBookClick() {
		_uiState.update {
			it.copy(showDeleteDialog = true)
		}
	}

	fun onDismissDeleteDialog() {
		_uiState.update {
			it.copy(showDeleteDialog = false)
		}
	}

	fun onConfirmDeleteBook() {
		viewModelScope.launch {
			_uiState.update {
				it.copy(
					isDeleting = true,
					errorMessage = null
				)
			}

			try {
				deleteBookUseCase(bookId)

				effectChannel.send(BookDetailUiEffect.NavigateBack)
			}
			catch (exception: DomainException) {
				_uiState.update {
					it.copy(
						isDeleting = false,
						showDeleteDialog = false,
						errorMessage = exception.message ?: "Could not delete book"
					)
				}
			}
			catch (exception: Exception) {
				_uiState.update {
					it.copy(
						isDeleting = false,
						showDeleteDialog = false,
						errorMessage = "Something went wrong"
					)
				}
			}
		}
	}
}