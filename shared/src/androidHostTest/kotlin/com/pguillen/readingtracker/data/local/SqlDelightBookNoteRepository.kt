package com.pguillen.readingtracker.data.local

import com.pguillen.readingtracker.data.local.repository.SqlDelightBookNoteRepository
import com.pguillen.readingtracker.data.local.repository.SqlDelightBookRepository
import com.pguillen.readingtracker.domain.model.BookNoteType
import com.pguillen.readingtracker.fake.BOOK_ID
import com.pguillen.readingtracker.fake.BOOK_ID_2
import com.pguillen.readingtracker.fake.NOTE_CONTENT
import com.pguillen.readingtracker.fake.NOTE_ID
import com.pguillen.readingtracker.fake.UPDATED_NOTE_CONTENT
import com.pguillen.readingtracker.fake.createBook
import com.pguillen.readingtracker.fake.createNote
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SqlDelightBookNoteRepositoryTest {

	private lateinit var bookRepository: SqlDelightBookRepository
	private lateinit var noteRepository: SqlDelightBookNoteRepository

	@BeforeTest
	fun setUp() {
		val database = createInMemoryDatabase()

		bookRepository = SqlDelightBookRepository(database)
		noteRepository = SqlDelightBookNoteRepository(database)
	}

	@Test
	fun `insertNote saves note in database`() = runTest {
		bookRepository.insertBook(
			createBook(id = BOOK_ID)
		)

		val note = createNote(
			id = NOTE_ID,
			bookId = BOOK_ID,
			type = BookNoteType.QUOTE,
			content = NOTE_CONTENT,
			page = 42
		)

		noteRepository.insertNote(note)

		val savedNote = noteRepository.getNoteById(NOTE_ID)

		assertEquals(note, savedNote)
	}

	@Test
	fun `observeNotesByBookId returns only notes for that book`() = runTest {
		bookRepository.insertBook(createBook(id = BOOK_ID))
		bookRepository.insertBook(createBook(id = BOOK_ID_2))

		noteRepository.insertNote(
			createNote(
				id = NOTE_ID,
				bookId = BOOK_ID
			)
		)

		noteRepository.insertNote(
			createNote(
				id = "note-2",
				bookId = BOOK_ID_2
			)
		)

		val notes = noteRepository.observeNotesByBookId(BOOK_ID).first()

		assertEquals(1, notes.size)
		assertEquals(NOTE_ID, notes.first().id)
	}

	@Test
	fun `updateNote updates existing note`() = runTest {
		bookRepository.insertBook(
			createBook(id = BOOK_ID)
		)

		noteRepository.insertNote(
			createNote(
				id = NOTE_ID,
				bookId = BOOK_ID,
				type = BookNoteType.NOTE,
				content = NOTE_CONTENT,
				page = 10
			)
		)

		noteRepository.updateNote(
			createNote(
				id = NOTE_ID,
				bookId = BOOK_ID,
				type = BookNoteType.QUOTE,
				content = UPDATED_NOTE_CONTENT,
				page = 50
			)
		)

		val note = noteRepository.getNoteById(NOTE_ID)

		assertEquals(BookNoteType.QUOTE, note?.type)
		assertEquals(UPDATED_NOTE_CONTENT, note?.content)
		assertEquals(50, note?.page)
	}

	@Test
	fun `deleteNote deletes existing note`() = runTest {
		bookRepository.insertBook(
			createBook(id = BOOK_ID)
		)

		noteRepository.insertNote(
			createNote(
				id = NOTE_ID,
				bookId = BOOK_ID
			)
		)

		noteRepository.deleteNote(NOTE_ID)

		val note = noteRepository.getNoteById(NOTE_ID)

		assertNull(note)
	}
}