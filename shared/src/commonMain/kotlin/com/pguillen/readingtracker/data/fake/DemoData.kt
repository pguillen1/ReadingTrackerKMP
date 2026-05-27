package com.pguillen.readingtracker.data.fake

import com.pguillen.readingtracker.domain.model.Book
import com.pguillen.readingtracker.domain.model.BookNote
import com.pguillen.readingtracker.domain.model.BookNoteType
import com.pguillen.readingtracker.domain.model.ReadingSession
import com.pguillen.readingtracker.domain.model.ReadingStatus
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

object DemoData {

	val books = listOf(
		Book(
			id = "book-1",
			title = "The Hobbit",
			author = "J.R.R. Tolkien",
			totalPages = 310,
			currentPage = 145,
			status = ReadingStatus.READING,
			startedAt = LocalDate(2024, 4, 12),
			finishedAt = null,
			addedAt = LocalDateTime(2024, 4, 10, 10, 0),
			updatedAt = LocalDateTime(2024, 5, 18, 18, 30)
		),
		Book(
			id = "book-2",
			title = "Atomic Habits",
			author = "James Clear",
			totalPages = 320,
			currentPage = 32,
			status = ReadingStatus.READING,
			startedAt = LocalDate(2024, 5, 1),
			finishedAt = null,
			addedAt = LocalDateTime(2024, 4, 29, 9, 0),
			updatedAt = LocalDateTime(2024, 5, 5, 21, 0)
		),
		Book(
			id = "book-3",
			title = "Project Hail Mary",
			author = "Andy Weir",
			totalPages = 496,
			currentPage = 496,
			status = ReadingStatus.FINISHED,
			startedAt = LocalDate(2024, 3, 1),
			finishedAt = LocalDate(2024, 3, 24),
			addedAt = LocalDateTime(2024, 2, 20, 12, 0),
			updatedAt = LocalDateTime(2024, 3, 24, 20, 0)
		),
		Book(
			id = "book-4",
			title = "Dune",
			author = "Frank Herbert",
			totalPages = 688,
			currentPage = 0,
			status = ReadingStatus.WANT_TO_READ,
			startedAt = null,
			finishedAt = null,
			addedAt = LocalDateTime(2024, 5, 8, 16, 0),
			updatedAt = LocalDateTime(2024, 5, 8, 16, 0)
		),
		Book(
			id = "book-5",
			title = "The Midnight Library",
			author = "Matt Haig",
			totalPages = 304,
			currentPage = 0,
			status = ReadingStatus.WANT_TO_READ,
			startedAt = null,
			finishedAt = null,
			addedAt = LocalDateTime(2024, 5, 12, 11, 0),
			updatedAt = LocalDateTime(2024, 5, 12, 11, 0)
		)
	)

	val sessions = listOf(
		ReadingSession(
			id = "session-1",
			bookId = "book-1",
			date = LocalDate(2024, 5, 18),
			startPage = 131,
			endPage = 145,
			minutes = 30,
			note = null,
			createdAt = LocalDateTime(2024, 5, 18, 18, 30)
		),
		ReadingSession(
			id = "session-2",
			bookId = "book-1",
			date = LocalDate(2024, 5, 15),
			startPage = 106,
			endPage = 130,
			minutes = 25,
			note = null,
			createdAt = LocalDateTime(2024, 5, 15, 21, 0)
		),
		ReadingSession(
			id = "session-3",
			bookId = "book-1",
			date = LocalDate(2024, 5, 12),
			startPage = 88,
			endPage = 105,
			minutes = 20,
			note = null,
			createdAt = LocalDateTime(2024, 5, 12, 20, 0)
		)
	)

	val notes = listOf(
		BookNote(
			id = "note-1",
			bookId = "book-1",
			type = BookNoteType.QUOTE,
			content = "It's a dangerous business, Frodo, going out your door.",
			page = 72,
			createdAt = LocalDateTime(2024, 5, 18, 18, 40),
			updatedAt = LocalDateTime(2024, 5, 18, 18, 40)
		)
	)
}