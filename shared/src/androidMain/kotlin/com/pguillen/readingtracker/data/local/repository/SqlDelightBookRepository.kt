package com.pguillen.readingtracker.data.local.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.pguillen.readingtracker.data.local.mapper.toDatabaseString
import com.pguillen.readingtracker.data.local.mapper.toEnumOrDefault
import com.pguillen.readingtracker.data.local.mapper.toLocalDateTimeFromDatabase
import com.pguillen.readingtracker.data.local.mapper.toNullableLocalDateFromDatabase
import com.pguillen.readingtracker.database.ReadingTrackerDatabase
import com.pguillen.readingtracker.domain.model.Book
import com.pguillen.readingtracker.domain.model.ReadingStatus
import com.pguillen.readingtracker.domain.repository.BookRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class SqlDelightBookRepository(
	database: ReadingTrackerDatabase
) : BookRepository {

	private val queries = database.readingTrackerQueries

	override fun observeBooks(): Flow<List<Book>> {
		return queries
			.selectAllBooks(::mapBook)
			.asFlow()
			.mapToList(Dispatchers.Default)
	}

	override fun observeBookById(bookId: String): Flow<Book?> {
		return queries
			.selectBookById(bookId, ::mapBook)
			.asFlow()
			.mapToOneOrNull(Dispatchers.Default)
	}

	override suspend fun getBookById(bookId: String): Book? {
		return queries
			.selectBookById(bookId, ::mapBook)
			.executeAsOneOrNull()
	}

	override suspend fun insertBook(book: Book) {
		queries.insertBook(
			id = book.id,
			title = book.title,
			author = book.author,
			total_pages = book.totalPages?.toLong(),
			current_page = book.currentPage.toLong(),
			status = book.status.name,
			started_at = book.startedAt?.toDatabaseString(),
			finished_at = book.finishedAt?.toDatabaseString(),
			added_at = book.addedAt.toDatabaseString(),
			updated_at = book.updatedAt.toDatabaseString()
		)
	}

	override suspend fun updateBook(book: Book) {
		queries.updateBook(
			title = book.title,
			author = book.author,
			total_pages = book.totalPages?.toLong(),
			current_page = book.currentPage.toLong(),
			status = book.status.name,
			started_at = book.startedAt?.toDatabaseString(),
			finished_at = book.finishedAt?.toDatabaseString(),
			updated_at = book.updatedAt.toDatabaseString(),
			id = book.id
		)
	}

	override suspend fun deleteBook(bookId: String) {
		queries.deleteBook(bookId)
	}

	private fun mapBook(
		id: String,
		title: String,
		author: String,
		total_pages: Long?,
		current_page: Long,
		status: String,
		started_at: String?,
		finished_at: String?,
		added_at: String,
		updated_at: String
	): Book {
		return Book(
			id = id,
			title = title,
			author = author,
			totalPages = total_pages?.toInt(),
			currentPage = current_page.toInt(),
			status = status.toEnumOrDefault(ReadingStatus.WANT_TO_READ),
			startedAt = started_at.toNullableLocalDateFromDatabase(),
			finishedAt = finished_at.toNullableLocalDateFromDatabase(),
			addedAt = added_at.toLocalDateTimeFromDatabase(),
			updatedAt = updated_at.toLocalDateTimeFromDatabase()
		)
	}
}