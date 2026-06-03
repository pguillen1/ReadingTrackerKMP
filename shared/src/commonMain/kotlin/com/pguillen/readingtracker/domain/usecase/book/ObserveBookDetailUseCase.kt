package com.pguillen.readingtracker.domain.usecase.book

import com.pguillen.readingtracker.domain.model.BookDetail
import com.pguillen.readingtracker.domain.repository.BookNoteRepository
import com.pguillen.readingtracker.domain.repository.BookRepository
import com.pguillen.readingtracker.domain.repository.ReadingSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ObserveBookDetailUseCase(
	private val bookRepository: BookRepository,
	private val readingSessionRepository: ReadingSessionRepository,
	private val bookNoteRepository: BookNoteRepository
) {
	operator fun invoke(bookId: String): Flow<BookDetail?> {
		return combine(
			bookRepository.observeBookById(bookId),
			readingSessionRepository.observeSessionsByBookId(bookId),
			bookNoteRepository.observeNotesByBookId(bookId)
		) { book, sessions, notes ->
			book?.let {
				BookDetail(
					book = it,
					recentSessions = sessions.take(3),
					recentNotes = notes.take(3)
				)
			}
		}
	}
}