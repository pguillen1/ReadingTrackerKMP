package com.pguillen.readingtracker.fake

import com.pguillen.readingtracker.domain.storage.BookCoverStorage
import com.pguillen.readingtracker.domain.storage.SelectedImage

class FakeBookCoverStorage : BookCoverStorage {

	var fileNameToReturn = "new_cover.webp"

	val savedImages = mutableListOf<Pair<String, SelectedImage>>()
	val deletedFiles = mutableListOf<String>()

	var saveShouldFail = false
	var deleteShouldFail = false

	override suspend fun saveCover(
		bookId: String,
		image: SelectedImage,
	): String {
		if (saveShouldFail) {
			throw IllegalStateException("Save failed")
		}

		savedImages += bookId to image

		return fileNameToReturn
	}

	override suspend fun deleteCover(
		fileName: String,
	) {
		deletedFiles += fileName

		if (deleteShouldFail) {
			throw IllegalStateException("Delete failed")
		}
	}
}