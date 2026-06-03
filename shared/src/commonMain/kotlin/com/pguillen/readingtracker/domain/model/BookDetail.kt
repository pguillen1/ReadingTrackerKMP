package com.pguillen.readingtracker.domain.model

data class BookDetail(
	val book: Book,
	val recentSessions: List<ReadingSession>,
	val recentNotes: List<BookNote>
)