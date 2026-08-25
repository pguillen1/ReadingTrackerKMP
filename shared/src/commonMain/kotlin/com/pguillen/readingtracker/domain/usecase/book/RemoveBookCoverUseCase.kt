package com.pguillen.readingtracker.domain.usecase.book

import com.pguillen.readingtracker.domain.repository.BookRepository

class RemoveBookCoverUseCase(
	private val bookRepository: BookRepository
) {
	suspend operator fun invoke(
		bookId: String
	) {
		try {
			bookRepository.updateBookCover(bookId, null)
		}
		catch (_: Exception) {

		}
	}
}