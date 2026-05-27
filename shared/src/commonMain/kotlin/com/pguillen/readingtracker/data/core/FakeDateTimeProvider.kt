package com.pguillen.readingtracker.data.core

import com.pguillen.readingtracker.core.date.DateTimeProvider
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

class FakeDateTimeProvider(
	private val fixedToday: LocalDate = LocalDate(2026, 1, 1),
	private val fixedNow: LocalDateTime = LocalDateTime(2026, 1, 1, 12, 0)
) : DateTimeProvider {

	override fun today(): LocalDate {
		return fixedToday
	}

	override fun now(): LocalDateTime {
		return fixedNow
	}
}