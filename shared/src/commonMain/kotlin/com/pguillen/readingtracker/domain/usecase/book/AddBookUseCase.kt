package com.pguillen.readingtracker.domain.usecase.book

import com.pguillen.readingtracker.core.date.DateTimeProvider
import com.pguillen.readingtracker.core.error.DomainException
import com.pguillen.readingtracker.core.id.IdGenerator
import com.pguillen.readingtracker.domain.model.Book
import com.pguillen.readingtracker.domain.model.ReadingStatus
import com.pguillen.readingtracker.domain.repository.BookRepository

data class AddBookParams(
	val title: String,
	val author: String,
	val totalPages: Int?,
	val currentPage: Int,
	val status: ReadingStatus
)

class AddBookUseCase(
	private val bookRepository: BookRepository,
	private val idGenerator: IdGenerator,
	private val dateTimeProvider: DateTimeProvider
) {
	suspend operator fun invoke(params: AddBookParams): Book {
		validateBookInput(
			title = params.title,
			totalPages = params.totalPages,
			currentPage = params.currentPage
		)

		val now = dateTimeProvider.now()
		val today = dateTimeProvider.today()

		val currentPage = when {
			params.status == ReadingStatus.FINISHED && params.totalPages != null -> params.totalPages
			else -> params.currentPage
		}

		val book = Book(
			id = idGenerator.generateId(),
			title = params.title.trim(),
			author = params.author.trim(),
			totalPages = params.totalPages,
			currentPage = currentPage,
			status = params.status,
			startedAt = when (params.status) {
				ReadingStatus.READING,
				ReadingStatus.FINISHED -> today
				ReadingStatus.WANT_TO_READ -> null
			},
			finishedAt = when (params.status) {
				ReadingStatus.FINISHED -> today
				else -> null
			},
			addedAt = now,
			updatedAt = now
		)

		bookRepository.insertBook(book)

		return book
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