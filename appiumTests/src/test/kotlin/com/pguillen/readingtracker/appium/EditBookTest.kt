package com.pguillen.readingtracker.appium

import org.junit.jupiter.api.Test

class EditBookTest: BaseAppiumTest() {

	@Test
	fun editBookTest() {
		openDeepLink("readingtracker://debug/seed/single-book")

		waitUntilVisible("library_book_card").click()
		waitUntilVisible("book_detail_edit_button").click()
		waitUntilVisible("add_edit_book_title_field")
			.sendKeys("Edited Book Title")
		waitUntilVisible("add_edit_book_author_field")
			.sendKeys("Edited Book Author")
		waitUntilVisible("add_edit_book_total_pages_field")
			.sendKeys("250")
		waitUntilVisible("add_edit_book_save_button").click()

		waitUntilTextVisible("Edited Book Title")
		waitUntilTextVisible("Edited Book Author")
		waitUntilTextVisible("250")
	}
}