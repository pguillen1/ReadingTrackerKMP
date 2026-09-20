package com.pguillen.readingtracker.appium

import org.junit.jupiter.api.Test

class DeleteBookTest: BaseAppiumTest() {

	@Test
	fun deleteBookTest() {
		openDeepLink("readingtracker://debug/seed/single-book")

		waitUntilVisible("library_book_card").click()
		waitUntilVisible("book_detail_delete_button").click()
		waitUntilVisible("book_detail_confirm_delete_button").click()

		waitUntilVisible("library_no_books_found")
	}
}