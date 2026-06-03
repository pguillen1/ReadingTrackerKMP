package com.pguillen.readingtracker.presentation.sessionedit

sealed interface AddEditReadingSessionUiEffect {
	data object NavigateBack : AddEditReadingSessionUiEffect
}