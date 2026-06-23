package com.pguillen.readingtracker.presentation.bookedit

import com.pguillen.readingtracker.domain.model.ReadingStatus
import com.pguillen.readingtracker.domain.usecase.book.AddBookUseCase
import com.pguillen.readingtracker.domain.usecase.book.ObserveBookByIdUseCase
import com.pguillen.readingtracker.domain.usecase.book.UpdateBookUseCase
import com.pguillen.readingtracker.fake.BOOK_AUTHOR
import com.pguillen.readingtracker.fake.BOOK_ID
import com.pguillen.readingtracker.fake.BOOK_TITLE
import com.pguillen.readingtracker.fake.FakeBookRepository
import com.pguillen.readingtracker.fake.FakeDateTimeProvider
import com.pguillen.readingtracker.fake.FakeIdGenerator
import com.pguillen.readingtracker.fake.createBook
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
class AddEditBookViewModelTest {

	private val testDispatcher = StandardTestDispatcher()

	private lateinit var fakeBookRepository: FakeBookRepository
	private lateinit var fakeIdGenerator: FakeIdGenerator
	private lateinit var fakeDateTimeProvider: FakeDateTimeProvider

	@BeforeTest
	fun setUp() {
		Dispatchers.setMain(testDispatcher)

		fakeBookRepository = FakeBookRepository()
		fakeIdGenerator = FakeIdGenerator(mutableListOf(BOOK_ID))
		fakeDateTimeProvider = FakeDateTimeProvider()
	}

	@AfterTest
	fun tearDown() {
		Dispatchers.resetMain()
	}

	@Test
	fun `add mode starts with empty form`() = runTest {
		val viewModel = createViewModel(
			mode = AddEditBookMode.Add
		)

		testDispatcher.scheduler.advanceUntilIdle()

		val state = viewModel.uiState.value

		assertFalse(state.isEditMode)
		assertFalse(state.isLoading)
		assertEquals("", state.title)
		assertEquals("", state.author)
		assertEquals("", state.totalPages)
		assertEquals("", state.currentPage)
	}

	@Test
	fun `field changes update uiState`() = runTest {
		val viewModel = createViewModel(
			mode = AddEditBookMode.Add
		)

		viewModel.onTitleChanged(BOOK_TITLE)
		viewModel.onAuthorChanged(BOOK_AUTHOR)
		viewModel.onTotalPagesChanged("300")
		viewModel.onCurrentPageChanged("50")
		viewModel.onStatusSelected(ReadingStatus.READING)

		val state = viewModel.uiState.value

		assertEquals(BOOK_TITLE, state.title)
		assertEquals(BOOK_AUTHOR, state.author)
		assertEquals("300", state.totalPages)
		assertEquals("50", state.currentPage)
		assertEquals(ReadingStatus.READING, state.selectedStatus)
	}

	@Test
	fun `onSaveClicked in add mode creates book and emits NavigateBack`() = runTest {
		val viewModel = createViewModel(
			mode = AddEditBookMode.Add
		)

		val effect = async {
			viewModel.effects.first()
		}

		viewModel.onTitleChanged(BOOK_TITLE)
		viewModel.onAuthorChanged(BOOK_AUTHOR)
		viewModel.onTotalPagesChanged("300")
		viewModel.onCurrentPageChanged("10")
		viewModel.onStatusSelected(ReadingStatus.READING)

		viewModel.onSaveClicked()
		testDispatcher.scheduler.advanceUntilIdle()

		val book = fakeBookRepository.getBookById(BOOK_ID)

		assertNotNull(book)
		assertEquals(BOOK_TITLE, book.title)
		assertEquals(BOOK_AUTHOR, book.author)
		assertEquals(300, book.totalPages)
		assertEquals(10, book.currentPage)
		assertEquals(ReadingStatus.READING, book.status)

		assertIs<AddEditBookUiEffect.NavigateBack>(effect.await())
	}

	@Test
	fun `edit mode loads existing book`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(
				id = BOOK_ID,
				title = BOOK_TITLE,
				author = BOOK_AUTHOR,
				totalPages = 300,
				currentPage = 50,
				status = ReadingStatus.READING
			)
		)

		val viewModel = createViewModel(
			mode = AddEditBookMode.Edit(BOOK_ID)
		)

		testDispatcher.scheduler.advanceUntilIdle()

		val state = viewModel.uiState.value

		assertTrue(state.isEditMode)
		assertFalse(state.isLoading)
		assertEquals(BOOK_TITLE, state.title)
		assertEquals(BOOK_AUTHOR, state.author)
		assertEquals("300", state.totalPages)
		assertEquals("50", state.currentPage)
		assertEquals(ReadingStatus.READING, state.selectedStatus)
	}

	@Test
	fun `onSaveClicked in edit mode updates book and emits NavigateBack`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(
				id = BOOK_ID,
				title = "Old title",
				author = "Old author",
				totalPages = 100,
				currentPage = 10,
				status = ReadingStatus.WANT_TO_READ
			)
		)

		val viewModel = createViewModel(
			mode = AddEditBookMode.Edit(BOOK_ID)
		)

		testDispatcher.scheduler.advanceUntilIdle()

		val effect = async {
			viewModel.effects.first()
		}

		viewModel.onTitleChanged(BOOK_TITLE)
		viewModel.onAuthorChanged(BOOK_AUTHOR)
		viewModel.onTotalPagesChanged("300")
		viewModel.onCurrentPageChanged("40")
		viewModel.onStatusSelected(ReadingStatus.READING)

		viewModel.onSaveClicked()
		testDispatcher.scheduler.advanceUntilIdle()

		val book = fakeBookRepository.getBookById(BOOK_ID)

		assertNotNull(book)
		assertEquals(BOOK_TITLE, book.title)
		assertEquals(BOOK_AUTHOR, book.author)
		assertEquals(300, book.totalPages)
		assertEquals(40, book.currentPage)
		assertEquals(ReadingStatus.READING, book.status)

		assertIs<AddEditBookUiEffect.NavigateBack>(effect.await())
	}

	@Test
	fun `edit mode shows error when book does not exist`() = runTest {
		val viewModel = createViewModel(
			mode = AddEditBookMode.Edit(BOOK_ID)
		)

		testDispatcher.scheduler.advanceUntilIdle()

		val state = viewModel.uiState.value

		assertFalse(state.isLoading)
		assertEquals("Book not found", state.errorMessage)
	}

	private fun createViewModel(
		mode: AddEditBookMode
	): AddEditBookViewModel {
		return AddEditBookViewModel(
			mode = mode,
			observeBookByIdUseCase = ObserveBookByIdUseCase(fakeBookRepository),
			addBookUseCase = AddBookUseCase(
				bookRepository = fakeBookRepository,
				idGenerator = fakeIdGenerator,
				dateTimeProvider = fakeDateTimeProvider
			),
			updateBookUseCase = UpdateBookUseCase(
				bookRepository = fakeBookRepository,
				dateTimeProvider = fakeDateTimeProvider
			)
		)
	}
}