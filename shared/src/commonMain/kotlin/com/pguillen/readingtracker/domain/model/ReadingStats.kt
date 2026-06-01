package com.pguillen.readingtracker.domain.model

import com.pguillen.readingtracker.presentation.stats.DailyReadingSummary

data class ReadingStats(
	val totalBooks: Int,
	val readingBooks: Int,
	val finishedBooks: Int,
	val totalSessions: Int,
	val totalPagesRead: Int,
	val totalMinutesRead: Int,
	val recentDailySummaries: List<DailyReadingSummary>
)
