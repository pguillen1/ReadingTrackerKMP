package com.pguillen.readingtracker.presentation.noteedit

import com.pguillen.readingtracker.domain.model.BookNoteType
import com.pguillen.readingtracker.domain.usecase.book.ObserveBookByIdUseCase
import com.pguillen.readingtracker.domain.usecase.note.AddBookNoteUseCase
import com.pguillen.readingtracker.domain.usecase.note.GetBookNoteByIdUseCase
import com.pguillen.readingtracker.domain.usecase.note.UpdateBookNoteUseCase
import com.pguillen.readingtracker.fake.BOOK_ID
import com.pguillen.readingtracker.fake.BOOK_TITLE
import com.pguillen.readingtracker.fake.FakeBookNoteRepository
import com.pguillen.readingtracker.fake.FakeBookRepository
import com.pguillen.readingtracker.fake.FakeDateTimeProvider
import com.pguillen.readingtracker.fake.FakeIdGenerator
import com.pguillen.readingtracker.fake.NOTE_CONTENT
import com.pguillen.readingtracker.fake.NOTE_ID
import com.pguillen.readingtracker.fake.createBook
import com.pguillen.readingtracker.fake.createNote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditBookNoteViewModelTest {

	private val testDispatcher = StandardTestDispatcher()

	private lateinit var fakeBookRepository: FakeBookRepository
	private lateinit var fakeBookNoteRepository: FakeBookNoteRepository
	private lateinit var fakeIdGenerator: FakeIdGenerator
	private lateinit var fakeDateTimeProvider: FakeDateTimeProvider

	@BeforeTest
	fun setUp() {
		Dispatchers.setMain(testDispatcher)

		fakeBookRepository = FakeBookRepository()
		fakeBookNoteRepository = FakeBookNoteRepository()
		fakeIdGenerator = FakeIdGenerator(mutableListOf(NOTE_ID))
		fakeDateTimeProvider = FakeDateTimeProvider()
	}

	@AfterTest
	fun tearDown() {
		Dispatchers.resetMain()
	}

	@Test
	fun `add mode starts with empty form`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(id = BOOK_ID, title = BOOK_TITLE)
		)

		val viewModel = createViewModel(
			mode = AddEditNoteMode.Add(BOOK_ID)
		)

		testDispatcher.scheduler.advanceUntilIdle()

		val state = viewModel.uiState.value

		assertFalse(state.isEditMode)
		assertFalse(state.isLoading)
		assertEquals(BOOK_TITLE, state.bookTitle)
		assertEquals(BookNoteType.NOTE, state.type)
		assertEquals("", state.content)
		assertEquals("", state.page)
	}

	@Test
	fun `field changes update uiState`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(id = BOOK_ID)
		)

		val viewModel = createViewModel(
			mode = AddEditNoteMode.Add(BOOK_ID)
		)

		viewModel.onTypeSelected(BookNoteType.QUOTE)
		viewModel.onContentChanged("Quote content")
		viewModel.onPageChanged("42")

		val state = viewModel.uiState.value

		assertEquals(BookNoteType.QUOTE, state.type)
		assertEquals("Quote content", state.content)
		assertEquals("42", state.page)
	}

	@Test
	fun `onSaveClicked in add mode creates note and emits NavigateBack`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(id = BOOK_ID)
		)

		val viewModel = createViewModel(
			mode = AddEditNoteMode.Add(BOOK_ID)
		)

		testDispatcher.scheduler.advanceUntilIdle()

		val effect = async {
			viewModel.effects.first()
		}

		viewModel.onTypeSelected(BookNoteType.QUOTE)
		viewModel.onContentChanged(NOTE_CONTENT)
		viewModel.onPageChanged("50")

		viewModel.onSaveClicked()
		testDispatcher.scheduler.advanceUntilIdle()

		val note = fakeBookNoteRepository.getNoteById(NOTE_ID)

		assertNotNull(note)
		assertEquals(BOOK_ID, note.bookId)
		assertEquals(BookNoteType.QUOTE, note.type)
		assertEquals(NOTE_CONTENT, note.content)
		assertEquals(50, note.page)

		assertIs<AddEditNoteUiEffect.NavigateBack>(effect.await())
	}

	@Test
	fun `edit mode loads existing note`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(id = BOOK_ID, title = BOOK_TITLE)
		)

		fakeBookNoteRepository.notes.value = listOf(
			createNote(
				id = NOTE_ID,
				bookId = BOOK_ID,
				type = BookNoteType.QUOTE,
				content = NOTE_CONTENT,
				page = 25
			)
		)

		val viewModel = createViewModel(
			mode = AddEditNoteMode.Edit(NOTE_ID)
		)

		testDispatcher.scheduler.advanceUntilIdle()

		val state = viewModel.uiState.value

		assertTrue(state.isEditMode)
		assertFalse(state.isLoading)
		assertEquals(BOOK_TITLE, state.bookTitle)
		assertEquals(BookNoteType.QUOTE, state.type)
		assertEquals(NOTE_CONTENT, state.content)
		assertEquals("25", state.page)
	}

	@Test
	fun `onSaveClicked in edit mode updates note and emits NavigateBack`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(id = BOOK_ID)
		)

		fakeBookNoteRepository.notes.value = listOf(
			createNote(
				id = NOTE_ID,
				bookId = BOOK_ID,
				type = BookNoteType.NOTE,
				content = "Old content",
				page = 10
			)
		)

		val viewModel = createViewModel(
			mode = AddEditNoteMode.Edit(NOTE_ID)
		)

		testDispatcher.scheduler.advanceUntilIdle()

		val effect = async {
			viewModel.effects.first()
		}

		viewModel.onTypeSelected(BookNoteType.QUOTE)
		viewModel.onContentChanged("Updated content")
		viewModel.onPageChanged("80")

		viewModel.onSaveClicked()
		testDispatcher.scheduler.advanceUntilIdle()

		val note = fakeBookNoteRepository.getNoteById(NOTE_ID)

		assertNotNull(note)
		assertEquals(BookNoteType.QUOTE, note.type)
		assertEquals("Updated content", note.content)
		assertEquals(80, note.page)

		assertIs<AddEditNoteUiEffect.NavigateBack>(effect.await())
	}

	@Test
	fun `edit mode shows error when note does not exist`() = runTest {
		val viewModel = createViewModel(
			mode = AddEditNoteMode.Edit(NOTE_ID)
		)

		testDispatcher.scheduler.advanceUntilIdle()

		val state = viewModel.uiState.value

		assertFalse(state.isLoading)
		assertEquals("Note not found", state.errorMessage)
	}

	private fun createViewModel(
		mode: AddEditNoteMode
	): AddEditNoteViewModel {
		return AddEditNoteViewModel(
			mode = mode,
			observeBookByIdUseCase = ObserveBookByIdUseCase(fakeBookRepository),
			getBookNoteByIdUseCase = GetBookNoteByIdUseCase(fakeBookNoteRepository),
			addBookNoteUseCase = AddBookNoteUseCase(
				bookRepository = fakeBookRepository,
				bookNoteRepository = fakeBookNoteRepository,
				idGenerator = fakeIdGenerator,
				dateTimeProvider = fakeDateTimeProvider
			),
			updateBookNoteUseCase = UpdateBookNoteUseCase(
				bookNoteRepository = fakeBookNoteRepository,
				dateTimeProvider = fakeDateTimeProvider,
				bookRepository = fakeBookRepository
			)
		)
	}
}