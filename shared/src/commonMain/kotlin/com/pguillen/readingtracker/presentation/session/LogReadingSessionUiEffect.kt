package com.pguillen.readingtracker.presentation.session

sealed interface LogReadingSessionUiEffect {
	data object NavigateBack : LogReadingSessionUiEffect
}