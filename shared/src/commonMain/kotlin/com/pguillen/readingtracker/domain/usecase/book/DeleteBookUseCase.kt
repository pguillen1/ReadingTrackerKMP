package com.pguillen.readingtracker.domain.usecase.book

import com.pguillen.readingtracker.domain.repository.BookRepository

class DeleteBookUseCase(
	private val bookRepository: BookRepository
) {
	suspend operator fun invoke(bookId: String) {
		bookRepository.deleteBook(bookId)
	}
}