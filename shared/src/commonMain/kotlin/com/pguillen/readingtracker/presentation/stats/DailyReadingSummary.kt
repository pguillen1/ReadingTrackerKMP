package com.pguillen.readingtracker.presentation.stats

import kotlinx.datetime.LocalDate

data class DailyReadingSummary(
	val date: LocalDate,
	val pagesRead: Int,
	val minutesRead: Int,
	val sessionsCount: Int
)