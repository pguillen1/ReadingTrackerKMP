package com.pguillen.readingtracker.presentation.stats

data class StatsUiState(
	val totalBooks: Int = 0,
	val readingBooks: Int = 0,
	val finishedBooks: Int = 0,
	val totalSessions: Int = 0,
	val totalPagesRead: Int = 0,
	val totalMinutesRead: Int = 0,
	val recentDailySummaries: List<DailyReadingSummary> = emptyList(),
	val isLoading: Boolean = true
)