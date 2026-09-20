package com.pguillen.readingtracker.appium

import org.junit.jupiter.api.Test

class EditSessionTest: BaseAppiumTest() {

	@Test
	fun editSessionTest() {
		openDeepLink("readingtracker://debug/seed/single-book-with-session")

		waitUntilVisible("library_book_card").click()
		waitUntilVisible("book_detail_sessions_see_all_button").click()
		waitUntilVisible("book_sessions_session_card").click()

		waitUntilVisible("add_edit_session_start_page_field")
			.sendKeys(
				"10"
			)
		waitUntilVisible("add_edit_session_end_page_field")
			.sendKeys(
				"50"
			)
		waitUntilVisible("add_edit_session_minutes_field")
			.sendKeys(
				"25"
			)
		waitUntilVisible("add_edit_session_note_field")
			.sendKeys(
				"Session Note"
			)
		waitUntilVisible("add_edit_session_save_button").click()

		waitUntilTextVisible("10")
		waitUntilTextVisible("25")
		waitUntilTextVisible("50")
		waitUntilTextVisible("Session Note")
	}
}