package com.pguillen.readingtracker.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

data class ReadingSession(
    val id: String,
    val bookId: String,
    val date: LocalDate,
    val startPage: Int?,
    val endPage: Int,
    val minutes: Int?,
    val note: String?,
    val createdAt: LocalDateTime
) {
    val pagesRead: Int?
        get() {
            val start = startPage ?: return null
            return (endPage - start).coerceAtLeast(0)
        }
}