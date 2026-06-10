package com.pguillen.readingtracker.presentation.bookdetail

sealed interface BookDetailUiEffect {
	data object NavigateBack : BookDetailUiEffect
}