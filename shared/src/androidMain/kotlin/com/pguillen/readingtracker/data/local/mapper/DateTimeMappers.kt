package com.pguillen.readingtracker.data.local.mapper

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

fun LocalDate.toDatabaseString(): String {
	return toString()
}

fun LocalDateTime.toDatabaseString(): String {
	return toString()
}

fun String.toLocalDateFromDatabase(): LocalDate {
	return LocalDate.parse(this)
}

fun String.toLocalDateTimeFromDatabase(): LocalDateTime {
	return LocalDateTime.parse(this)
}

fun String?.toNullableLocalDateFromDatabase(): LocalDate? {
	return this?.let { LocalDate.parse(it) }
}

fun String?.toNullableLocalDateTimeFromDatabase(): LocalDateTime? {
	return this?.let { LocalDateTime.parse(it) }
}