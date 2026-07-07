package com.pguillen.readingtracker.data.core

import com.pguillen.readingtracker.core.date.DateTimeProvider
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import kotlin.time.Clock

class SystemDateTimeProvider(
	private val timeZone: TimeZone = TimeZone.currentSystemDefault()
) : DateTimeProvider {

	override fun now(): LocalDateTime {
		return Clock.System.now().toLocalDateTime(timeZone)
	}

	override fun today(): LocalDate {
		return Clock.System.todayIn(timeZone)
	}
}