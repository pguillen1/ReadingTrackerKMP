package com.pguillen.readingtracker.appium

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SearchFilterTest : BaseAppiumTest() {

	@Test
	fun searchFilterTest() {
		openDeepLink("readingtracker://debug/seed/multiple-books")

		waitUntilVisible("library_search_field")
			.sendKeys("1")
		var bookTitle = waitUntilVisible("library_book_title")
		assertEquals("Maestro Title 1", bookTitle.text)
		waitUntilVisible("library_search_field")
			.sendKeys("2")
		bookTitle = waitUntilVisible("library_book_title")
		assertEquals("Maestro Title 2", bookTitle.text)
		waitUntilVisible("library_search_field")
			.sendKeys("3")
		bookTitle = waitUntilVisible("library_book_title")
		assertEquals("Maestro Title 3", bookTitle.text)
	}
}