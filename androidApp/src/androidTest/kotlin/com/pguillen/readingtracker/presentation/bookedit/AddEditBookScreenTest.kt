package com.pguillen.readingtracker.presentation.bookedit

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.pguillen.readingtracker.domain.model.ReadingStatus
import com.pguillen.readingtracker.domain.model.ThemePreference
import com.pguillen.readingtracker.presentation.testtag.ReadingTrackerTestTags
import com.pguillen.readingtracker.presentation.theme.ReadingTrackerTheme
import com.pguillen.readingtracker.test.BOOK_AUTHOR
import com.pguillen.readingtracker.test.BOOK_CURRENT_PAGE
import com.pguillen.readingtracker.test.BOOK_TITLE
import com.pguillen.readingtracker.test.BOOK_TOTAL_PAGES
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AddEditBookScreenTest {

	@get:Rule
	val composeRule = createComposeRule()

	@Test
	fun displaysMainElements() {
		setContent(
			uiStateProvider = { createAddEditBookUiState() }
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditBook.SCREEN)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditBook.SCREEN_TITLE)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditBook.BACK_BUTTON)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditBook.TITLE_FIELD)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditBook.AUTHOR_FIELD)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditBook.TOTAL_PAGES_FIELD)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditBook.CURRENT_PAGE_FIELD)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditBook.STATUS_WANT_TO_READ)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditBook.STATUS_READING)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditBook.STATUS_FINISHED)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditBook.SAVE_BUTTON)
			.assertIsDisplayed()
	}

	@Test
	fun displaysBookDataInEditMode() {
		setContent(
			uiStateProvider = {
				createAddEditBookUiState(
					isEditMode = true,
					title = BOOK_TITLE,
					author = BOOK_AUTHOR,
					totalPages = BOOK_TOTAL_PAGES.toString(),
					currentPage = BOOK_CURRENT_PAGE.toString(),
					selectedStatus = ReadingStatus.READING
				)
			}
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditBook.TITLE_FIELD)
			.assertTextContains(BOOK_TITLE)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditBook.AUTHOR_FIELD)
			.assertTextContains(BOOK_AUTHOR)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditBook.TOTAL_PAGES_FIELD)
			.assertTextContains(BOOK_TOTAL_PAGES.toString())

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditBook.CURRENT_PAGE_FIELD)
			.assertTextContains(BOOK_CURRENT_PAGE.toString())
	}

	@Test
	fun typingInFieldsCallsCallbacks() {

		lateinit var uiState: MutableState<AddEditBookUiState>

		composeRule.setContent {
			ReadingTrackerTheme(themePreference = ThemePreference.LIGHT) {
				uiState = remember {
					mutableStateOf(createAddEditBookUiState())
				}

				AddEditBookScreen(
					uiState = uiState.value,
					onTitleChanged = { value ->
						uiState.value = uiState.value.copy(title = value)
					},
					onAuthorChanged = { value ->
						uiState.value = uiState.value.copy(author = value)
					},
					onTotalPagesChanged = { value ->
						uiState.value = uiState.value.copy(totalPages = value)
					},
					onCurrentPageChanged = { value ->
						uiState.value = uiState.value.copy(currentPage = value)
					},
					onStatusSelected = { status ->
						uiState.value = uiState.value.copy(selectedStatus = status)
					},
					onNavigateBack = {},
					onSaveClicked = {}
				)
			}
		}

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditBook.TITLE_FIELD)
			.performTextInput(BOOK_TITLE)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditBook.AUTHOR_FIELD)
			.performTextInput(BOOK_AUTHOR)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditBook.TOTAL_PAGES_FIELD)
			.performTextInput(BOOK_TOTAL_PAGES.toString())

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditBook.CURRENT_PAGE_FIELD)
			.performTextInput(BOOK_CURRENT_PAGE.toString())

		assertEquals(BOOK_TITLE, uiState.value.title)
		assertEquals(BOOK_AUTHOR, uiState.value.author)
		assertEquals(BOOK_TOTAL_PAGES.toString(), uiState.value.totalPages)
		assertEquals(BOOK_CURRENT_PAGE.toString(), uiState.value.currentPage)
	}

	@Test
	fun clickingReadingStatusCallsCallback() {
		var selectedStatus: ReadingStatus? = null

		setContent(
			onStatusSelected = { status ->
				selectedStatus = status
			}
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditBook.STATUS_READING)
			.performClick()

		assertEquals(ReadingStatus.READING, selectedStatus)
	}

	@Test
	fun clickingFinishedStatusCallsCallback() {
		var selectedStatus: ReadingStatus? = null

		setContent(
			onStatusSelected = { status ->
				selectedStatus = status
			}
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditBook.STATUS_FINISHED)
			.performClick()

		assertEquals(ReadingStatus.FINISHED, selectedStatus)
	}

	@Test
	fun clickingWantToReadStatusCallsCallback() {
		var selectedStatus: ReadingStatus? = null

		setContent(
			onStatusSelected = { status ->
				selectedStatus = status
			}
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditBook.STATUS_WANT_TO_READ)
			.performClick()

		assertEquals(ReadingStatus.WANT_TO_READ, selectedStatus)
	}

	@Test
	fun clickingSaveCallsCallback() {
		var clicked = false

		setContent(
			uiStateProvider = {
				createAddEditBookUiState(
					title = BOOK_TITLE,
					author = BOOK_AUTHOR,
					totalPages = BOOK_TOTAL_PAGES.toString(),
					currentPage = BOOK_CURRENT_PAGE.toString(),
					selectedStatus = ReadingStatus.READING
				)
			},
			onSaveClick = {
				clicked = true
			}
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditBook.SAVE_BUTTON)
			.performClick()

		assertTrue(clicked)
	}

	@Test
	fun clickingBackCallsCallback() {
		var clicked = false

		setContent(
			onBackClick = {
				clicked = true
			}
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditBook.BACK_BUTTON)
			.performClick()

		assertTrue(clicked)
	}

	private fun setContent(
		uiStateProvider: () -> AddEditBookUiState = { createAddEditBookUiState() },
		onTitleChanged: (String) -> Unit = {},
		onAuthorChanged: (String) -> Unit = {},
		onTotalPagesChanged: (String) -> Unit = {},
		onCurrentPageChanged: (String) -> Unit = {},
		onStatusSelected: (ReadingStatus) -> Unit = {},
		onSaveClick: () -> Unit = {},
		onBackClick: () -> Unit = {}
	) {
		composeRule.setContent {
			ReadingTrackerTheme(themePreference = ThemePreference.LIGHT) {
				AddEditBookScreen(
					uiState = uiStateProvider(),
					onTitleChanged = onTitleChanged,
					onAuthorChanged = onAuthorChanged,
					onTotalPagesChanged = onTotalPagesChanged,
					onCurrentPageChanged = onCurrentPageChanged,
					onStatusSelected = onStatusSelected,
					onSaveClicked = onSaveClick,
					onNavigateBack = onBackClick
				)
			}
		}
	}

	private fun createAddEditBookUiState(
		isLoading: Boolean = false,
		isEditMode: Boolean = false,
		title: String = "",
		author: String = "",
		totalPages: String = "",
		currentPage: String = "",
		selectedStatus: ReadingStatus = ReadingStatus.WANT_TO_READ,
		errorMessage: String? = null
	): AddEditBookUiState {
		return AddEditBookUiState(
			isLoading = isLoading,
			isEditMode = isEditMode,
			title = title,
			author = author,
			totalPages = totalPages,
			currentPage = currentPage,
			selectedStatus = selectedStatus,
			errorMessage = errorMessage
		)
	}
}