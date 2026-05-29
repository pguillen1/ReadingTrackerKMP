package com.pguillen.readingtracker.presentation.library

import com.pguillen.readingtracker.domain.model.Book

data class LibraryUiState(
	val books: List<Book> = emptyList(),
	val searchQuery: String = "",
	val selectedFilter: LibraryFilter = LibraryFilter.ALL,
	val isLoading: Boolean = true
) {
	val isEmpty: Boolean
		get() = !isLoading && books.isEmpty()

	val isSearchActive: Boolean
		get() = searchQuery.isNotBlank()
}