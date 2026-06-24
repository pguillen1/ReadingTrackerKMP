package com.pguillen.readingtracker.data.local

import com.pguillen.readingtracker.data.local.repository.SqlDelightBookRepository
import com.pguillen.readingtracker.data.local.repository.SqlDelightReadingSessionRepository
import com.pguillen.readingtracker.fake.BOOK_ID
import com.pguillen.readingtracker.fake.BOOK_ID_2
import com.pguillen.readingtracker.fake.SESSION_ID
import com.pguillen.readingtracker.fake.SESSION_ID_2
import com.pguillen.readingtracker.fake.createBook
import com.pguillen.readingtracker.fake.createSession
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SqlDelightReadingSessionRepositoryTest {

	private lateinit var bookRepository: SqlDelightBookRepository
	private lateinit var sessionRepository: SqlDelightReadingSessionRepository

	@BeforeTest
	fun setUp() {
		val database = createInMemoryDatabase()

		bookRepository = SqlDelightBookRepository(database)
		sessionRepository = SqlDelightReadingSessionRepository(database)
	}

	@Test
	fun `insertSession saves session in database`() = runTest {
		bookRepository.insertBook(
			createBook(id = BOOK_ID)
		)

		val session = createSession(
			id = SESSION_ID,
			bookId = BOOK_ID,
			startPage = 10,
			endPage = 40,
			minutes = 30
		)

		sessionRepository.insertSession(session)

		val savedSession = sessionRepository.getSessionById(SESSION_ID)

		assertEquals(session, savedSession)
	}

	@Test
	fun `observeSessionsByBookId returns only sessions for that book`() = runTest {
		bookRepository.insertBook(createBook(id = BOOK_ID))
		bookRepository.insertBook(createBook(id = BOOK_ID_2))

		sessionRepository.insertSession(
			createSession(
				id = SESSION_ID,
				bookId = BOOK_ID
			)
		)

		sessionRepository.insertSession(
			createSession(
				id = SESSION_ID_2,
				bookId = BOOK_ID_2
			)
		)

		val sessions = sessionRepository.observeSessionsByBookId(BOOK_ID).first()

		assertEquals(1, sessions.size)
		assertEquals(SESSION_ID, sessions.first().id)
	}

	@Test
	fun `updateSession updates existing session`() = runTest {
		bookRepository.insertBook(
			createBook(id = BOOK_ID)
		)

		sessionRepository.insertSession(
			createSession(
				id = SESSION_ID,
				bookId = BOOK_ID,
				endPage = 30,
				minutes = 20
			)
		)

		sessionRepository.updateSession(
			createSession(
				id = SESSION_ID,
				bookId = BOOK_ID,
				date = LocalDate(2026, 2, 1),
				startPage = 30,
				endPage = 70,
				minutes = 45,
				note = "Updated"
			)
		)

		val session = sessionRepository.getSessionById(SESSION_ID)

		assertEquals(LocalDate(2026, 2, 1), session?.date)
		assertEquals(30, session?.startPage)
		assertEquals(70, session?.endPage)
		assertEquals(45, session?.minutes)
		assertEquals("Updated", session?.note)
	}

	@Test
	fun `deleteSession deletes existing session`() = runTest {
		bookRepository.insertBook(
			createBook(id = BOOK_ID)
		)

		sessionRepository.insertSession(
			createSession(
				id = SESSION_ID,
				bookId = BOOK_ID
			)
		)

		sessionRepository.deleteSession(SESSION_ID)

		val session = sessionRepository.getSessionById(SESSION_ID)

		assertNull(session)
	}
}