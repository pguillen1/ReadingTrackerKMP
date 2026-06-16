package com.pguillen.readingtracker.domain.usecase.note

import com.pguillen.readingtracker.core.error.DomainException
import com.pguillen.readingtracker.domain.model.BookNoteType
import com.pguillen.readingtracker.fake.BOOK_ID
import com.pguillen.readingtracker.fake.FakeBookNoteRepository
import com.pguillen.readingtracker.fake.FakeBookRepository
import com.pguillen.readingtracker.fake.FakeDateTimeProvider
import com.pguillen.readingtracker.fake.NOTE_ID
import com.pguillen.readingtracker.fake.UPDATED_NOTE_CONTENT
import com.pguillen.readingtracker.fake.createBook
import com.pguillen.readingtracker.fake.createNote
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class UpdateBookNoteUseCaseTest {

	private lateinit var fakeBookRepository: FakeBookRepository
	private lateinit var fakeBookNoteRepository: FakeBookNoteRepository
	private lateinit var fakeDateTimeProvider: FakeDateTimeProvider
	private lateinit var updateBookNoteUseCase: UpdateBookNoteUseCase

	@BeforeTest
	fun setUp() {
		fakeBookRepository = FakeBookRepository()
		fakeBookNoteRepository = FakeBookNoteRepository()
		fakeDateTimeProvider = FakeDateTimeProvider()

		updateBookNoteUseCase = UpdateBookNoteUseCase(
			bookNoteRepository = fakeBookNoteRepository,
			dateTimeProvider = fakeDateTimeProvider,
			bookRepository = fakeBookRepository
		)
	}

	@Test
	fun `invoke updates existing note`() = runTest {

		fakeBookRepository.books.value = listOf(createBook())

		fakeBookNoteRepository.notes.value = listOf(
			createNote(
				id = NOTE_ID,
				type = BookNoteType.NOTE,
				content = "Old content",
				page = 10
			)
		)

		updateBookNoteUseCase(
			UpdateBookNoteParams(
				noteId = NOTE_ID,
				type = BookNoteType.QUOTE,
				content = UPDATED_NOTE_CONTENT,
				page = 35
			)
		)

		val note = fakeBookNoteRepository.getNoteById(NOTE_ID)

		assertNotNull(note)
		assertEquals(BookNoteType.QUOTE, note.type)
		assertEquals(UPDATED_NOTE_CONTENT, note.content)
		assertEquals(35, note.page)
		assertEquals(fakeDateTimeProvider.now(), note.updatedAt)
	}

	@Test
	fun `invoke keeps original createdAt when updating note`() = runTest {

		fakeBookRepository.books.value = listOf(createBook())

		val originalCreatedAt = LocalDateTime(2026, 1, 1, 10, 0)

		fakeBookNoteRepository.notes.value = listOf(
			createNote(
				id = NOTE_ID,
				createdAt = originalCreatedAt
			)
		)

		updateBookNoteUseCase(
			UpdateBookNoteParams(
				noteId = NOTE_ID,
				type = BookNoteType.NOTE,
				content = UPDATED_NOTE_CONTENT,
				page = null
			)
		)

		val note = fakeBookNoteRepository.getNoteById(NOTE_ID)

		assertNotNull(note)
		assertEquals(originalCreatedAt, note.createdAt)
		assertEquals(fakeDateTimeProvider.now(), note.updatedAt)
	}

	@Test
	fun `invoke trims content`() = runTest {

		fakeBookRepository.books.value = listOf(createBook())

		fakeBookNoteRepository.notes.value = listOf(
			createNote(id = NOTE_ID)
		)

		updateBookNoteUseCase(
			UpdateBookNoteParams(
				noteId = NOTE_ID,
				type = BookNoteType.NOTE,
				content = "  $UPDATED_NOTE_CONTENT  ",
				page = null
			)
		)

		val note = fakeBookNoteRepository.getNoteById(NOTE_ID)

		assertNotNull(note)
		assertEquals(UPDATED_NOTE_CONTENT, note.content)
	}

	@Test
	fun `invoke throws not found error when note does not exist`() = runTest {

		fakeBookRepository.books.value = listOf(createBook())

		assertFailsWith<DomainException.NotFound> {
			updateBookNoteUseCase(
				UpdateBookNoteParams(
					noteId = NOTE_ID,
					type = BookNoteType.NOTE,
					content = UPDATED_NOTE_CONTENT,
					page = null
				)
			)
		}
	}

	@Test
	fun `invoke throws validation error when content is blank`() = runTest {

		fakeBookRepository.books.value = listOf(createBook())

		fakeBookNoteRepository.notes.value = listOf(
			createNote(id = NOTE_ID)
		)

		assertFailsWith<DomainException.Validation> {
			updateBookNoteUseCase(
				UpdateBookNoteParams(
					noteId = NOTE_ID,
					type = BookNoteType.NOTE,
					content = "   ",
					page = null
				)
			)
		}
	}

	@Test
	fun `invoke throws validation error when page is negative`() = runTest {

		fakeBookRepository.books.value = listOf(createBook())

		fakeBookNoteRepository.notes.value = listOf(
			createNote(id = NOTE_ID)
		)

		assertFailsWith<DomainException.Validation> {
			updateBookNoteUseCase(
				UpdateBookNoteParams(
					noteId = NOTE_ID,
					type = BookNoteType.NOTE,
					content = UPDATED_NOTE_CONTENT,
					page = -5
				)
			)
		}
	}
}