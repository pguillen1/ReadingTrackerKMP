package com.pguillen.readingtracker.fake

import com.pguillen.readingtracker.domain.model.Book
import com.pguillen.readingtracker.domain.model.BookNote
import com.pguillen.readingtracker.domain.model.BookNoteType
import com.pguillen.readingtracker.domain.model.ReadingSession
import com.pguillen.readingtracker.domain.model.ReadingStatus
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

fun createBook(
	id: String = BOOK_ID,
	title: String = BOOK_TITLE,
	author: String = BOOK_AUTHOR,
	totalPages: Int = BOOK_TOTAL_PAGES,
	currentPage: Int = BOOK_CURRENT_PAGE,
	status: ReadingStatus = ReadingStatus.WANT_TO_READ,
	startedAt: LocalDate = LocalDate(2026, 1, 1),
	finishedAt: LocalDate? = null,
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
		startedAt = startedAt,
		finishedAt = finishedAt,
		addedAt = addedAt,
		updatedAt = updatedAt
	)
}

fun createSession(
	id: String = SESSION_ID,
	bookId: String = BOOK_ID,
	date: LocalDate = LocalDate(2026, 1, 1),
	startPage: Int = SESSION_START_PAGE,
	endPage: Int = SESSION_END_PAGE,
	minutes: Int? = SESSION_MINUTES,
	note: String? = SESSION_NOTE,
	createdAt: LocalDateTime = LocalDateTime(2026, 1, 1, 10, 0),
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

fun createNote(
	id: String = NOTE_ID,
	bookId: String = BOOK_ID,
	type: BookNoteType = BookNoteType.NOTE,
	content: String = "Original note",
	page: Int? = null,
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