package com.pguillen.readingtracker.domain.repository

import com.pguillen.readingtracker.domain.model.Book
import kotlinx.coroutines.flow.Flow

interface BookRepository {

	fun observeBooks(): Flow<List<Book>>

	fun observeBookById(bookId: String): Flow<Book?>

	suspend fun getBookById(bookId: String): Book?

	suspend fun insertBook(book: Book)

	suspend fun updateBook(book: Book)

	suspend fun deleteBook(bookId: String)
}