package com.pguillen.readingtracker.domain.usecase.session

import com.pguillen.readingtracker.core.date.DateTimeProvider
import com.pguillen.readingtracker.core.error.DomainException
import com.pguillen.readingtracker.domain.model.ReadingSession
import com.pguillen.readingtracker.domain.model.ReadingStatus
import com.pguillen.readingtracker.domain.repository.BookRepository
import com.pguillen.readingtracker.domain.repository.ReadingSessionRepository
import kotlinx.datetime.LocalDate

data class UpdateReadingSessionParams(
	val sessionId: String,
	val date: LocalDate,
	val startPage: Int?,
	val endPage: Int,
	val minutes: Int?,
	val note: String?
)

class UpdateReadingSessionUseCase(
	private val bookRepository: BookRepository,
	private val readingSessionRepository: ReadingSessionRepository,
	private val dateTimeProvider: DateTimeProvider
) {
	suspend operator fun invoke(params: UpdateReadingSessionParams): ReadingSession {
		val existingSession = readingSessionRepository.getSessionById(params.sessionId)
			?: throw DomainException.NotFound("Reading session not found")

		val book = bookRepository.getBookById(existingSession.bookId)
			?: throw DomainException.NotFound("Book not found")

		validateSession(
			startPage = params.startPage,
			endPage = params.endPage,
			minutes = params.minutes,
			totalPages = book.totalPages
		)

		val updatedSession = existingSession.copy(
			date = params.date,
			startPage = params.startPage,
			endPage = params.endPage,
			minutes = params.minutes,
			note = params.note?.trim()?.takeIf { it.isNotBlank() }
		)

		readingSessionRepository.updateSession(updatedSession)

		val newStatus = when {
			book.totalPages != null && params.endPage == book.totalPages -> ReadingStatus.FINISHED
			book.status == ReadingStatus.WANT_TO_READ -> ReadingStatus.READING
			else -> book.status
		}

		val updatedBook = book.copy(
			currentPage = maxOf(book.currentPage, params.endPage),
			status = newStatus,
			startedAt = book.startedAt ?: dateTimeProvider.today(),
			finishedAt = if (newStatus == ReadingStatus.FINISHED) {
				dateTimeProvider.today()
			}
			else {
				book.finishedAt
			},
			updatedAt = dateTimeProvider.now()
		)

		bookRepository.updateBook(updatedBook)

		return updatedSession
	}

	private fun validateSession(
		startPage: Int?,
		endPage: Int,
		minutes: Int?,
		totalPages: Int?
	) {
		if (endPage < 0) {
			throw DomainException.Validation("End page cannot be negative")
		}

		if (startPage != null && startPage < 0) {
			throw DomainException.Validation("Start page cannot be negative")
		}

		if (startPage != null && endPage < startPage) {
			throw DomainException.Validation("End page cannot be lower than start page")
		}

		if (minutes != null && minutes <= 0) {
			throw DomainException.Validation("Minutes must be greater than 0")
		}

		if (totalPages != null && endPage > totalPages) {
			throw DomainException.Validation("End page cannot be greater than total pages")
		}
	}
}