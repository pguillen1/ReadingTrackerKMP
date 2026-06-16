package com.pguillen.readingtracker.domain.usecase.book

import com.pguillen.readingtracker.fake.BOOK_ID
import com.pguillen.readingtracker.fake.FakeBookRepository
import com.pguillen.readingtracker.fake.createBook
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNull

class DeleteBookUseCaseTest {

	private lateinit var fakeBookRepository: FakeBookRepository
	private lateinit var deleteBookUseCase: DeleteBookUseCase

	@BeforeTest
	fun setUp() {
		fakeBookRepository = FakeBookRepository()

		deleteBookUseCase = DeleteBookUseCase(
			bookRepository = fakeBookRepository
		)
	}

	@Test
	fun `invoke deletes existing book`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(id = BOOK_ID)
		)

		deleteBookUseCase(BOOK_ID)

		val book = fakeBookRepository.getBookById(BOOK_ID)

		assertNull(book)
	}
}