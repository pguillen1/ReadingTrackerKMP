package com.pguillen.readingtracker.domain.storage

class FakeBookCoverStorage : BookCoverStorage {
	override suspend fun saveCover(
		bookId: String,
		image: SelectedImage
	): String {
		return ""
	}

	override suspend fun deleteCover(fileName: String) {}
}