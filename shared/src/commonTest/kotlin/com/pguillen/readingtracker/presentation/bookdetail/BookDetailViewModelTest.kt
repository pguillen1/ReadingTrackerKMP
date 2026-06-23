package com.pguillen.readingtracker.presentation.bookdetail

import com.pguillen.readingtracker.domain.usecase.book.DeleteBookUseCase
import com.pguillen.readingtracker.domain.usecase.book.ObserveBookDetailUseCase
import com.pguillen.readingtracker.fake.BOOK_ID
import com.pguillen.readingtracker.fake.BOOK_TITLE
import com.pguillen.readingtracker.fake.FakeBookNoteRepository
import com.pguillen.readingtracker.fake.FakeBookRepository
import com.pguillen.readingtracker.fake.FakeReadingSessionRepository
import com.pguillen.readingtracker.fake.NOTE_ID
import com.pguillen.readingtracker.fake.SESSION_ID
import com.pguillen.readingtracker.fake.createBook
import com.pguillen.readingtracker.fake.createNote
import com.pguillen.readingtracker.fake.createSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
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
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class BookDetailViewModelTest {

	private val testDispatcher = StandardTestDispatcher()

	private lateinit var fakeBookRepository: FakeBookRepository
	private lateinit var fakeReadingSessionRepository: FakeReadingSessionRepository
	private lateinit var fakeBookNoteRepository: FakeBookNoteRepository
	private lateinit var viewModel: BookDetailViewModel

	@BeforeTest
	fun setUp() {
		Dispatchers.setMain(testDispatcher)

		fakeBookRepository = FakeBookRepository()
		fakeReadingSessionRepository = FakeReadingSessionRepository()
		fakeBookNoteRepository = FakeBookNoteRepository()
	}

	@AfterTest
	fun tearDown() {
		Dispatchers.resetMain()
	}

	@Test
	fun `uiState loads book detail`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(
				id = BOOK_ID,
				title = BOOK_TITLE
			)
		)
		fakeReadingSessionRepository.sessions.value = listOf(
			createSession(id = SESSION_ID, bookId = BOOK_ID)
		)
		fakeBookNoteRepository.notes.value = listOf(
			createNote(id = NOTE_ID, bookId = BOOK_ID)
		)

		viewModel = createViewModel()

		backgroundScope.launch {
			viewModel.uiState.collect {}
		}

		testDispatcher.scheduler.advanceUntilIdle()

		val state = viewModel.uiState.value

		assertFalse(state.isLoading)
		assertNotNull(state.book)
		assertEquals(BOOK_TITLE, state.book.title)
		assertEquals(1, state.recentSessions.size)
		assertEquals(1, state.recentNotes.size)
		assertNull(state.errorMessage)
	}

	@Test
	fun `uiState shows error when book does not exist`() = runTest {
		viewModel = createViewModel()

		backgroundScope.launch {
			viewModel.uiState.collect {}
		}

		testDispatcher.scheduler.advanceUntilIdle()

		val state = viewModel.uiState.value

		assertFalse(state.isLoading)
		assertNull(state.book)
		assertEquals("Book not found", state.errorMessage)
	}

	@Test
	fun `onDeleteBookClick shows delete dialog`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(id = BOOK_ID)
		)

		viewModel = createViewModel()

		backgroundScope.launch {
			viewModel.uiState.collect {}
		}

		testDispatcher.scheduler.advanceUntilIdle()

		viewModel.onDeleteBookClick()

		val state = viewModel.uiState.value

		assertTrue(state.showDeleteDialog)
	}

	@Test
	fun `onDismissDeleteDialog hides delete dialog`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(id = BOOK_ID)
		)

		viewModel = createViewModel()

		backgroundScope.launch {
			viewModel.uiState.collect {}
		}

		testDispatcher.scheduler.advanceUntilIdle()

		viewModel.onDeleteBookClick()
		viewModel.onDismissDeleteDialog()

		val state = viewModel.uiState.value

		assertFalse(state.showDeleteDialog)
	}

	@Test
	fun `onConfirmDeleteBook deletes book and emits NavigateBack`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(id = BOOK_ID)
		)

		viewModel = createViewModel()

		backgroundScope.launch {
			viewModel.uiState.collect {}
		}

		testDispatcher.scheduler.advanceUntilIdle()

		val effect = async {
			viewModel.effects.first()
		}

		viewModel.onDeleteBookClick()
		viewModel.onConfirmDeleteBook()
		testDispatcher.scheduler.advanceUntilIdle()

		val deletedBook = fakeBookRepository.getBookById(BOOK_ID)

		assertNull(deletedBook)
		assertIs<BookDetailUiEffect.NavigateBack>(effect.await())
	}

	private fun createViewModel(): BookDetailViewModel {
		return BookDetailViewModel(
			bookId = BOOK_ID,
			observeBookDetailUseCase = ObserveBookDetailUseCase(
				bookRepository = fakeBookRepository,
				readingSessionRepository = fakeReadingSessionRepository,
				bookNoteRepository = fakeBookNoteRepository
			),
			deleteBookUseCase = DeleteBookUseCase(
				bookRepository = fakeBookRepository
			)
		)
	}
}