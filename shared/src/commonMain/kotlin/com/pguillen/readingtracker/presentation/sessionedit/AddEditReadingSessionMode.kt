package com.pguillen.readingtracker.presentation.sessionedit

sealed interface AddEditReadingSessionMode {
	data class Add(val bookId: String) : AddEditReadingSessionMode
	data class Edit(val sessionId: String) : AddEditReadingSessionMode
}