package com.pguillen.readingtracker.presentation.noteedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pguillen.readingtracker.core.error.DomainException
import com.pguillen.readingtracker.domain.model.BookNoteType
import com.pguillen.readingtracker.domain.usecase.book.ObserveBookByIdUseCase
import com.pguillen.readingtracker.domain.usecase.note.AddBookNoteParams
import com.pguillen.readingtracker.domain.usecase.note.AddBookNoteUseCase
import com.pguillen.readingtracker.domain.usecase.note.GetBookNoteByIdUseCase
import com.pguillen.readingtracker.domain.usecase.note.UpdateBookNoteParams
import com.pguillen.readingtracker.domain.usecase.note.UpdateBookNoteUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddEditNoteViewModel(
	private val mode: AddEditNoteMode,
	private val observeBookByIdUseCase: ObserveBookByIdUseCase,
	private val getBookNoteByIdUseCase: GetBookNoteByIdUseCase,
	private val addBookNoteUseCase: AddBookNoteUseCase,
	private val updateBookNoteUseCase: UpdateBookNoteUseCase
) : ViewModel() {

	private val _uiState = MutableStateFlow(
		AddEditNoteUiState(
			isEditMode = mode is AddEditNoteMode.Edit,
			isLoading = mode is AddEditNoteMode.Edit
		)
	)
	val uiState: StateFlow<AddEditNoteUiState> = _uiState.asStateFlow()

	private val effectChannel = Channel<AddEditNoteUiEffect>()
	val effects = effectChannel.receiveAsFlow()

	private var bookId: String? = null
	private var noteId: String? = null

	init {
		when (mode) {
			is AddEditNoteMode.Add -> {
				bookId = mode.bookId
				observeBookTitle(mode.bookId)
			}

			is AddEditNoteMode.Edit -> {
				noteId = mode.noteId
				loadNote(mode.noteId)
			}
		}
	}

	private fun observeBookTitle(bookId: String) {
		viewModelScope.launch {
			observeBookByIdUseCase(bookId).collect { book ->
				_uiState.update {
					it.copy(
						bookTitle = book?.title.orEmpty(),
						errorMessage = if (book == null) "Book not found" else null
					)
				}
			}
		}
	}

	private fun loadNote(noteId: String) {
		viewModelScope.launch {
			val note = getBookNoteByIdUseCase(noteId)

			if (note == null) {
				_uiState.update {
					it.copy(
						isLoading = false,
						errorMessage = "Note not found"
					)
				}
				return@launch
			}

			bookId = note.bookId

			_uiState.update {
				it.copy(
					type = note.type,
					content = note.content,
					page = note.page?.toString().orEmpty(),
					isEditMode = true,
					isLoading = false,
					errorMessage = null
				)
			}

			observeBookTitle(note.bookId)
		}
	}

	fun onTypeSelected(type: BookNoteType) {
		_uiState.update {
			it.copy(
				type = type,
				errorMessage = null
			)
		}
	}

	fun onContentChanged(content: String) {
		_uiState.update {
			it.copy(
				content = content,
				errorMessage = null
			)
		}
	}

	fun onPageChanged(page: String) {
		if (page.all { it.isDigit() }) {
			_uiState.update {
				it.copy(
					page = page,
					errorMessage = null
				)
			}
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
					is AddEditNoteMode.Add -> {
						val currentBookId = bookId
							?: throw DomainException.NotFound("Book not found")

						addBookNoteUseCase(
							AddBookNoteParams(
								bookId = currentBookId,
								type = state.type,
								content = state.content,
								page = state.page.toIntOrNull()
							)
						)
					}

					is AddEditNoteMode.Edit -> {
						val currentNoteId = noteId
							?: throw DomainException.NotFound("Note not found")

						updateBookNoteUseCase(
							UpdateBookNoteParams(
								noteId = currentNoteId,
								type = state.type,
								content = state.content,
								page = state.page.toIntOrNull()
							)
						)
					}
				}

				effectChannel.send(AddEditNoteUiEffect.NavigateBack)
			}
			catch (exception: DomainException) {
				_uiState.update {
					it.copy(
						isSaving = false,
						errorMessage = exception.message ?: "Invalid note data"
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