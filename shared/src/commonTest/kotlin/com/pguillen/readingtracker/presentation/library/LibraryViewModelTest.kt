package com.pguillen.readingtracker.presentation.library

import com.pguillen.readingtracker.domain.model.BookSortOption
import com.pguillen.readingtracker.domain.model.ReadingStatus
import com.pguillen.readingtracker.domain.model.UserPreferences
import com.pguillen.readingtracker.domain.usecase.book.ObserveBooksUseCase
import com.pguillen.readingtracker.domain.usecase.settings.ObserveUserPreferencesUseCase
import com.pguillen.readingtracker.fake.BOOK_ID
import com.pguillen.readingtracker.fake.BOOK_ID_2
import com.pguillen.readingtracker.fake.BOOK_ID_3
import com.pguillen.readingtracker.fake.BOOK_TITLE
import com.pguillen.readingtracker.fake.BOOK_TITLE_2
import com.pguillen.readingtracker.fake.BOOK_TITLE_3
import com.pguillen.readingtracker.fake.FakeBookRepository
import com.pguillen.readingtracker.fake.FakeUserPreferencesRepository
import com.pguillen.readingtracker.fake.createBook
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

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModelTest {

	private val testDispatcher = StandardTestDispatcher()

	private lateinit var fakeBookRepository: FakeBookRepository
	private lateinit var fakeUserPreferencesRepository: FakeUserPreferencesRepository
	private lateinit var viewModel: LibraryViewModel

	@BeforeTest
	fun setUp() {
		Dispatchers.setMain(testDispatcher)

		fakeBookRepository = FakeBookRepository()
		fakeUserPreferencesRepository = FakeUserPreferencesRepository()

		viewModel = LibraryViewModel(
			observeBooksUseCase = ObserveBooksUseCase(fakeBookRepository),
			observeUserPreferencesUseCase = ObserveUserPreferencesUseCase(
				fakeUserPreferencesRepository
			)
		)
	}

	@AfterTest
	fun tearDown() {
		Dispatchers.resetMain()
	}

	@Test
	fun `uiState contains books from repository`() = runTest {
		val collectJob = backgroundScope.launch {
			viewModel.uiState.collect {}
		}

		fakeBookRepository.books.value = listOf(
			createBook(id = BOOK_ID, title = BOOK_TITLE_2),
			createBook(id = BOOK_ID_2, title = BOOK_TITLE)
		)

		testDispatcher.scheduler.advanceUntilIdle()

		val state = viewModel.uiState.value

		assertEquals(2, state.books.size)
		assertEquals(BOOK_TITLE_2, state.books[0].title)
		assertEquals(BOOK_TITLE, state.books[1].title)
	}

	@Test
	fun `onSearchQueryChanged filters books by title`() = runTest {
		val collectJob = backgroundScope.launch {
			viewModel.uiState.collect {}
		}

		fakeBookRepository.books.value = listOf(
			createBook(id = BOOK_ID, title = BOOK_TITLE_2),
			createBook(id = BOOK_ID_2, title = BOOK_TITLE)
		)

		viewModel.onSearchQueryChanged("hobbit")
		testDispatcher.scheduler.advanceUntilIdle()

		val state = viewModel.uiState.value

		assertEquals("hobbit", state.searchQuery)
		assertEquals(1, state.books.size)
		assertEquals(BOOK_TITLE, state.books.first().title)
	}

	@Test
	fun `onSearchQueryChanged filters books by author`() = runTest {
		val collectJob = backgroundScope.launch {
			viewModel.uiState.collect {}
		}

		fakeBookRepository.books.value = listOf(
			createBook(id = BOOK_ID, title = BOOK_TITLE_2, author = "Frank Herbert"),
			createBook(id = BOOK_ID_2, title = BOOK_TITLE, author = "J R R Tolkien")
		)

		viewModel.onSearchQueryChanged("tolkien")
		testDispatcher.scheduler.advanceUntilIdle()

		val state = viewModel.uiState.value

		assertEquals(1, state.books.size)
		assertEquals(BOOK_TITLE, state.books.first().title)
	}

	@Test
	fun `onFilterSelected filters books by reading status`() = runTest {
		val collectJob = backgroundScope.launch {
			viewModel.uiState.collect {}
		}

		fakeBookRepository.books.value = listOf(
			createBook(id = BOOK_ID, title = BOOK_TITLE_2, status = ReadingStatus.READING),
			createBook(id = BOOK_ID_2, title = BOOK_TITLE, status = ReadingStatus.FINISHED),
			createBook(id = BOOK_ID_3, title = BOOK_TITLE_3, status = ReadingStatus.WANT_TO_READ)
		)

		viewModel.onFilterSelected(LibraryFilter.READING)
		testDispatcher.scheduler.advanceUntilIdle()

		val state = viewModel.uiState.value

		assertEquals(LibraryFilter.READING, state.selectedFilter)
		assertEquals(1, state.books.size)
		assertEquals(BOOK_TITLE_2, state.books.first().title)
	}

	@Test
	fun `onFilterSelected clears filter when selecting same filter again`() = runTest {
		val collectJob = backgroundScope.launch {
			viewModel.uiState.collect {}
		}

		fakeBookRepository.books.value = listOf(
			createBook(id = BOOK_ID, status = ReadingStatus.READING),
			createBook(id = BOOK_ID_2, status = ReadingStatus.FINISHED)
		)

		viewModel.onFilterSelected(LibraryFilter.READING)
		viewModel.onFilterSelected(LibraryFilter.READING)
		testDispatcher.scheduler.advanceUntilIdle()

		val state = viewModel.uiState.value

		assertEquals(LibraryFilter.ALL, state.selectedFilter)
		assertEquals(2, state.books.size)
	}

	@Test
	fun `uiState sorts books using default sort option from preferences`() = runTest {
		val collectJob = backgroundScope.launch {
			viewModel.uiState.collect {}
		}

		fakeBookRepository.books.value = listOf(
			createBook(id = BOOK_ID, title = "Z Book"),
			createBook(id = BOOK_ID_2, title = "A Book"),
			createBook(id = BOOK_ID_3, title = "M Book")
		)

		fakeUserPreferencesRepository.preferences.value = UserPreferences(
			defaultSortOption = BookSortOption.TITLE
		)

		testDispatcher.scheduler.advanceUntilIdle()

		val state = viewModel.uiState.value

		assertEquals("A Book", state.books[0].title)
		assertEquals("M Book", state.books[1].title)
		assertEquals("Z Book", state.books[2].title)
	}
}