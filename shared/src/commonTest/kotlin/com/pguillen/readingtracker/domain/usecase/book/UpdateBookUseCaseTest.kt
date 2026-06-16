package com.pguillen.readingtracker.domain.usecase.book

import com.pguillen.readingtracker.core.error.DomainException
import com.pguillen.readingtracker.domain.model.ReadingStatus
import com.pguillen.readingtracker.fake.BOOK_AUTHOR
import com.pguillen.readingtracker.fake.BOOK_CURRENT_PAGE
import com.pguillen.readingtracker.fake.BOOK_ID
import com.pguillen.readingtracker.fake.BOOK_TITLE
import com.pguillen.readingtracker.fake.BOOK_TOTAL_PAGES
import com.pguillen.readingtracker.fake.FakeBookRepository
import com.pguillen.readingtracker.fake.FakeDateTimeProvider
import com.pguillen.readingtracker.fake.HIGH_CURRENT_PAGE
import com.pguillen.readingtracker.fake.NEGATIVE_TOTAL_PAGES
import com.pguillen.readingtracker.fake.createBook
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class UpdateBookUseCaseTest {

	private lateinit var fakeBookRepository: FakeBookRepository
	private lateinit var fakeDateTimeProvider: FakeDateTimeProvider
	private lateinit var updateBookUseCase: UpdateBookUseCase

	@BeforeTest
	fun setUp() {
		fakeBookRepository = FakeBookRepository()
		fakeDateTimeProvider = FakeDateTimeProvider()

		updateBookUseCase = UpdateBookUseCase(
			bookRepository = fakeBookRepository,
			dateTimeProvider = fakeDateTimeProvider
		)
	}

	@Test
	fun `invoke updates existing book`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(
				id = BOOK_ID,
				title = "Old title",
				author = "Old author",
				totalPages = 200,
				currentPage = 20,
				status = ReadingStatus.WANT_TO_READ
			)
		)

		updateBookUseCase(
			UpdateBookParams(
				bookId = BOOK_ID,
				title = BOOK_TITLE,
				author = BOOK_AUTHOR,
				totalPages = BOOK_TOTAL_PAGES,
				currentPage = BOOK_CURRENT_PAGE,
				status = ReadingStatus.READING
			)
		)

		val book = fakeBookRepository.getBookById(BOOK_ID)

		assertNotNull(book)
		assertEquals(BOOK_TITLE, book.title)
		assertEquals(BOOK_AUTHOR, book.author)
		assertEquals(BOOK_TOTAL_PAGES, book.totalPages)
		assertEquals(BOOK_CURRENT_PAGE, book.currentPage)
		assertEquals(ReadingStatus.READING, book.status)
		assertEquals(fakeDateTimeProvider.now(), book.updatedAt)
	}

	@Test
	fun `invoke trims title and author`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(id = BOOK_ID)
		)

		updateBookUseCase(
			UpdateBookParams(
				bookId = BOOK_ID,
				title = "  $BOOK_TITLE  ",
				author = "  $BOOK_AUTHOR  ",
				totalPages = BOOK_TOTAL_PAGES,
				currentPage = BOOK_CURRENT_PAGE,
				status = ReadingStatus.READING
			)
		)

		val book = fakeBookRepository.getBookById(BOOK_ID)

		assertNotNull(book)
		assertEquals(BOOK_TITLE, book.title)
		assertEquals(BOOK_AUTHOR, book.author)
	}

	@Test
	fun `invoke throws not found error when book does not exist`() = runTest {
		assertFailsWith<DomainException.NotFound> {
			updateBookUseCase(
				UpdateBookParams(
					bookId = BOOK_ID,
					title = BOOK_TITLE,
					author = BOOK_AUTHOR,
					totalPages = BOOK_TOTAL_PAGES,
					currentPage = BOOK_CURRENT_PAGE,
					status = ReadingStatus.READING
				)
			)
		}
	}

	@Test
	fun `invoke throws validation error when title is blank`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(id = BOOK_ID)
		)

		assertFailsWith<DomainException.Validation> {
			updateBookUseCase(
				UpdateBookParams(
					bookId = BOOK_ID,
					title = "   ",
					author = BOOK_AUTHOR,
					totalPages = BOOK_TOTAL_PAGES,
					currentPage = BOOK_CURRENT_PAGE,
					status = ReadingStatus.READING
				)
			)
		}
	}

	@Test
	fun `invoke throws validation error when total pages is negative`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(id = BOOK_ID)
		)

		assertFailsWith<DomainException.Validation> {
			updateBookUseCase(
				UpdateBookParams(
					bookId = BOOK_ID,
					title = BOOK_TITLE,
					author = BOOK_AUTHOR,
					totalPages = NEGATIVE_TOTAL_PAGES,
					currentPage = BOOK_CURRENT_PAGE,
					status = ReadingStatus.READING
				)
			)
		}
	}

	@Test
	fun `invoke throws validation error when current page is greater than total pages`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(id = BOOK_ID)
		)

		assertFailsWith<DomainException.Validation> {
			updateBookUseCase(
				UpdateBookParams(
					bookId = BOOK_ID,
					title = BOOK_TITLE,
					author = BOOK_AUTHOR,
					totalPages = BOOK_TOTAL_PAGES,
					currentPage = HIGH_CURRENT_PAGE,
					status = ReadingStatus.READING
				)
			)
		}
	}

	@Test
	fun `invoke sets current page to total pages when status is finished`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(
				id = BOOK_ID,
				totalPages = 300,
				currentPage = 120,
				status = ReadingStatus.READING
			)
		)

		updateBookUseCase(
			UpdateBookParams(
				bookId = BOOK_ID,
				title = BOOK_TITLE,
				author = BOOK_AUTHOR,
				totalPages = 300,
				currentPage = 120,
				status = ReadingStatus.FINISHED
			)
		)

		val book = fakeBookRepository.getBookById(BOOK_ID)

		assertNotNull(book)
		assertEquals(300, book.currentPage)
		assertEquals(ReadingStatus.FINISHED, book.status)
	}

	@Test
	fun `invoke sets finished date when status changes to finished`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(
				id = BOOK_ID,
				totalPages = 300,
				currentPage = 120,
				status = ReadingStatus.READING,
				finishedAt = null
			)
		)

		updateBookUseCase(
			UpdateBookParams(
				bookId = BOOK_ID,
				title = BOOK_TITLE,
				author = BOOK_AUTHOR,
				totalPages = 300,
				currentPage = 120,
				status = ReadingStatus.FINISHED
			)
		)

		val book = fakeBookRepository.getBookById(BOOK_ID)

		assertNotNull(book)
		assertEquals(fakeDateTimeProvider.today(), book.finishedAt)
	}

	@Test
	fun `invoke clears finished date when status changes from finished to reading`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(
				id = BOOK_ID,
				totalPages = 300,
				currentPage = 300,
				status = ReadingStatus.FINISHED,
				finishedAt = fakeDateTimeProvider.today()
			)
		)

		updateBookUseCase(
			UpdateBookParams(
				bookId = BOOK_ID,
				title = BOOK_TITLE,
				author = BOOK_AUTHOR,
				totalPages = 300,
				currentPage = 180,
				status = ReadingStatus.READING
			)
		)

		val book = fakeBookRepository.getBookById(BOOK_ID)

		assertNotNull(book)
		assertEquals(ReadingStatus.READING, book.status)
		assertNull(book.finishedAt)
	}
}