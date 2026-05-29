package com.pguillen.readingtracker.presentation.library

import com.pguillen.readingtracker.domain.model.ReadingStatus

enum class LibraryFilter {
	ALL,
	WANT_TO_READ,
	READING,
	FINISHED
}

fun LibraryFilter.toReadingStatusOrNull(): ReadingStatus? {
	return when (this) {
		LibraryFilter.ALL -> null
		LibraryFilter.WANT_TO_READ -> ReadingStatus.WANT_TO_READ
		LibraryFilter.READING -> ReadingStatus.READING
		LibraryFilter.FINISHED -> ReadingStatus.FINISHED
	}
}