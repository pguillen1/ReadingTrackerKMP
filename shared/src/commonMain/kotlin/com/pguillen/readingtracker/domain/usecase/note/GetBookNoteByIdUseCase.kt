package com.pguillen.readingtracker.domain.usecase.note

import com.pguillen.readingtracker.domain.model.BookNote
import com.pguillen.readingtracker.domain.repository.BookNoteRepository

class GetBookNoteByIdUseCase(
	private val bookNoteRepository: BookNoteRepository
) {
	suspend operator fun invoke(noteId: String): BookNote? {
		return bookNoteRepository.getNoteById(noteId)
	}
}