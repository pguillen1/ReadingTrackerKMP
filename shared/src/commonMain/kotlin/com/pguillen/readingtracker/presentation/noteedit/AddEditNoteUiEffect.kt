package com.pguillen.readingtracker.presentation.noteedit

sealed interface AddEditNoteUiEffect {
	data object NavigateBack : AddEditNoteUiEffect
}