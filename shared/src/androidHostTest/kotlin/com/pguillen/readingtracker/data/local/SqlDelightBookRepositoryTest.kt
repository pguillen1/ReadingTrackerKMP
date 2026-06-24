package com.pguillen.readingtracker.data.local

import com.pguillen.readingtracker.data.local.repository.SqlDelightBookRepository
import com.pguillen.readingtracker.domain.model.ReadingStatus
import com.pguillen.readingtracker.fake.BOOK_ID
import com.pguillen.readingtracker.fake.BOOK_ID_2
import com.pguillen.readingtracker.fake.BOOK_TITLE
import com.pguillen.readingtracker.fake.BOOK_TITLE_2
import com.pguillen.readingtracker.fake.createBook
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SqlDelightBookRepositoryTest {

	private lateinit var bookRepository: SqlDelightBookRepository

	@BeforeTest
	fun setUp() {
		val database = createInMemoryDatabase()
		bookRepository = SqlDelightBookRepository(database)
	}

	@Test
	fun `insertBook saves book in database`() = runTest {
		val book = createBook(
			id = BOOK_ID,
			title = BOOK_TITLE
		)

		bookRepository.insertBook(book)

		val savedBook = bookRepository.getBookById(BOOK_ID)

		assertEquals(book, savedBook)
	}

	@Test
	fun `observeBooks returns inserted books`() = runTest {
		bookRepository.insertBook(
			createBook(id = BOOK_ID, title = BOOK_TITLE)
		)

		bookRepository.insertBook(
			createBook(id = BOOK_ID_2, title = BOOK_TITLE_2)
		)

		val books = bookRepository.observeBooks().first()

		val titles = books.map { it.title }

		assertEquals(2, books.size)
		assertTrue(BOOK_TITLE in titles)
		assertTrue(BOOK_TITLE_2 in titles)
	}

	@Test
	fun `updateBook updates existing book`() = runTest {
		bookRepository.insertBook(
			createBook(
				id = BOOK_ID,
				title = BOOK_TITLE,
				currentPage = 10,
				status = ReadingStatus.READING
			)
		)

		bookRepository.updateBook(
			createBook(
				id = BOOK_ID,
				title = BOOK_TITLE_2,
				currentPage = 300,
				status = ReadingStatus.FINISHED
			)
		)

		val savedBook = bookRepository.getBookById(BOOK_ID)

		assertEquals(BOOK_TITLE_2, savedBook?.title)
		assertEquals(300, savedBook?.currentPage)
		assertEquals(ReadingStatus.FINISHED, savedBook?.status)
	}

	@Test
	fun `deleteBook deletes existing book`() = runTest {
		bookRepository.insertBook(
			createBook(id = BOOK_ID)
		)

		bookRepository.deleteBook(BOOK_ID)

		val savedBook = bookRepository.getBookById(BOOK_ID)

		assertNull(savedBook)
	}
}