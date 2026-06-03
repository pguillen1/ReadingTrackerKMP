package com.pguillen.readingtracker.presentation.bookedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pguillen.readingtracker.core.error.DomainException
import com.pguillen.readingtracker.domain.model.ReadingStatus
import com.pguillen.readingtracker.domain.usecase.book.AddBookParams
import com.pguillen.readingtracker.domain.usecase.book.AddBookUseCase
import com.pguillen.readingtracker.domain.usecase.book.ObserveBookByIdUseCase
import com.pguillen.readingtracker.domain.usecase.book.UpdateBookParams
import com.pguillen.readingtracker.domain.usecase.book.UpdateBookUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddEditBookViewModel(
	private val mode: AddEditBookMode,
	private val observeBookByIdUseCase: ObserveBookByIdUseCase,
	private val addBookUseCase: AddBookUseCase,
	private val updateBookUseCase: UpdateBookUseCase
) : ViewModel() {

	private val _uiState = MutableStateFlow(
		AddEditBookUiState(
			isEditMode = mode is AddEditBookMode.Edit,
			isLoading = mode is AddEditBookMode.Edit
		)
	)
	val uiState: StateFlow<AddEditBookUiState> = _uiState.asStateFlow()

	private val effectChannel = Channel<AddEditBookUiEffect>()
	val effects = effectChannel.receiveAsFlow()

	private var bookId: String? = null
	private var hasInitializedEditForm = false

	init {
		when (mode) {
			AddEditBookMode.Add -> Unit

			is AddEditBookMode.Edit -> {
				bookId = mode.bookId
				observeBook(mode.bookId)
			}
		}
	}

	private fun observeBook(bookId: String) {
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

				if (!hasInitializedEditForm) {
					hasInitializedEditForm = true

					_uiState.update {
						it.copy(
							title = book.title,
							author = book.author,
							totalPages = book.totalPages?.toString().orEmpty(),
							currentPage = book.currentPage.toString(),
							selectedStatus = book.status,
							isEditMode = true,
							isLoading = false,
							errorMessage = null
						)
					}
				}
			}
		}
	}

	fun onTitleChanged(title: String) {
		_uiState.update {
			it.copy(
				title = title,
				errorMessage = null
			)
		}
	}

	fun onAuthorChanged(author: String) {
		_uiState.update {
			it.copy(
				author = author,
				errorMessage = null
			)
		}
	}

	fun onTotalPagesChanged(totalPages: String) {
		if (totalPages.all { it.isDigit() }) {
			_uiState.update {
				it.copy(
					totalPages = totalPages,
					errorMessage = null
				)
			}
		}
	}

	fun onCurrentPageChanged(currentPage: String) {
		if (currentPage.all { it.isDigit() }) {
			_uiState.update {
				it.copy(
					currentPage = currentPage,
					errorMessage = null
				)
			}
		}
	}

	fun onStatusSelected(status: ReadingStatus) {
		_uiState.update {
			it.copy(
				selectedStatus = status,
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
					AddEditBookMode.Add -> {
						addBookUseCase(
							AddBookParams(
								title = state.title,
								author = state.author,
								totalPages = state.totalPages.toIntOrNull(),
								currentPage = state.currentPage.toIntOrNull() ?: 0,
								status = state.selectedStatus
							)
						)
					}

					is AddEditBookMode.Edit -> {
						val currentBookId = bookId
							?: throw DomainException.NotFound("Book not found")

						updateBookUseCase(
							UpdateBookParams(
								bookId = currentBookId,
								title = state.title,
								author = state.author,
								totalPages = state.totalPages.toIntOrNull(),
								currentPage = state.currentPage.toIntOrNull() ?: 0,
								status = state.selectedStatus
							)
						)
					}
				}

				effectChannel.send(AddEditBookUiEffect.NavigateBack)
			}
			catch (exception: DomainException) {
				_uiState.update {
					it.copy(
						isSaving = false,
						errorMessage = exception.message ?: "Invalid book data"
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