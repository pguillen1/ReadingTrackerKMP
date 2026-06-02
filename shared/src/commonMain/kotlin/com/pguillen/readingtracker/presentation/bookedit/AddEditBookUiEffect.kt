package com.pguillen.readingtracker.presentation.bookedit

sealed interface AddEditBookUiEffect {
	data object NavigateBack : AddEditBookUiEffect
}