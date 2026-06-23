package com.pguillen.readingtracker.presentation.sessionedit

import com.pguillen.readingtracker.domain.model.ReadingStatus
import com.pguillen.readingtracker.domain.usecase.book.ObserveBookByIdUseCase
import com.pguillen.readingtracker.domain.usecase.session.GetReadingSessionByIdUseCase
import com.pguillen.readingtracker.domain.usecase.session.RegisterReadingSessionUseCase
import com.pguillen.readingtracker.domain.usecase.session.UpdateReadingSessionUseCase
import com.pguillen.readingtracker.fake.BOOK_ID
import com.pguillen.readingtracker.fake.BOOK_TITLE
import com.pguillen.readingtracker.fake.FakeBookRepository
import com.pguillen.readingtracker.fake.FakeDateTimeProvider
import com.pguillen.readingtracker.fake.FakeIdGenerator
import com.pguillen.readingtracker.fake.FakeReadingSessionRepository
import com.pguillen.readingtracker.fake.SESSION_ID
import com.pguillen.readingtracker.fake.createBook
import com.pguillen.readingtracker.fake.createSession
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
class AddEditReadingSessionViewModelTest {

	private val testDispatcher = StandardTestDispatcher()

	private lateinit var fakeBookRepository: FakeBookRepository
	private lateinit var fakeReadingSessionRepository: FakeReadingSessionRepository
	private lateinit var fakeIdGenerator: FakeIdGenerator
	private lateinit var fakeDateTimeProvider: FakeDateTimeProvider

	@BeforeTest
	fun setUp() {
		Dispatchers.setMain(testDispatcher)

		fakeBookRepository = FakeBookRepository()
		fakeReadingSessionRepository = FakeReadingSessionRepository()
		fakeIdGenerator = FakeIdGenerator(mutableListOf(SESSION_ID))
		fakeDateTimeProvider = FakeDateTimeProvider()
	}

	@AfterTest
	fun tearDown() {
		Dispatchers.resetMain()
	}

