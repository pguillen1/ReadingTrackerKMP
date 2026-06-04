package com.pguillen.readingtracker.presentation.bookedit

sealed interface AddEditBookMode {
	data object Add : AddEditBookMode
	data class Edit(val bookId: String) : AddEditBookMode
}