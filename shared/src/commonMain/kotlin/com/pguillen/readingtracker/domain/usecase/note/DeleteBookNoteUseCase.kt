package com.pguillen.readingtracker.domain.usecase.note

import com.pguillen.readingtracker.domain.repository.BookNoteRepository

class DeleteBookNoteUseCase(
	private val bookNoteRepository: BookNoteRepository
) {
	suspend operator fun invoke(noteId: String) {
		bookNoteRepository.deleteNote(noteId)
	}
}