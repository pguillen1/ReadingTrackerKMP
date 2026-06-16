package com.pguillen.readingtracker.domain.usecase.session

import com.pguillen.readingtracker.core.error.DomainException
import com.pguillen.readingtracker.domain.model.ReadingStatus
import com.pguillen.readingtracker.fake.BOOK_ID
import com.pguillen.readingtracker.fake.FakeBookRepository
import com.pguillen.readingtracker.fake.FakeDateTimeProvider
import com.pguillen.readingtracker.fake.FakeReadingSessionRepository
import com.pguillen.readingtracker.fake.SESSION_ID
import com.pguillen.readingtracker.fake.UPDATED_SESSION_DATE
import com.pguillen.readingtracker.fake.createBook
import com.pguillen.readingtracker.fake.createSession
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class UpdateReadingSessionUseCaseTest {

	private lateinit var fakeBookRepository: FakeBookRepository
	private lateinit var fakeReadingSessionRepository: FakeReadingSessionRepository
	private lateinit var fakeDateTimeProvider: FakeDateTimeProvider
	private lateinit var updateReadingSessionUseCase: UpdateReadingSessionUseCase

	@BeforeTest
	fun setUp() {
		fakeBookRepository = FakeBookRepository()
		fakeReadingSessionRepository = FakeReadingSessionRepository()
		fakeDateTimeProvider = FakeDateTimeProvider()

		updateReadingSessionUseCase = UpdateReadingSessionUseCase(
			bookRepository = fakeBookRepository,
			readingSessionRepository = fakeReadingSessionRepository,
			dateTimeProvider = fakeDateTimeProvider
		)
	}

	@Test
	fun `invoke updates existing reading session`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(
				id = BOOK_ID,
				currentPage = 20,
				totalPages = 100,
				status = ReadingStatus.READING
			)
		)

		fakeReadingSessionRepository.sessions.value = listOf(
			createSession(
				id = SESSION_ID,
				bookId = BOOK_ID,
				startPage = 10,
				endPage = 20,
				minutes = 15,
				note = "Old note"
			)
		)

		updateReadingSessionUseCase(
			UpdateReadingSessionParams(
				sessionId = SESSION_ID,
				date = UPDATED_SESSION_DATE,
				startPage = 20,
				endPage = 50,
				minutes = 30,
				note = "Updated note"
			)
		)

		val session = fakeReadingSessionRepository.getSessionById(SESSION_ID)

		assertNotNull(session)
		assertEquals(UPDATED_SESSION_DATE, session.date)
		assertEquals(20, session.startPage)
		assertEquals(50, session.endPage)
		assertEquals(30, session.minutes)
		assertEquals("Updated note", session.note)
	}

	@Test
	fun `invoke updates book current page when edited end page is greater`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(
				id = BOOK_ID,
				currentPage = 20,
				totalPages = 100,
				status = ReadingStatus.READING
			)
		)

		fakeReadingSessionRepository.sessions.value = listOf(
			createSession(
				id = SESSION_ID,
				bookId = BOOK_ID,
				startPage = 10,
				endPage = 20
			)
		)

		updateReadingSessionUseCase(
			UpdateReadingSessionParams(
				sessionId = SESSION_ID,
				date = UPDATED_SESSION_DATE,
				startPage = 20,
				endPage = 75,
				minutes = 45,
				note = null
			)
		)

		val book = fakeBookRepository.getBookById(BOOK_ID)

		assertNotNull(book)
		assertEquals(75, book.currentPage)
		assertEquals(ReadingStatus.READING, book.status)
		assertEquals(fakeDateTimeProvider.now(), book.updatedAt)
	}

	@Test
	fun `invoke marks book as finished when edited end page reaches total pages`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(
				id = BOOK_ID,
				currentPage = 80,
				totalPages = 100,
				status = ReadingStatus.READING,
				finishedAt = null
			)
		)

		fakeReadingSessionRepository.sessions.value = listOf(
			createSession(
				id = SESSION_ID,
				bookId = BOOK_ID,
				startPage = 70,
				endPage = 80
			)
		)

		updateReadingSessionUseCase(
			UpdateReadingSessionParams(
				sessionId = SESSION_ID,
				date = UPDATED_SESSION_DATE,
				startPage = 80,
				endPage = 100,
				minutes = 30,
				note = null
			)
		)

		val book = fakeBookRepository.getBookById(BOOK_ID)

		assertNotNull(book)
		assertEquals(100, book.currentPage)
		assertEquals(ReadingStatus.FINISHED, book.status)
		assertEquals(UPDATED_SESSION_DATE, book.finishedAt)
	}

	@Test
	fun `invoke does not decrease book current page when edited end page is lower`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(
				id = BOOK_ID,
				currentPage = 90,
				totalPages = 100,
				status = ReadingStatus.READING
			)
		)

		fakeReadingSessionRepository.sessions.value = listOf(
			createSession(
				id = SESSION_ID,
				bookId = BOOK_ID,
				startPage = 80,
				endPage = 90
			)
		)

		updateReadingSessionUseCase(
			UpdateReadingSessionParams(
				sessionId = SESSION_ID,
				date = UPDATED_SESSION_DATE,
				startPage = 20,
				endPage = 40,
				minutes = 15,
				note = null
			)
		)

		val book = fakeBookRepository.getBookById(BOOK_ID)

		assertNotNull(book)
		assertEquals(90, book.currentPage)
	}

	@Test
	fun `invoke throws not found error when session does not exist`() = runTest {
		assertFailsWith<DomainException.NotFound> {
			updateReadingSessionUseCase(
				UpdateReadingSessionParams(
					sessionId = SESSION_ID,
					date = UPDATED_SESSION_DATE,
					startPage = 10,
					endPage = 20,
					minutes = 15,
					note = null
				)
			)
		}
	}

	@Test
	fun `invoke throws not found error when related book does not exist`() = runTest {
		fakeReadingSessionRepository.sessions.value = listOf(
			createSession(
				id = SESSION_ID,
				bookId = BOOK_ID
			)
		)

		assertFailsWith<DomainException.NotFound> {
			updateReadingSessionUseCase(
				UpdateReadingSessionParams(
					sessionId = SESSION_ID,
					date = UPDATED_SESSION_DATE,
					startPage = 10,
					endPage = 20,
					minutes = 15,
					note = null
				)
			)
		}
	}

	@Test
	fun `invoke throws validation error when end page is greater than total pages`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(
				id = BOOK_ID,
				totalPages = 100,
				currentPage = 60
			)
		)

		fakeReadingSessionRepository.sessions.value = listOf(
			createSession(
				id = SESSION_ID,
				bookId = BOOK_ID
			)
		)

		assertFailsWith<DomainException.Validation> {
			updateReadingSessionUseCase(
				UpdateReadingSessionParams(
					sessionId = SESSION_ID,
					date = UPDATED_SESSION_DATE,
					startPage = 90,
					endPage = 120,
					minutes = 20,
					note = null
				)
			)
		}
	}

	@Test
	fun `invoke throws validation error when start page is greater than end page`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(
				id = BOOK_ID,
				totalPages = 100,
				currentPage = 60
			)
		)

		fakeReadingSessionRepository.sessions.value = listOf(
			createSession(
				id = SESSION_ID,
				bookId = BOOK_ID
			)
		)

		assertFailsWith<DomainException.Validation> {
			updateReadingSessionUseCase(
				UpdateReadingSessionParams(
					sessionId = SESSION_ID,
					date = UPDATED_SESSION_DATE,
					startPage = 70,
					endPage = 60,
					minutes = 20,
					note = null
				)
			)
		}
	}

	@Test
	fun `invoke throws validation error when minutes is negative`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(
				id = BOOK_ID,
				totalPages = 100,
				currentPage = 60
			)
		)

		fakeReadingSessionRepository.sessions.value = listOf(
			createSession(
				id = SESSION_ID,
				bookId = BOOK_ID
			)
		)

		assertFailsWith<DomainException.Validation> {
			updateReadingSessionUseCase(
				UpdateReadingSessionParams(
					sessionId = SESSION_ID,
					date = UPDATED_SESSION_DATE,
					startPage = 50,
					endPage = 60,
					minutes = -5,
					note = null
				)
			)
		}
	}
}