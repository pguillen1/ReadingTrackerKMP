package com.pguillen.readingtracker.presentation.sessionedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pguillen.readingtracker.core.date.DateTimeProvider
import com.pguillen.readingtracker.core.error.DomainException
import com.pguillen.readingtracker.domain.usecase.book.ObserveBookByIdUseCase
import com.pguillen.readingtracker.domain.usecase.session.GetReadingSessionByIdUseCase
import com.pguillen.readingtracker.domain.usecase.session.RegisterReadingSessionParams
import com.pguillen.readingtracker.domain.usecase.session.RegisterReadingSessionUseCase
import com.pguillen.readingtracker.domain.usecase.session.UpdateReadingSessionParams
import com.pguillen.readingtracker.domain.usecase.session.UpdateReadingSessionUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddEditReadingSessionViewModel(
	private val mode: AddEditReadingSessionMode,
	private val observeBookByIdUseCase: ObserveBookByIdUseCase,
	private val getReadingSessionByIdUseCase: GetReadingSessionByIdUseCase,
	private val registerReadingSessionUseCase: RegisterReadingSessionUseCase,
	private val updateReadingSessionUseCase: UpdateReadingSessionUseCase,
	private val dateTimeProvider: DateTimeProvider
) : ViewModel() {

	private val _uiState = MutableStateFlow(
		AddEditReadingSessionUiState(
			isEditMode = mode is AddEditReadingSessionMode.Edit,
			isLoading = mode is AddEditReadingSessionMode.Edit
		)
	)
	val uiState: StateFlow<AddEditReadingSessionUiState> = _uiState.asStateFlow()

	private val effectChannel = Channel<AddEditReadingSessionUiEffect>()
	val effects = effectChannel.receiveAsFlow()

	private var bookId: String? = null
	private var sessionId: String? = null

	private var hasInitializedAddForm = false

	init {
		when (mode) {
			is AddEditReadingSessionMode.Add -> {
				bookId = mode.bookId
				observeBookForAdd(mode.bookId)
			}

			is AddEditReadingSessionMode.Edit -> {
				sessionId = mode.sessionId
				loadSession(mode.sessionId)
			}
		}
	}

	private fun observeBookForAdd(bookId: String) {
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
					if (!hasInitializedAddForm) {
						hasInitializedAddForm = true

						currentState.copy(
							bookTitle = book.title,
							totalPages = book.totalPages,
							date = dateTimeProvider.today(),
							startPage = book.currentPage.toString(),
							endPage = book.currentPage.toString(),
							isLoading = false,
							errorMessage = null
						)
					}
					else {
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

	private fun loadSession(sessionId: String) {
		viewModelScope.launch {
			val session = getReadingSessionByIdUseCase(sessionId)

			if (session == null) {
				_uiState.update {
					it.copy(
						isLoading = false,
						errorMessage = "Session not found"
					)
				}
				return@launch
			}

			bookId = session.bookId

			_uiState.update {
				it.copy(
					date = session.date,
					startPage = session.startPage?.toString().orEmpty(),
					endPage = session.endPage.toString(),
					minutes = session.minutes?.toString().orEmpty(),
					note = session.note.orEmpty(),
					isEditMode = true,
					isLoading = false,
					errorMessage = null
				)
			}

			observeBookForEdit(session.bookId)
		}
	}

	private fun observeBookForEdit(bookId: String) {
		viewModelScope.launch {
			observeBookByIdUseCase(bookId).collect { book ->
				_uiState.update {
					it.copy(
						bookTitle = book?.title.orEmpty(),
						totalPages = book?.totalPages ?: 0,
						errorMessage = if (book == null) "Book not found" else it.errorMessage
					)
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
				when (mode) {
					is AddEditReadingSessionMode.Add -> {
						val currentBookId = bookId
							?: throw DomainException.NotFound("Book not found")

						registerReadingSessionUseCase(
							RegisterReadingSessionParams(
								bookId = currentBookId,
								date = state.date ?: dateTimeProvider.today(),
								startPage = state.startPage.toIntOrNull(),
								endPage = state.endPage.toIntOrNull() ?: 0,
								minutes = state.minutes.toIntOrNull(),
								note = state.note
							)
						)
					}

					is AddEditReadingSessionMode.Edit -> {
						val currentSessionId = sessionId
							?: throw DomainException.NotFound("Session not found")

						updateReadingSessionUseCase(
							UpdateReadingSessionParams(
								sessionId = currentSessionId,
								date = state.date ?: dateTimeProvider.today(),
								startPage = state.startPage.toIntOrNull(),
								endPage = state.endPage.toIntOrNull() ?: 0,
								minutes = state.minutes.toIntOrNull(),
								note = state.note
							)
						)
					}
				}

				effectChannel.send(AddEditReadingSessionUiEffect.NavigateBack)
			}
			catch (exception: DomainException) {
				_uiState.update {
					it.copy(
						isSaving = false,
						errorMessage = exception.message ?: "Invalid session data"
					)
				}
			}
			catch (exception: Exception) {
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