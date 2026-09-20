package com.pguillen.readingtracker.appium

import org.junit.jupiter.api.Test

class AddBookTest : BaseAppiumTest() {

	@Test
	fun addBookTest() {
		val addButton = waitUntilVisible("library_add_book_fab")
		addButton.click()
		val titleField = waitUntilVisible("add_edit_book_title_field")
		val authorField = waitUntilVisible("add_edit_book_author_field")
		val totalPagesField = waitUntilVisible("add_edit_book_total_pages_field")

		titleField.sendKeys("Appium Book Title")
		authorField.sendKeys("Appium Book Author")
		totalPagesField.sendKeys("300")

		val saveButton = waitUntilVisible("add_edit_book_save_button")
		saveButton.click()

		waitUntilTextVisible("Appium Book Title")
		waitUntilTextVisible("Appium Book Author")
		waitUntilTextVisible("300")
	}
}