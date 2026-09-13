package com.pguillen.readingtracker

import com.pguillen.readingtracker.domain.model.Book
import com.pguillen.readingtracker.domain.model.BookNote
import com.pguillen.readingtracker.domain.model.BookNoteType
import com.pguillen.readingtracker.domain.model.ReadingSession
import com.pguillen.readingtracker.domain.model.ReadingStatus
import com.pguillen.readingtracker.domain.repository.BookRepository
import com.pguillen.readingtracker.domain.repository.ReadingSessionRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

class DebugSeeder(
	private val bookRepository: BookRepository,
	private val sessionRepository: ReadingSessionRepository
) {
	suspend fun empty() {
		reset()
	}

	suspend fun seedSingleBook() {
		reset()

		bookRepository.insertBook(
			createBook(
				id = "Maestro Book ID"
			)
		)
	}

	suspend fun seedBookWithSession() {
		reset()

		val book = createBook(
			id = "Maestro Book ID"
		)
		bookRepository.insertBook(book)

		sessionRepository.insertSession(
			createSession(
				id = "Maestro Session ID",
				bookId = book.id,
			)
		)
	}

	suspend fun seedMultipleBooks() {
		reset()

		bookRepository.insertBook(
			createBook(
				id = "Maestro Book ID 1",
				title = "Maestro Title 1",
				status = ReadingStatus.WANT_TO_READ
			)
		)
		bookRepository.insertBook(
			createBook(
				id = "Maestro Book ID 2",
				title = "Maestro Title 2",
				status = ReadingStatus.READING
			)
		)
		bookRepository.insertBook(
			createBook(
				id = "Maestro Book ID 3",
				title = "Maestro Title 3",
				status = ReadingStatus.FINISHED
			)
		)
	}

	private suspend fun reset() {
		val books = bookRepository
			.observeBooks()
			.first()

		books.forEach { book ->
			bookRepository.deleteBook(book.id)
		}
	}

	private fun createBook(
		id: String,
		title: String = "Maestro Book",
		author: String = "Maestro Author",
		totalPages: Int = 300,
		currentPage: Int = 0,
		status: ReadingStatus = ReadingStatus.WANT_TO_READ,
		startedAt: LocalDate = LocalDate(2026, 1, 1),
		finishedAt: LocalDate = LocalDate(2026, 1, 1),
		addedAt: LocalDateTime = LocalDateTime(2026, 1, 1, 10, 0),
		updatedAt: LocalDateTime = LocalDateTime(2026, 1, 1, 10, 0)
	): Book {
		return Book(
			id = id,
			title = title,
			author = author,
			totalPages = totalPages,
			currentPage = currentPage,
			status = status,
			coverFileName = null,
			startedAt = startedAt,
			finishedAt = finishedAt,
			addedAt = addedAt,
			updatedAt = updatedAt,
		)
	}
	
	private fun createSession(
		id: String,
		bookId: String,
		date: LocalDate = LocalDate(2026, 1, 1),
		startPage: Int = 0,
		endPage: Int = 20,
		minutes: Int = 0,
		note: String = "Maestro Session Note",
		createdAt: LocalDateTime = LocalDateTime(2026, 1, 1, 10, 0)
	): ReadingSession {
		return ReadingSession(
			id = id,
			bookId = bookId,
			date = date,
			startPage = startPage,
			endPage = endPage,
			minutes = minutes,
			note = note,
			createdAt = createdAt
		)
	}

	private fun createNote(
		id: String,
		bookId: String,
		type: BookNoteType = BookNoteType.NOTE,
		content: String = "Maestro Book Note Content",
		page: Int = 10,
		createdAt: LocalDateTime = LocalDateTime(2026, 1, 1, 10, 0),
		updatedAt: LocalDateTime = LocalDateTime(2026, 1, 1, 10, 0)
	): BookNote {
		return BookNote(
			id = id,
			bookId = bookId,
			type = type,
			content = content,
			page = page,
			createdAt = createdAt,
			updatedAt = updatedAt
		)
	}
}