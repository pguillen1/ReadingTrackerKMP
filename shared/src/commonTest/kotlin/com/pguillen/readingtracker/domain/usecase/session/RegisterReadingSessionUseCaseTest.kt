package com.pguillen.readingtracker.domain.usecase.session

import com.pguillen.readingtracker.core.error.DomainException
import com.pguillen.readingtracker.domain.model.ReadingStatus
import com.pguillen.readingtracker.fake.BOOK_CURRENT_PAGE
import com.pguillen.readingtracker.fake.BOOK_ID
import com.pguillen.readingtracker.fake.BOOK_TOTAL_PAGES
import com.pguillen.readingtracker.fake.FakeBookRepository
import com.pguillen.readingtracker.fake.FakeDateTimeProvider
import com.pguillen.readingtracker.fake.FakeIdGenerator
import com.pguillen.readingtracker.fake.FakeReadingSessionRepository
import com.pguillen.readingtracker.fake.SESSION_END_PAGE
import com.pguillen.readingtracker.fake.SESSION_ID
import com.pguillen.readingtracker.fake.SESSION_MINUTES
import com.pguillen.readingtracker.fake.SESSION_NOTE
import com.pguillen.readingtracker.fake.SESSION_START_PAGE
import com.pguillen.readingtracker.fake.createBook
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterReadingSessionUseCaseTest {

	lateinit var fakeReadingSessionRepository: FakeReadingSessionRepository
	lateinit var fakeIdGenerator: FakeIdGenerator
	lateinit var fakeDateTimeProvider: FakeDateTimeProvider
	lateinit var fakeBookRepository: FakeBookRepository
	lateinit var registerReadingSessionUseCase: RegisterReadingSessionUseCase

	@BeforeTest
	fun setUp() {
		fakeBookRepository = FakeBookRepository()
		fakeReadingSessionRepository = FakeReadingSessionRepository()
		fakeIdGenerator = FakeIdGenerator(mutableListOf(SESSION_ID))
		fakeDateTimeProvider = FakeDateTimeProvider()
		registerReadingSessionUseCase = RegisterReadingSessionUseCase(
			fakeBookRepository,
			fakeReadingSessionRepository,
			fakeIdGenerator,
			fakeDateTimeProvider
		)
	}

	@Test
	fun `invoke creates reading session`() = runTest {

		fakeBookRepository.books.value = listOf(createBook())

		registerReadingSessionUseCase(
			RegisterReadingSessionParams(
				bookId = BOOK_ID,
				date = LocalDate(2026, 1, 15),
				startPage = SESSION_START_PAGE,
				endPage = SESSION_END_PAGE,
				minutes = SESSION_MINUTES,
				note = SESSION_NOTE
			)
		)

		val session = fakeReadingSessionRepository.getSessionById(SESSION_ID)

		assertNotNull(session)
		assertEquals(BOOK_ID, session.bookId)
		assertEquals(LocalDate(2026, 1, 15), session.date)
		assertEquals(SESSION_START_PAGE, session.startPage)
		assertEquals(SESSION_END_PAGE, session.endPage)
		assertEquals(SESSION_MINUTES, session.minutes)
		assertEquals(SESSION_NOTE, session.note)
		assertEquals(fakeDateTimeProvider.now(), session.createdAt)
	}

	@Test
	fun `invoke updates book current page when session end page is greater`() = runTest {

		fakeBookRepository.books.value =
			listOf(createBook(currentPage = BOOK_CURRENT_PAGE, totalPages = BOOK_TOTAL_PAGES))

		registerReadingSessionUseCase(
			RegisterReadingSessionParams(
				bookId = BOOK_ID,
				date = LocalDate(2026, 1, 15),
				startPage = BOOK_CURRENT_PAGE,
				endPage = SESSION_END_PAGE,
				minutes = SESSION_MINUTES,
				note = null
			)
		)

		val updatedBook = fakeBookRepository.getBookById(BOOK_ID)

		assertNotNull(updatedBook)
		assertEquals(SESSION_END_PAGE, updatedBook.currentPage)
		assertEquals(ReadingStatus.READING, updatedBook.status)
	}

	@Test
	fun `invoke marks book as finished when end page reaches total pages`() = runTest {

		fakeBookRepository.books.value = listOf(createBook())

		registerReadingSessionUseCase(
			RegisterReadingSessionParams(
				bookId = BOOK_ID,
				date = LocalDate(2026, 1, 15),
				startPage = BOOK_CURRENT_PAGE,
				endPage = BOOK_TOTAL_PAGES,
				minutes = SESSION_MINUTES,
				note = null
			)
		)

		val updatedBook = fakeBookRepository.getBookById(BOOK_ID)

		assertNotNull(updatedBook)
		assertEquals(BOOK_TOTAL_PAGES, updatedBook.currentPage)
		assertEquals(ReadingStatus.FINISHED, updatedBook.status)
		assertEquals(LocalDate(2026, 1, 15), updatedBook.finishedAt)
	}

	@Test
	fun `invoke does not decrease book current page when end page is lower`() = runTest {

		fakeBookRepository.books.value = listOf(createBook(currentPage = SESSION_END_PAGE))

		registerReadingSessionUseCase(
			RegisterReadingSessionParams(
				bookId = BOOK_ID,
				date = LocalDate(2026, 1, 15),
				startPage = BOOK_CURRENT_PAGE,
				endPage = SESSION_START_PAGE,
				minutes = SESSION_MINUTES,
				note = null
			)
		)

		val updatedBook = fakeBookRepository.getBookById(BOOK_ID)

		assertNotNull(updatedBook)
		assertEquals(SESSION_END_PAGE, updatedBook.currentPage)
	}

	@Test
	fun `invoke throws validation error when end page is blank or invalid`() = runTest {

		fakeBookRepository.books.value = listOf(createBook())

		assertFailsWith<DomainException.Validation> {
			registerReadingSessionUseCase(
				RegisterReadingSessionParams(
					bookId = BOOK_ID,
					date = LocalDate(2026, 1, 15),
					startPage = SESSION_START_PAGE,
					endPage = 0,
					minutes = SESSION_MINUTES,
					note = null
				)
			)
		}
	}

	@Test
	fun `invoke throws validation error when end page is greater than total pages`() = runTest {

		fakeBookRepository.books.value = listOf(createBook(totalPages = SESSION_END_PAGE))

		assertFailsWith<DomainException.Validation> {
			registerReadingSessionUseCase(
				RegisterReadingSessionParams(
					bookId = BOOK_ID,
					date = LocalDate(2026, 1, 15),
					startPage = SESSION_START_PAGE,
					endPage = BOOK_TOTAL_PAGES,
					minutes = SESSION_MINUTES,
					note = null
				)
			)
		}
	}
}