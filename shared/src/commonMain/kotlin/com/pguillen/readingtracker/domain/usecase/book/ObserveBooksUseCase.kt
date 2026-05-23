package com.pguillen.readingtracker.domain.usecase.book

import com.pguillen.readingtracker.domain.model.Book
import com.pguillen.readingtracker.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow

class ObserveBooksUseCase(
	private val bookRepository: BookRepository
) {
	operator fun invoke(): Flow<List<Book>> {
		return bookRepository.observeBooks()
	}
}