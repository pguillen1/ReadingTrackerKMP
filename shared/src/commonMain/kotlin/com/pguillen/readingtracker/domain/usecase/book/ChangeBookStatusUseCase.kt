package com.pguillen.readingtracker.domain.usecase.book

import com.pguillen.readingtracker.core.date.DateTimeProvider
import com.pguillen.readingtracker.core.error.DomainException
import com.pguillen.readingtracker.domain.model.Book
import com.pguillen.readingtracker.domain.model.ReadingStatus
import com.pguillen.readingtracker.domain.repository.BookRepository

class ChangeBookStatusUseCase(
	private val bookRepository: BookRepository,
	private val dateTimeProvider: DateTimeProvider
) {
	suspend operator fun invoke(
		bookId: String,
		newStatus: ReadingStatus
	): Book {
		val book = bookRepository.getBookById(bookId)
			?: throw DomainException.NotFound("Book not found")

		val today = dateTimeProvider.today()

		val updatedBook = book.copy(
			status = newStatus,
			currentPage = when {
				newStatus == ReadingStatus.FINISHED && book.totalPages != null -> book.totalPages
				else -> book.currentPage
			},
			startedAt = when (newStatus) {
				ReadingStatus.WANT_TO_READ -> null
				ReadingStatus.READING,
				ReadingStatus.FINISHED -> book.startedAt ?: today
			},
			finishedAt = when (newStatus) {
				ReadingStatus.FINISHED -> today
				ReadingStatus.WANT_TO_READ,
				ReadingStatus.READING -> null
			},
			updatedAt = dateTimeProvider.now()
		)

		bookRepository.updateBook(updatedBook)

		return updatedBook
	}
}