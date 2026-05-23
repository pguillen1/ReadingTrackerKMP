package com.pguillen.readingtracker.domain.model

data class ReadingStats(
	val totalBooks: Int,
	val readingBooks: Int,
	val finishedBooks: Int,
	val totalSessions: Int,
	val totalPagesRead: Int,
	val totalMinutesRead: Int
)
