package com.pguillen.readingtracker.domain.usecase.book

import com.pguillen.readingtracker.core.error.DomainException
import com.pguillen.readingtracker.domain.model.ReadingStatus
import com.pguillen.readingtracker.fake.BOOK_AUTHOR
import com.pguillen.readingtracker.fake.BOOK_AUTHOR_WITH_SPACES
import com.pguillen.readingtracker.fake.BOOK_CURRENT_PAGE
import com.pguillen.readingtracker.fake.BOOK_ID
import com.pguillen.readingtracker.fake.BOOK_TITLE
import com.pguillen.readingtracker.fake.BOOK_TITLE_WITH_SPACES
import com.pguillen.readingtracker.fake.BOOK_TOTAL_PAGES
import com.pguillen.readingtracker.fake.FakeBookRepository
import com.pguillen.readingtracker.fake.FakeDateTimeProvider
import com.pguillen.readingtracker.fake.FakeIdGenerator
import com.pguillen.readingtracker.fake.HIGH_CURRENT_PAGE
import com.pguillen.readingtracker.fake.NEGATIVE_TOTAL_PAGES
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class AddBookUseCaseTest {

	lateinit var fakeBookRepository: FakeBookRepository
	lateinit var fakeIdGenerator: FakeIdGenerator
	lateinit var fakeDateTimeProvider: FakeDateTimeProvider
	lateinit var addBookUseCase: AddBookUseCase

	@BeforeTest
	fun setUp() {
		fakeBookRepository = FakeBookRepository()
		fakeIdGenerator = FakeIdGenerator(mutableListOf(BOOK_ID))
		fakeDateTimeProvider = FakeDateTimeProvider()
		addBookUseCase = AddBookUseCase(fakeBookRepository, fakeIdGenerator, fakeDateTimeProvider)
	}

	@Test
	fun `invoke creates book with generated id and timestamps`() = runTest {

		addBookUseCase(
			AddBookParams(
				title = BOOK_TITLE,
				author = BOOK_AUTHOR,
				totalPages = BOOK_TOTAL_PAGES,
				currentPage = BOOK_CURRENT_PAGE,
				status = ReadingStatus.WANT_TO_READ
			)
		)

		val book = fakeBookRepository.getBookById(BOOK_ID)

		assertNotNull(book)
		assertEquals(BOOK_ID, book.id)
		assertEquals(BOOK_TITLE, book.title)
		assertEquals(BOOK_AUTHOR, book.author)
		assertEquals(BOOK_TOTAL_PAGES, book.totalPages)
		assertEquals(BOOK_CURRENT_PAGE, book.currentPage)
		assertEquals(ReadingStatus.WANT_TO_READ, book.status)
		assertEquals(fakeDateTimeProvider.now(), book.addedAt)
		assertEquals(fakeDateTimeProvider.now(), book.updatedAt)
	}

	@Test
	fun `invoke trims title and author`() = runTest {

		addBookUseCase(
			AddBookParams(
				title = BOOK_TITLE_WITH_SPACES,
				author = BOOK_AUTHOR_WITH_SPACES,
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
	fun `invoke sets current page to total pages when status is finished`() = runTest {

		addBookUseCase(
			AddBookParams(
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
	fun `invoke throws validation error when title is blank`() = runTest {

		assertFailsWith<DomainException.Validation> {
			addBookUseCase(
				AddBookParams(
					title = "   ",
					author = BOOK_AUTHOR,
					totalPages = BOOK_TOTAL_PAGES,
					currentPage = BOOK_CURRENT_PAGE,
					status = ReadingStatus.WANT_TO_READ
				)
			)
		}
	}

	@Test
	fun `invoke throws validation error when total pages is negative`() = runTest {

		assertFailsWith<DomainException.Validation> {
			addBookUseCase(
				AddBookParams(
					title = BOOK_TITLE,
					author = BOOK_AUTHOR,
					totalPages = NEGATIVE_TOTAL_PAGES,
					currentPage = BOOK_CURRENT_PAGE,
					status = ReadingStatus.WANT_TO_READ
				)
			)
		}
	}

	@Test
	fun `invoke throws validation error when current page is greater than total pages`() = runTest {

		assertFailsWith<DomainException.Validation> {
			addBookUseCase(
				AddBookParams(
					title = BOOK_TITLE,
					author = BOOK_AUTHOR,
					totalPages = BOOK_TOTAL_PAGES,
					currentPage = HIGH_CURRENT_PAGE,
					status = ReadingStatus.READING
				)
			)
		}
	}
}