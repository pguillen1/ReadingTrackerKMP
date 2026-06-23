package com.pguillen.readingtracker.presentation.booknotes

import com.pguillen.readingtracker.domain.model.BookNoteType
import com.pguillen.readingtracker.domain.usecase.book.ObserveBookByIdUseCase
import com.pguillen.readingtracker.domain.usecase.note.DeleteBookNoteUseCase
import com.pguillen.readingtracker.domain.usecase.note.ObserveBookNotesUseCase
import com.pguillen.readingtracker.fake.BOOK_ID
import com.pguillen.readingtracker.fake.BOOK_TITLE
import com.pguillen.readingtracker.fake.FakeBookNoteRepository
import com.pguillen.readingtracker.fake.FakeBookRepository
import com.pguillen.readingtracker.fake.NOTE_ID
import com.pguillen.readingtracker.fake.createBook
import com.pguillen.readingtracker.fake.createNote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class BookNotesViewModelTest {

	private val testDispatcher = StandardTestDispatcher()

	private lateinit var fakeBookRepository: FakeBookRepository
	private lateinit var fakeBookNoteRepository: FakeBookNoteRepository
	private lateinit var viewModel: BookNotesViewModel

	@BeforeTest
	fun setUp() {
		Dispatchers.setMain(testDispatcher)

		fakeBookRepository = FakeBookRepository()
		fakeBookNoteRepository = FakeBookNoteRepository()
	}

	@AfterTest
	fun tearDown() {
		Dispatchers.resetMain()
	}

	@Test
	fun `uiState loads book title and notes`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(
				id = BOOK_ID,
				title = BOOK_TITLE
			)
		)

		fakeBookNoteRepository.notes.value = listOf(
			createNote(
				id = NOTE_ID,
				bookId = BOOK_ID,
				type = BookNoteType.NOTE,
				content = "First note"
			),
			createNote(
				id = "note-2",
				bookId = BOOK_ID,
				type = BookNoteType.QUOTE,
				content = "First quote"
			)
		)

		viewModel = createViewModel()

		backgroundScope.launch {
			viewModel.uiState.collect {}
		}

		testDispatcher.scheduler.advanceUntilIdle()

		val state = viewModel.uiState.value

		assertFalse(state.isLoading)
		assertEquals(BOOK_TITLE, state.bookTitle)
		assertEquals(2, state.notes.size)
		assertEquals(2, state.filteredNotes.size)
		assertEquals(BookNoteFilter.ALL, state.selectedFilter)
		assertNull(state.errorMessage)
	}

	@Test
	fun `onFilterSelected filters notes`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(id = BOOK_ID)
		)

		fakeBookNoteRepository.notes.value = listOf(
			createNote(
				id = NOTE_ID,
				bookId = BOOK_ID,
				type = BookNoteType.NOTE
			),
			createNote(
				id = "note-2",
				bookId = BOOK_ID,
				type = BookNoteType.QUOTE
			)
		)

		viewModel = createViewModel()

		backgroundScope.launch {
			viewModel.uiState.collect {}
		}

		testDispatcher.scheduler.advanceUntilIdle()

		viewModel.onFilterSelected(BookNoteFilter.NOTES)

		val state = viewModel.uiState.value

		assertEquals(BookNoteFilter.NOTES, state.selectedFilter)
		assertEquals(1, state.filteredNotes.size)
		assertEquals(BookNoteType.NOTE, state.filteredNotes.first().type)
	}

	@Test
	fun `onFilterSelected filters quotes`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(id = BOOK_ID)
		)

		fakeBookNoteRepository.notes.value = listOf(
			createNote(
				id = NOTE_ID,
				bookId = BOOK_ID,
				type = BookNoteType.NOTE
			),
			createNote(
				id = "note-2",
				bookId = BOOK_ID,
				type = BookNoteType.QUOTE
			)
		)

		viewModel = createViewModel()

		backgroundScope.launch {
			viewModel.uiState.collect {}
		}

		testDispatcher.scheduler.advanceUntilIdle()

		viewModel.onFilterSelected(BookNoteFilter.QUOTES)

		val state = viewModel.uiState.value

		assertEquals(BookNoteFilter.QUOTES, state.selectedFilter)
		assertEquals(1, state.filteredNotes.size)
		assertEquals(BookNoteType.QUOTE, state.filteredNotes.first().type)
	}

	@Test
	fun `onFilterSelected clears filter when selecting same filter again`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(id = BOOK_ID)
		)

		fakeBookNoteRepository.notes.value = listOf(
			createNote(
				id = NOTE_ID,
				bookId = BOOK_ID,
				type = BookNoteType.NOTE
			),
			createNote(
				id = "note-2",
				bookId = BOOK_ID,
				type = BookNoteType.QUOTE
			)
		)

		viewModel = createViewModel()

		backgroundScope.launch {
			viewModel.uiState.collect {}
		}

		testDispatcher.scheduler.advanceUntilIdle()

		viewModel.onFilterSelected(BookNoteFilter.NOTES)
		viewModel.onFilterSelected(BookNoteFilter.NOTES)

		val state = viewModel.uiState.value

		assertEquals(BookNoteFilter.ALL, state.selectedFilter)
		assertEquals(2, state.filteredNotes.size)
	}

	@Test
	fun `uiState shows error when book does not exist`() = runTest {
		fakeBookNoteRepository.notes.value = listOf(
			createNote(
				id = NOTE_ID,
				bookId = BOOK_ID
			)
		)

		viewModel = createViewModel()

		backgroundScope.launch {
			viewModel.uiState.collect {}
		}

		testDispatcher.scheduler.advanceUntilIdle()

		val state = viewModel.uiState.value

		assertFalse(state.isLoading)
		assertEquals("Book not found", state.errorMessage)
		assertEquals(emptyList(), state.notes)
	}

	@Test
	fun `onDeleteNoteClick shows delete dialog`() = runTest {
		val note = createNote(
			id = NOTE_ID,
			bookId = BOOK_ID
		)

		fakeBookRepository.books.value = listOf(
			createBook(id = BOOK_ID)
		)

		fakeBookNoteRepository.notes.value = listOf(note)

		viewModel = createViewModel()

		backgroundScope.launch {
			viewModel.uiState.collect {}
		}

		testDispatcher.scheduler.advanceUntilIdle()

		viewModel.onDeleteNoteClick(note)

		val state = viewModel.uiState.value

		assertEquals(note, state.notePendingDelete)
	}

	@Test
	fun `onDismissDeleteDialog clears note pending delete`() = runTest {
		val note = createNote(
			id = NOTE_ID,
			bookId = BOOK_ID
		)

		fakeBookRepository.books.value = listOf(
			createBook(id = BOOK_ID)
		)

		fakeBookNoteRepository.notes.value = listOf(note)

		viewModel = createViewModel()

		backgroundScope.launch {
			viewModel.uiState.collect {}
		}

		testDispatcher.scheduler.advanceUntilIdle()

		viewModel.onDeleteNoteClick(note)
		viewModel.onDismissDeleteDialog()

		val state = viewModel.uiState.value

		assertNull(state.notePendingDelete)
	}

	@Test
	fun `onConfirmDeleteNote deletes note`() = runTest {
		val note = createNote(
			id = NOTE_ID,
			bookId = BOOK_ID
		)

		fakeBookRepository.books.value = listOf(
			createBook(id = BOOK_ID)
		)

		fakeBookNoteRepository.notes.value = listOf(note)

		viewModel = createViewModel()

		backgroundScope.launch {
			viewModel.uiState.collect {}
		}

		testDispatcher.scheduler.advanceUntilIdle()

		viewModel.onDeleteNoteClick(note)
		viewModel.onConfirmDeleteNote()
		testDispatcher.scheduler.advanceUntilIdle()

		val deletedNote = fakeBookNoteRepository.getNoteById(NOTE_ID)
		val state = viewModel.uiState.value

		assertNull(deletedNote)
		assertNull(state.notePendingDelete)
		assertEquals(emptyList(), state.notes)
	}

	private fun createViewModel(): BookNotesViewModel {
		return BookNotesViewModel(
			bookId = BOOK_ID,
			observeBookByIdUseCase = ObserveBookByIdUseCase(fakeBookRepository),
			observeBookNotesUseCase = ObserveBookNotesUseCase(fakeBookNoteRepository),
			deleteBookNoteUseCase = DeleteBookNoteUseCase(fakeBookNoteRepository)
		)
	}
}