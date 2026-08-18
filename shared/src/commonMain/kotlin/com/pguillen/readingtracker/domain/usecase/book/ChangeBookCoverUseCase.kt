package com.pguillen.readingtracker.domain.usecase.book

import com.pguillen.readingtracker.domain.repository.BookRepository
import com.pguillen.readingtracker.domain.storage.BookCoverStorage
import com.pguillen.readingtracker.domain.storage.SelectedImage

class ChangeBookCoverUseCase(
    private val bookRepository: BookRepository,
    private val bookCoverStorage: BookCoverStorage
) {
    suspend operator fun invoke(
        bookId: String,
        image: SelectedImage
    ) {
        val book = bookRepository.getBookById(bookId)
            ?: throw IllegalArgumentException(
                "Book not found"
            )

        val oldCoverFileName = book.coverFileName
        val newCoverFileName = bookCoverStorage.saveCover(
            bookId = bookId,
            image = image,
        )
        try {
            bookRepository.updateBookCover(
                bookId = bookId,
                coverFileName = newCoverFileName,
            )
        } catch (exception: Exception) {

            bookCoverStorage.deleteCover(
                newCoverFileName
            )

            throw exception
        }
        oldCoverFileName?.let { fileName ->
            bookCoverStorage.deleteCover(fileName)
        }
    }
}