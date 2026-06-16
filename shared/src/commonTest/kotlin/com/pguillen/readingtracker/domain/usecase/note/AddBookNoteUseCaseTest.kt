package com.pguillen.readingtracker.domain.usecase.note

import com.pguillen.readingtracker.core.error.DomainException
import com.pguillen.readingtracker.domain.model.BookNoteType
import com.pguillen.readingtracker.fake.BOOK_ID
import com.pguillen.readingtracker.fake.FakeBookNoteRepository
import com.pguillen.readingtracker.fake.FakeBookRepository
import com.pguillen.readingtracker.fake.FakeDateTimeProvider
import com.pguillen.readingtracker.fake.FakeIdGenerator
import com.pguillen.readingtracker.fake.NOTE_CONTENT
import com.pguillen.readingtracker.fake.NOTE_ID
import com.pguillen.readingtracker.fake.createBook
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class AddBookNoteUseCaseTest {

	private lateinit var fakeBookRepository: FakeBookRepository
	private lateinit var fakeBookNoteRepository: FakeBookNoteRepository
	private lateinit var fakeIdGenerator: FakeIdGenerator
	private lateinit var fakeDateTimeProvider: FakeDateTimeProvider
	private lateinit var addBookNoteUseCase: AddBookNoteUseCase

	@BeforeTest
	fun setUp() {
		fakeBookRepository = FakeBookRepository()
		fakeBookNoteRepository = FakeBookNoteRepository()
		fakeIdGenerator = FakeIdGenerator(mutableListOf(NOTE_ID))
		fakeDateTimeProvider = FakeDateTimeProvider()

		addBookNoteUseCase = AddBookNoteUseCase(
			bookRepository = fakeBookRepository,
			bookNoteRepository = fakeBookNoteRepository,
			idGenerator = fakeIdGenerator,
			dateTimeProvider = fakeDateTimeProvider
		)
	}

	@Test
	fun `invoke creates note`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(id = BOOK_ID)
		)

		addBookNoteUseCase(
			AddBookNoteParams(
				bookId = BOOK_ID,
				type = BookNoteType.NOTE,
				content = NOTE_CONTENT,
				page = 25
			)
		)

		val note = fakeBookNoteRepository.getNoteById(NOTE_ID)

		assertNotNull(note)
		assertEquals(NOTE_ID, note.id)
		assertEquals(BOOK_ID, note.bookId)
		assertEquals(BookNoteType.NOTE, note.type)
		assertEquals(NOTE_CONTENT, note.content)
		assertEquals(25, note.page)
		assertEquals(fakeDateTimeProvider.now(), note.createdAt)
		assertEquals(fakeDateTimeProvider.now(), note.updatedAt)
	}

	@Test
	fun `invoke trims content`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(id = BOOK_ID)
		)

		addBookNoteUseCase(
			AddBookNoteParams(
				bookId = BOOK_ID,
				type = BookNoteType.QUOTE,
				content = "  $NOTE_CONTENT  ",
				page = null
			)
		)

		val note = fakeBookNoteRepository.getNoteById(NOTE_ID)

		assertNotNull(note)
		assertEquals(NOTE_CONTENT, note.content)
	}

	@Test
	fun `invoke throws not found error when book does not exist`() = runTest {
		assertFailsWith<DomainException.NotFound> {
			addBookNoteUseCase(
				AddBookNoteParams(
					bookId = BOOK_ID,
					type = BookNoteType.NOTE,
					content = NOTE_CONTENT,
					page = null
				)
			)
		}
	}

	@Test
	fun `invoke throws validation error when content is blank`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(id = BOOK_ID)
		)

		assertFailsWith<DomainException.Validation> {
			addBookNoteUseCase(
				AddBookNoteParams(
					bookId = BOOK_ID,
					type = BookNoteType.NOTE,
					content = "   ",
					page = null
				)
			)
		}
	}

	@Test
	fun `invoke throws validation error when page is negative`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(id = BOOK_ID)
		)

		assertFailsWith<DomainException.Validation> {
			addBookNoteUseCase(
				AddBookNoteParams(
					bookId = BOOK_ID,
					type = BookNoteType.NOTE,
					content = NOTE_CONTENT,
					page = -1
				)
			)
		}
	}
}