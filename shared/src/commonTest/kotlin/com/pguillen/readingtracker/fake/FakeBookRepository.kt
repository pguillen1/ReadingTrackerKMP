package com.pguillen.readingtracker.fake

import com.pguillen.readingtracker.domain.model.Book
import com.pguillen.readingtracker.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeBookRepository : BookRepository {

	val books = MutableStateFlow(emptyList<Book>())

	override fun observeBooks(): Flow<List<Book>> {
		return books
	}

	override fun observeBookById(bookId: String): Flow<Book?> {
		return books.map { list ->
			list.firstOrNull { it.id == bookId }
		}
	}

	override suspend fun getBookById(bookId: String): Book? {
		return books.value.firstOrNull { it.id == bookId }
	}

	override suspend fun insertBook(book: Book) {
		books.value += book
	}

	override suspend fun updateBook(book: Book) {
		books.value = books.value.map {
			if (it.id == book.id) book else it
		}
	}

	override suspend fun deleteBook(bookId: String) {
		books.value = books.value.filterNot { it.id == bookId }
	}
}