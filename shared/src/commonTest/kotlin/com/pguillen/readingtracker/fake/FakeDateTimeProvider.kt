package com.pguillen.readingtracker.fake

import com.pguillen.readingtracker.core.date.DateTimeProvider
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

class FakeDateTimeProvider(
	private val today: LocalDate = LocalDate(2026, 1, 15),
	private val now: LocalDateTime = LocalDateTime(2026, 1, 15, 10, 30)
) : DateTimeProvider {

	override fun today(): LocalDate {
		return today
	}

	override fun now(): LocalDateTime {
		return now
	}
}