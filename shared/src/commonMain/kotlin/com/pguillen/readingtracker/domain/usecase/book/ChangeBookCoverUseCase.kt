package com.pguillen.readingtracker.domain.usecase.book

import com.pguillen.readingtracker.domain.repository.BookRepository
import com.pguillen.readingtracker.domain.storage.BookCoverStorage
import com.pguillen.readingtracker.domain.storage.SelectedImage
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

class ChangeBookCoverUseCase(
    private val bookRepository: BookRepository,
    private val bookCoverStorage: BookCoverStorage
) {
    suspend operator fun invoke(
        bookId: String,
        image: SelectedImage
    ) = withContext(NonCancellable) {
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

            try {
                bookCoverStorage.deleteCover(newCoverFileName)
            } catch (_: Exception) {

            }

            throw exception
        }
        oldCoverFileName?.let { fileName ->
            try {
                bookCoverStorage.deleteCover(fileName)
            } catch (_: Exception) {

            }
        }
    }
}