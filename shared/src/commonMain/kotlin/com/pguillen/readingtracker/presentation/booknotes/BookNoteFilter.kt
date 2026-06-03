package com.pguillen.readingtracker.presentation.booknotes

import com.pguillen.readingtracker.domain.model.BookNoteType

enum class BookNoteFilter {
	ALL,
	NOTES,
	QUOTES
}

fun BookNoteFilter.toBookNoteTypeOrNull(): BookNoteType? {
	return when (this) {
		BookNoteFilter.ALL -> null
		BookNoteFilter.NOTES -> BookNoteType.NOTE
		BookNoteFilter.QUOTES -> BookNoteType.QUOTE
	}
}