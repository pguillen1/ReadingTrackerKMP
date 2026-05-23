package com.pguillen.readingtracker.domain.usecase.book

import com.pguillen.readingtracker.domain.model.Book
import com.pguillen.readingtracker.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow

class ObserveBookByIdUseCase(
	private val bookRepository: BookRepository
) {
	operator fun invoke(bookId: String): Flow<Book?> {
		return bookRepository.observeBookById(bookId)
	}
}