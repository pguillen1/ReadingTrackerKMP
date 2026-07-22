package com.pguillen.readingtracker.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val totalPages: Int,
    val currentPage: Int,
    val status: ReadingStatus,
    val startedAt: LocalDate?,
    val finishedAt: LocalDate?,
    val addedAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    val progressPercentage: Float
        get() {
            if (totalPages <= 0) return 0f
            return (currentPage.toFloat() / totalPages.toFloat()).coerceIn(0f, 1f)
        }

    val isFinished: Boolean
        get() = status == ReadingStatus.FINISHED
}