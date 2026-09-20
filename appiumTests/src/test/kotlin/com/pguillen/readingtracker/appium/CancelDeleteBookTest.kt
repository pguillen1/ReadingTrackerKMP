package com.pguillen.readingtracker.appium

import org.junit.jupiter.api.Test

class CancelDeleteBookTest: BaseAppiumTest() {

	@Test
	fun cancelDeleteBookTest() {
		openDeepLink("readingtracker://debug/seed/single-book")

		waitUntilVisible("library_book_card").click()
		waitUntilVisible("book_detail_delete_button").click()
		waitUntilVisible("book_detail_cancel_delete_button").click()

		waitUntilVisible("book_detail_book_card")
	}
}