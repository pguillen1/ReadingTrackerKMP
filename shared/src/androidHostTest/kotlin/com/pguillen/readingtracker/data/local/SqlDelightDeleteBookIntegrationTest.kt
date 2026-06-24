package com.pguillen.readingtracker.data.local

import com.pguillen.readingtracker.data.local.repository.SqlDelightBookNoteRepository
import com.pguillen.readingtracker.data.local.repository.SqlDelightBookRepository
import com.pguillen.readingtracker.data.local.repository.SqlDelightReadingSessionRepository
import com.pguillen.readingtracker.fake.BOOK_ID
import com.pguillen.readingtracker.fake.NOTE_ID
import com.pguillen.readingtracker.fake.SESSION_ID
import com.pguillen.readingtracker.fake.createBook
import com.pguillen.readingtracker.fake.createNote
import com.pguillen.readingtracker.fake.createSession
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SqlDelightDeleteBookIntegrationTest {

	private lateinit var bookRepository: SqlDelightBookRepository
	private lateinit var sessionRepository: SqlDelightReadingSessionRepository
	private lateinit var noteRepository: SqlDelightBookNoteRepository

	@BeforeTest
	fun setUp() {
		val database = createInMemoryDatabase()

		bookRepository = SqlDelightBookRepository(database)
		sessionRepository = SqlDelightReadingSessionRepository(database)
		noteRepository = SqlDelightBookNoteRepository(database)
	}

	@Test
	fun `deleteBook deletes book sessions and notes`() = runTest {
		bookRepository.insertBook(
			createBook(id = BOOK_ID)
		)

		sessionRepository.insertSession(
			createSession(
				id = SESSION_ID,
				bookId = BOOK_ID
			)
		)

		noteRepository.insertNote(
			createNote(
				id = NOTE_ID,
				bookId = BOOK_ID
			)
		)

		bookRepository.deleteBook(BOOK_ID)

		val deletedBook = bookRepository.getBookById(BOOK_ID)
		val sessions = sessionRepository.observeSessionsByBookId(BOOK_ID).first()
		val notes = noteRepository.observeNotesByBookId(BOOK_ID).first()

		assertNull(deletedBook)
		assertEquals(emptyList(), sessions)
		assertEquals(emptyList(), notes)
	}
}