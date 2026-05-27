package com.pguillen.readingtracker.data.repository

import com.pguillen.readingtracker.domain.model.Book
import com.pguillen.readingtracker.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeBookRepository(
	initialBooks: List<Book> = emptyList()
) : BookRepository {

	private val booksFlow = MutableStateFlow(initialBooks)

	override fun observeBooks(): Flow<List<Book>> {
		return booksFlow
	}

	override fun observeBookById(bookId: String): Flow<Book?> {
		return booksFlow.map { books ->
			books.firstOrNull { it.id == bookId }
		}
	}

	override suspend fun getBookById(bookId: String): Book? {
		return booksFlow.value.firstOrNull { it.id == bookId }
	}

	override suspend fun insertBook(book: Book) {
		booksFlow.value += book
	}

	override suspend fun updateBook(book: Book) {
		booksFlow.value = booksFlow.value.map { currentBook ->
			if (currentBook.id == book.id) book else currentBook
		}
	}

	override suspend fun deleteBook(bookId: String) {
		booksFlow.value = booksFlow.value.filterNot { book ->
			book.id == bookId
		}
	}
}