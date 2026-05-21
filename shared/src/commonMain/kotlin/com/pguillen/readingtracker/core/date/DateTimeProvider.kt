package com.pguillen.readingtracker.core.date

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

interface DateTimeProvider {
	fun today(): LocalDate
	fun now(): LocalDateTime
}