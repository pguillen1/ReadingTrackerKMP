package com.pguillen.readingtracker.appium

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StatusFilterTest: BaseAppiumTest() {

	@Test
	fun statusFilterTest() {
		openDeepLink("readingtracker://debug/seed/multiple-books")

		waitUntilVisible("library_filter_want_to_read").click()
		var bookStatus = waitUntilVisible("library_book_status")
		assertEquals("Want to read", bookStatus.text)

		waitUntilVisible("library_filter_reading").click()
		bookStatus = waitUntilVisible("library_book_status")
		assertEquals("Reading", bookStatus.text)

		waitUntilVisible("library_filter_finished").click()
		bookStatus = waitUntilVisible("library_book_status")
		assertEquals("Finished", bookStatus.text)
	}
}