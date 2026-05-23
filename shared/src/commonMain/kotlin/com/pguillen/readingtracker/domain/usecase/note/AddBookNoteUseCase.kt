package com.pguillen.readingtracker.domain.usecase.note

import com.pguillen.readingtracker.core.date.DateTimeProvider
import com.pguillen.readingtracker.core.error.DomainException
import com.pguillen.readingtracker.core.id.IdGenerator
import com.pguillen.readingtracker.domain.model.BookNote
import com.pguillen.readingtracker.domain.model.BookNoteType
import com.pguillen.readingtracker.domain.repository.BookNoteRepository
import com.pguillen.readingtracker.domain.repository.BookRepository

data class AddBookNoteParams(
	val bookId: String,
	val type: BookNoteType,
	val content: String,
	val page: Int?
)

class AddBookNoteUseCase(
	private val bookRepository: BookRepository,
	private val bookNoteRepository: BookNoteRepository,
	private val idGenerator: IdGenerator,
	private val dateTimeProvider: DateTimeProvider
) {
	suspend operator fun invoke(params: AddBookNoteParams): BookNote {
		val book = bookRepository.getBookById(params.bookId)
			?: throw DomainException.NotFound("Book not found")

		validateNote(
			content = params.content,
			page = params.page,
			totalPages = book.totalPages
		)

		val now = dateTimeProvider.now()

		val note = BookNote(
			id = idGenerator.generateId(),
			bookId = params.bookId,
			type = params.type,
			content = params.content.trim(),
			page = params.page,
			createdAt = now,
			updatedAt = now
		)

		bookNoteRepository.insertNote(note)

		return note
	}

	private fun validateNote(
		content: String,
		page: Int?,
		totalPages: Int?
	) {
		if (content.isBlank()) {
			throw DomainException.Validation("Note content cannot be empty")
		}

		if (page != null && page < 0) {
			throw DomainException.Validation("Page cannot be negative")
		}

		if (page != null && totalPages != null && page > totalPages) {
			throw DomainException.Validation("Page cannot be greater than total pages")
		}
	}
}