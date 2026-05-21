package com.pguillen.readingtracker.domain.model

import kotlinx.datetime.LocalDateTime

data class BookNote(
    val id: String,
    val bookId: String,
    val type: BookNoteType,
    val content: String,
    val page: Int?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)