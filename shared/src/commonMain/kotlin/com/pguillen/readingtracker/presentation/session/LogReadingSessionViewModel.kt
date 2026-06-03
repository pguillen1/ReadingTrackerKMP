package com.pguillen.readingtracker.presentation.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pguillen.readingtracker.core.date.DateTimeProvider
import com.pguillen.readingtracker.core.error.DomainException
import com.pguillen.readingtracker.domain.usecase.book.ObserveBookByIdUseCase
import com.pguillen.readingtracker.domain.usecase.session.RegisterReadingSessionParams
import com.pguillen.readingtracker.domain.usecase.session.RegisterReadingSessionUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LogReadingSessionViewModel(
	private val bookId: String,
	private val observeBookByIdUseCase: ObserveBookByIdUseCase,
	private val registerReadingSessionUseCase: RegisterReadingSessionUseCase,
	private val dateTimeProvider: DateTimeProvider
) : ViewModel() {

	private val _uiState = MutableStateFlow(LogReadingSessionUiState())
	val uiState: StateFlow<LogReadingSessionUiState> = _uiState.asStateFlow()

	private val effectChannel = Channel<LogReadingSessionUiEffect>()
	val effects = effectChannel.receiveAsFlow()

	private var hasInitializedForm = false

	init {
		observeBook()
	}

	private fun observeBook() {
		viewModelScope.launch {
			observeBookByIdUseCase(bookId).collect { book ->
				if (book == null) {
					_uiState.update {
						it.copy(
							isLoading = false,
							errorMessage = "Book not found"
						)
					}
					return@collect
				}

				_uiState.update { currentState ->
					if (!hasInitializedForm) {
						hasInitializedForm = true

						currentState.copy(
							bookTitle = book.title,
							totalPages = book.totalPages,
							startPage = book.currentPage.toString(),
							endPage = book.currentPage.toString(),
							isLoading = false,
							errorMessage = null
						)
					} else {
						currentState.copy(
							bookTitle = book.title,
							totalPages = book.totalPages,
							isLoading = false,
							errorMessage = null
						)
					}
				}
			}
		}
	}

	fun onStartPageChanged(value: String) {
		if (value.all { it.isDigit() }) {
			_uiState.update {
				it.copy(
					startPage = value,
					errorMessage = null
				)
			}
		}
	}

	fun onEndPageChanged(value: String) {
		if (value.all { it.isDigit() }) {
			_uiState.update {
				it.copy(
					endPage = value,
					errorMessage = null
				)
			}
		}
	}

	fun onMinutesChanged(value: String) {
		if (value.all { it.isDigit() }) {
			_uiState.update {
				it.copy(
					minutes = value,
					errorMessage = null
				)
			}
		}
	}

	fun onNoteChanged(value: String) {
		_uiState.update {
			it.copy(
				note = value,
				errorMessage = null
			)
		}
	}

	fun onSaveClicked() {
		val state = _uiState.value

		viewModelScope.launch {
			_uiState.update {
				it.copy(
					isSaving = true,
					errorMessage = null
				)
			}

			try {
				registerReadingSessionUseCase(
					RegisterReadingSessionParams(
						bookId = bookId,
						date = dateTimeProvider.today(),
						startPage = state.startPage.toIntOrNull(),
						endPage = state.endPage.toIntOrNull() ?: 0,
						minutes = state.minutes.toIntOrNull(),
						note = state.note
					)
				)

				effectChannel.send(LogReadingSessionUiEffect.NavigateBack)
			} catch (exception: DomainException) {
				_uiState.update {
					it.copy(
						isSaving = false,
						errorMessage = exception.message ?: "Invalid session data"
					)
				}
			} catch (exception: Exception) {
				_uiState.update {
					it.copy(
						isSaving = false,
						errorMessage = "Something went wrong"
					)
				}
			}
		}
	}
}