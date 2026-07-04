package com.pguillen.readingtracker.presentation.sessionedit

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.pguillen.readingtracker.domain.model.ThemePreference
import com.pguillen.readingtracker.presentation.testtag.ReadingTrackerTestTags
import com.pguillen.readingtracker.presentation.theme.ReadingTrackerTheme
import com.pguillen.readingtracker.test.BOOK_TITLE
import com.pguillen.readingtracker.test.BOOK_TOTAL_PAGES
import com.pguillen.readingtracker.test.SESSION_END_PAGE
import com.pguillen.readingtracker.test.SESSION_MINUTES
import com.pguillen.readingtracker.test.SESSION_NOTE
import com.pguillen.readingtracker.test.SESSION_START_PAGE
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertTrue

class AddEditReadingSessionScreenTest {

	@get:Rule
	val composeRule = createComposeRule()

	@Test
	fun displaysMainElements() {
		setContent(
			uiState = createAddEditSessionUiState()
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditSession.SCREEN)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditSession.SCREEN_TITLE)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditSession.BACK_BUTTON)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditSession.TOTAL_PAGES_FIELD)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditSession.START_PAGE_FIELD)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditSession.END_PAGE_FIELD)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditSession.MINUTES_FIELD)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditSession.NOTE_FIELD)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditSession.SAVE_BUTTON)
			.assertIsDisplayed()
	}

	@Test
	fun displaysSessionDataInEditMode() {
		setContent(
			uiState = createAddEditSessionUiState(
				isEditMode = true,
				totalPages = BOOK_TOTAL_PAGES,
				startPage = SESSION_START_PAGE.toString(),
				endPage = SESSION_END_PAGE.toString(),
				minutes = SESSION_MINUTES.toString(),
				note = SESSION_NOTE
			)
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditSession.TOTAL_PAGES_FIELD)
			.assertTextContains(BOOK_TOTAL_PAGES.toString(), substring = true)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditSession.START_PAGE_FIELD)
			.assertTextContains(SESSION_START_PAGE.toString())

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditSession.END_PAGE_FIELD)
			.assertTextContains(SESSION_END_PAGE.toString())

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditSession.MINUTES_FIELD)
			.assertTextContains(SESSION_MINUTES.toString())

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditSession.NOTE_FIELD)
			.assertTextContains(SESSION_NOTE)
	}

	@Test
	fun typingInFieldsUpdatesFormState() {
		lateinit var uiState: MutableState<AddEditReadingSessionUiState>

		composeRule.setContent {
			ReadingTrackerTheme(themePreference = ThemePreference.LIGHT) {
				uiState = remember {
					mutableStateOf(
						createAddEditSessionUiState(
							totalPages = BOOK_TOTAL_PAGES,
							startPage = "",
							endPage = "",
							minutes = "",
							note = ""
						)
					)
				}

				AddEditReadingSessionScreen(
					uiState = uiState.value,
					onNavigateBack = {},
					onStartPageChanged = { value ->
						uiState.value = uiState.value.copy(startPage = value)
					},
					onEndPageChanged = { value ->
						uiState.value = uiState.value.copy(endPage = value)
					},
					onMinutesChanged = { value ->
						uiState.value = uiState.value.copy(minutes = value)
					},
					onNoteChanged = { value ->
						uiState.value = uiState.value.copy(note = value)
					},
					onSaveClicked = {},
				)
			}
		}

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditSession.START_PAGE_FIELD)
			.performTextInput(SESSION_START_PAGE.toString())

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditSession.END_PAGE_FIELD)
			.performTextInput(SESSION_END_PAGE.toString())

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditSession.MINUTES_FIELD)
			.performTextInput(SESSION_MINUTES.toString())

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditSession.NOTE_FIELD)
			.performTextInput(SESSION_NOTE)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditSession.START_PAGE_FIELD)
			.assertTextContains(SESSION_START_PAGE.toString())

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditSession.END_PAGE_FIELD)
			.assertTextContains(SESSION_END_PAGE.toString())

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditSession.MINUTES_FIELD)
			.assertTextContains(SESSION_MINUTES.toString())

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditSession.NOTE_FIELD)
			.assertTextContains(SESSION_NOTE)
	}

	@Test
	fun clickingSaveCallsCallback() {
		var clicked = false

		setContent(
			uiState = createAddEditSessionUiState(
				totalPages = BOOK_TOTAL_PAGES,
				startPage = SESSION_START_PAGE.toString(),
				endPage = SESSION_END_PAGE.toString(),
				minutes = SESSION_MINUTES.toString(),
				note = SESSION_NOTE
			),
			onSaveClicked = {
				clicked = true
			}
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditSession.SAVE_BUTTON)
			.performClick()

		assertTrue(clicked)
	}

	@Test
	fun clickingBackCallsCallback() {
		var clicked = false

		setContent(
			onNavigateBack = {
				clicked = true
			}
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditSession.BACK_BUTTON)
			.performClick()

		assertTrue(clicked)
	}

	private fun setContent(
		uiState: AddEditReadingSessionUiState = createAddEditSessionUiState(),
		onNavigateBack: () -> Unit = {},
		onStartPageChanged: (String) -> Unit = {},
		onEndPageChanged: (String) -> Unit = {},
		onMinutesChanged: (String) -> Unit = {},
		onNoteChanged: (String) -> Unit = {},
		onSaveClicked: () -> Unit = {}
	) {
		composeRule.setContent {
			ReadingTrackerTheme(themePreference = ThemePreference.LIGHT) {
				AddEditReadingSessionScreen(
					uiState = uiState,
					onNavigateBack = onNavigateBack,
					onStartPageChanged = onStartPageChanged,
					onEndPageChanged = onEndPageChanged,
					onMinutesChanged = onMinutesChanged,
					onNoteChanged = onNoteChanged,
					onSaveClicked = onSaveClicked,
				)
			}
		}
	}

	private fun createAddEditSessionUiState(
		isLoading: Boolean = false,
		isEditMode: Boolean = false,
		bookTitle: String = BOOK_TITLE,
		totalPages: Int? = BOOK_TOTAL_PAGES,
		startPage: String = "",
		endPage: String = "",
		minutes: String = "",
		note: String = "",
		errorMessage: String? = null
	): AddEditReadingSessionUiState {
		return AddEditReadingSessionUiState(
			isLoading = isLoading,
			isEditMode = isEditMode,
			bookTitle = bookTitle,
			totalPages = totalPages,
			startPage = startPage,
			endPage = endPage,
			minutes = minutes,
			note = note,
			errorMessage = errorMessage
		)
	}
}