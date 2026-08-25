package com.pguillen.readingtracker.domain.storage

interface BookCoverStorage {
    suspend fun saveCover(
        bookId: String,
        image: SelectedImage
    ): String

    suspend fun deleteCover(
        fileName: String
    )
}