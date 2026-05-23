package com.pguillen.readingtracker.domain.usecase.note

import com.pguillen.readingtracker.domain.model.BookNote
import com.pguillen.readingtracker.domain.repository.BookNoteRepository
import kotlinx.coroutines.flow.Flow

class ObserveBookNotesUseCase(
	private val bookNoteRepository: BookNoteRepository
) {
	operator fun invoke(bookId: String): Flow<List<BookNote>> {
		return bookNoteRepository.observeNotesByBookId(bookId)
	}
}