	@Test
	fun `add mode initializes form from book current page`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(
				id = BOOK_ID,
				title = BOOK_TITLE,
				totalPages = 300,
				currentPage = 50,
				status = ReadingStatus.READING
			)
		)

		val viewModel = createViewModel(
			mode = AddEditReadingSessionMode.Add(BOOK_ID)
		)

		testDispatcher.scheduler.advanceUntilIdle()

		val state = viewModel.uiState.value

		assertFalse(state.isEditMode)
		assertFalse(state.isLoading)
		assertEquals(BOOK_TITLE, state.bookTitle)
		assertEquals(300, state.totalPages)
		assertEquals("50", state.startPage)
		assertEquals("50", state.endPage)
	}

	@Test
	fun `field changes update uiState`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(id = BOOK_ID)
		)

		val viewModel = createViewModel(
			mode = AddEditReadingSessionMode.Add(BOOK_ID)
		)

		testDispatcher.scheduler.advanceUntilIdle()

		viewModel.onStartPageChanged("10")
		viewModel.onEndPageChanged("30")
		viewModel.onMinutesChanged("25")
		viewModel.onNoteChanged("Good session")

		val state = viewModel.uiState.value

		assertEquals("10", state.startPage)
		assertEquals("30", state.endPage)
		assertEquals("25", state.minutes)
		assertEquals("Good session", state.note)
	}

	@Test
	fun `onSaveClicked in add mode creates session and emits NavigateBack`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(
				id = BOOK_ID,
				currentPage = 10,
				totalPages = 100,
				status = ReadingStatus.READING
			)
		)

		val viewModel = createViewModel(
			mode = AddEditReadingSessionMode.Add(BOOK_ID)
		)

		testDispatcher.scheduler.advanceUntilIdle()

		val effect = async {
			viewModel.effects.first()
		}

		viewModel.onStartPageChanged("10")
		viewModel.onEndPageChanged("30")
		viewModel.onMinutesChanged("25")
		viewModel.onNoteChanged("Good session")

		viewModel.onSaveClicked()
		testDispatcher.scheduler.advanceUntilIdle()

		val session = fakeReadingSessionRepository.getSessionById(SESSION_ID)

		assertNotNull(session)
		assertEquals(BOOK_ID, session.bookId)
		assertEquals(10, session.startPage)
		assertEquals(30, session.endPage)
		assertEquals(25, session.minutes)
		assertEquals("Good session", session.note)

		assertIs<AddEditReadingSessionUiEffect.NavigateBack>(effect.await())
	}

	@Test
	fun `edit mode loads existing session`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(
				id = BOOK_ID,
				title = BOOK_TITLE,
				totalPages = 100,
				currentPage = 30
			)
		)

		fakeReadingSessionRepository.sessions.value = listOf(
			createSession(
				id = SESSION_ID,
				bookId = BOOK_ID,
				startPage = 10,
				endPage = 30,
				minutes = 20,
				note = "Old note"
			)
		)

		val viewModel = createViewModel(
			mode = AddEditReadingSessionMode.Edit(SESSION_ID)
		)

		testDispatcher.scheduler.advanceUntilIdle()

		val state = viewModel.uiState.value

		assertTrue(state.isEditMode)
		assertFalse(state.isLoading)
		assertEquals(BOOK_TITLE, state.bookTitle)
		assertEquals("10", state.startPage)
		assertEquals("30", state.endPage)
		assertEquals("20", state.minutes)
		assertEquals("Old note", state.note)
	}

	@Test
	fun `onSaveClicked in edit mode updates session and emits NavigateBack`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(
				id = BOOK_ID,
				totalPages = 100,
				currentPage = 30
			)
		)

		fakeReadingSessionRepository.sessions.value = listOf(
			createSession(
				id = SESSION_ID,
				bookId = BOOK_ID,
				startPage = 10,
				endPage = 30,
				minutes = 20,
				note = "Old note"
			)
		)

		val viewModel = createViewModel(
			mode = AddEditReadingSessionMode.Edit(SESSION_ID)
		)

		testDispatcher.scheduler.advanceUntilIdle()

		val effect = async {
			viewModel.effects.first()
		}

		viewModel.onStartPageChanged("30")
		viewModel.onEndPageChanged("50")
		viewModel.onMinutesChanged("35")
		viewModel.onNoteChanged("Updated note")

		viewModel.onSaveClicked()
		testDispatcher.scheduler.advanceUntilIdle()

		val session = fakeReadingSessionRepository.getSessionById(SESSION_ID)

		assertNotNull(session)
		assertEquals(30, session.startPage)
		assertEquals(50, session.endPage)
		assertEquals(35, session.minutes)
		assertEquals("Updated note", session.note)

		assertIs<AddEditReadingSessionUiEffect.NavigateBack>(effect.await())
	}

	private fun createViewModel(
		mode: AddEditReadingSessionMode
	): AddEditReadingSessionViewModel {
		return AddEditReadingSessionViewModel(
			mode = mode,
			observeBookByIdUseCase = ObserveBookByIdUseCase(fakeBookRepository),
			getReadingSessionByIdUseCase = GetReadingSessionByIdUseCase(fakeReadingSessionRepository),
			registerReadingSessionUseCase = RegisterReadingSessionUseCase(
				bookRepository = fakeBookRepository,
				readingSessionRepository = fakeReadingSessionRepository,
				idGenerator = fakeIdGenerator,
				dateTimeProvider = fakeDateTimeProvider
			),
			updateReadingSessionUseCase = UpdateReadingSessionUseCase(
				bookRepository = fakeBookRepository,
				readingSessionRepository = fakeReadingSessionRepository,
				dateTimeProvider = fakeDateTimeProvider
			),
			dateTimeProvider = fakeDateTimeProvider
		)
	}
}