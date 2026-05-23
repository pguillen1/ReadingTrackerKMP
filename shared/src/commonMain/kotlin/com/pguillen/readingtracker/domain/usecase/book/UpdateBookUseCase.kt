package com.pguillen.readingtracker.domain.usecase.book

import com.pguillen.readingtracker.core.date.DateTimeProvider
import com.pguillen.readingtracker.core.error.DomainException
import com.pguillen.readingtracker.domain.model.Book
import com.pguillen.readingtracker.domain.model.ReadingStatus
import com.pguillen.readingtracker.domain.repository.BookRepository
import kotlinx.datetime.LocalDate

data class UpdateBookParams(
	val bookId: String,
	val title: String,
	val author: String,
	val totalPages: Int?,
	val currentPage: Int,
	val status: ReadingStatus
)

class UpdateBookUseCase(
	private val bookRepository: BookRepository,
	private val dateTimeProvider: DateTimeProvider
) {
	suspend operator fun invoke(params: UpdateBookParams): Book {
		val existingBook = bookRepository.getBookById(params.bookId)
			?: throw DomainException.NotFound("Book not found")

		validateBookInput(
			title = params.title,
			totalPages = params.totalPages,
			currentPage = params.currentPage
		)

		val resolvedCurrentPage = when {
			params.status == ReadingStatus.FINISHED && params.totalPages != null -> params.totalPages
			else -> params.currentPage
		}

		val updatedBook = existingBook.copy(
			title = params.title.trim(),
			author = params.author.trim(),
			totalPages = params.totalPages,
			currentPage = resolvedCurrentPage,
			status = params.status,
			startedAt = resolveStartedAt(
				previous = existingBook.startedAt,
				newStatus = params.status
			),
			finishedAt = resolveFinishedAt(
				newStatus = params.status
			),
			updatedAt = dateTimeProvider.now()
		)

		bookRepository.updateBook(updatedBook)

		return updatedBook
	}

	private fun resolveStartedAt(
		previous: LocalDate?,
		newStatus: ReadingStatus
	): LocalDate? {
		return when (newStatus) {
			ReadingStatus.WANT_TO_READ -> null
			ReadingStatus.READING,
			ReadingStatus.FINISHED -> previous ?: dateTimeProvider.today()
		}
	}

	private fun resolveFinishedAt(
		newStatus: ReadingStatus
	): LocalDate? {
		return when (newStatus) {
			ReadingStatus.FINISHED -> dateTimeProvider.today()
			ReadingStatus.WANT_TO_READ,
			ReadingStatus.READING -> null
		}
	}

	private fun validateBookInput(
		title: String,
		totalPages: Int?,
		currentPage: Int
	) {
		if (title.isBlank()) {
			throw DomainException.Validation("Book title cannot be empty")
		}

		if (totalPages != null && totalPages <= 0) {
			throw DomainException.Validation("Total pages must be greater than 0")
		}

		if (currentPage < 0) {
			throw DomainException.Validation("Current page cannot be negative")
		}

		if (totalPages != null && currentPage > totalPages) {
			throw DomainException.Validation("Current page cannot be greater than total pages")
		}
	}
}