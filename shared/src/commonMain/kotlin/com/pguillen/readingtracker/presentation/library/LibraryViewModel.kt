package com.pguillen.readingtracker.presentation.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pguillen.readingtracker.domain.model.Book
import com.pguillen.readingtracker.domain.usecase.book.ObserveBooksUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class LibraryViewModel(
	observeBooksUseCase: ObserveBooksUseCase
) : ViewModel() {

	private val searchQuery = MutableStateFlow("")
	private val selectedFilter = MutableStateFlow(LibraryFilter.ALL)

	val uiState: StateFlow<LibraryUiState> =
		combine(
			observeBooksUseCase(),
			searchQuery,
			selectedFilter
		) { books, query, filter ->
			LibraryUiState(
				books = books
					.filterByStatus(filter)
					.filterByQuery(query)
					.sortedByDescending { it.updatedAt },
				searchQuery = query,
				selectedFilter = filter,
				isLoading = false
			)
		}.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = LibraryUiState()
		)

	fun onSearchQueryChanged(query: String) {
		searchQuery.value = query
	}

	fun onFilterSelected(filter: LibraryFilter) {
		selectedFilter.value = if (selectedFilter.value == filter) {
			LibraryFilter.ALL
		} else {
			filter
		}
	}

	private fun List<Book>.filterByStatus(filter: LibraryFilter): List<Book> {
		val status = filter.toReadingStatusOrNull() ?: return this
		return filter { book -> book.status == status }
	}

	private fun List<Book>.filterByQuery(query: String): List<Book> {
		val normalizedQuery = query.trim().lowercase()

		if (normalizedQuery.isBlank()) return this

		return filter { book ->
			book.title.lowercase().contains(normalizedQuery) ||
					book.author.lowercase().contains(normalizedQuery)
		}
	}
}