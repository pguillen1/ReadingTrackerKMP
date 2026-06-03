package com.pguillen.readingtracker.presentation.logsession

sealed interface LogReadingSessionUiEffect {
	data object NavigateBack : LogReadingSessionUiEffect
}