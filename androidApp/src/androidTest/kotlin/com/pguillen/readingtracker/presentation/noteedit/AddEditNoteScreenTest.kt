package com.pguillen.readingtracker.presentation.noteedit

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.pguillen.readingtracker.domain.model.BookNoteType
import com.pguillen.readingtracker.domain.model.ThemePreference
import com.pguillen.readingtracker.presentation.testtag.ReadingTrackerTestTags
import com.pguillen.readingtracker.presentation.theme.ReadingTrackerTheme
import com.pguillen.readingtracker.test.BOOK_TITLE
import com.pguillen.readingtracker.test.NOTE_CONTENT
import com.pguillen.readingtracker.test.NOTE_PAGE
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AddEditNoteScreenTest {

	@get:Rule
	val composeRule = createComposeRule()

	@Test
	fun displaysMainElements() {
		setContent(
			uiState = createAddEditNoteUiState()
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditNote.SCREEN)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditNote.SCREEN_TITLE)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditNote.BACK_BUTTON)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditNote.TYPE_NOTE)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditNote.TYPE_QUOTE)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditNote.CONTENT_FIELD)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditNote.PAGE_FIELD)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditNote.SAVE_BUTTON)
			.assertIsDisplayed()
	}

	@Test
	fun displaysNoteDataInEditMode() {
		setContent(
			uiState = createAddEditNoteUiState(
				isEditMode = true,
				type = BookNoteType.QUOTE,
				content = NOTE_CONTENT,
				page = NOTE_PAGE.toString()
			)
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditNote.CONTENT_FIELD)
			.assertTextContains(NOTE_CONTENT)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditNote.PAGE_FIELD)
			.assertTextContains(NOTE_PAGE.toString())
	}

	@Test
	fun typingInFieldsUpdatesFormState() {
		lateinit var uiState: MutableState<AddEditNoteUiState>

		composeRule.setContent {
			ReadingTrackerTheme(themePreference = ThemePreference.LIGHT) {
				uiState = remember {
					mutableStateOf(
						createAddEditNoteUiState(
							content = "",
							page = ""
						)
					)
				}

				AddEditNoteScreen(
					uiState = uiState.value,
					onTypeSelected = { type ->
						uiState.value = uiState.value.copy(type = type)
					},
					onContentChanged = { value ->
						uiState.value = uiState.value.copy(content = value)
					},
					onPageChanged = { value ->
						uiState.value = uiState.value.copy(page = value)
					},
					onSaveClicked = {},
					onNavigateBack = {}
				)
			}
		}

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditNote.CONTENT_FIELD)
			.performTextInput(NOTE_CONTENT)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditNote.PAGE_FIELD)
			.performTextInput(NOTE_PAGE.toString())

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditNote.CONTENT_FIELD)
			.assertTextContains(NOTE_CONTENT)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditNote.PAGE_FIELD)
			.assertTextContains(NOTE_PAGE.toString())
	}

	@Test
	fun clickingNoteTypeCallsCallback() {
		var selectedType: BookNoteType? = null

		setContent(
			onTypeSelected = { type ->
				selectedType = type
			}
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditNote.TYPE_NOTE)
			.performClick()

		assertEquals(BookNoteType.NOTE, selectedType)
	}

	@Test
	fun clickingQuoteTypeCallsCallback() {
		var selectedType: BookNoteType? = null

		setContent(
			onTypeSelected = { type ->
				selectedType = type
			}
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditNote.TYPE_QUOTE)
			.performClick()

		assertEquals(BookNoteType.QUOTE, selectedType)
	}

	@Test
	fun clickingSaveCallsCallback() {
		var clicked = false

		setContent(
			uiState = createAddEditNoteUiState(
				content = NOTE_CONTENT,
				page = NOTE_PAGE.toString()
			),
			onSaveClicked = {
				clicked = true
			}
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditNote.SAVE_BUTTON)
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

		composeRule.onNodeWithTag(ReadingTrackerTestTags.AddEditNote.BACK_BUTTON)
			.performClick()

		assertTrue(clicked)
	}

	private fun setContent(
		uiState: AddEditNoteUiState = createAddEditNoteUiState(),
		onTypeSelected: (BookNoteType) -> Unit = {},
		onContentChanged: (String) -> Unit = {},
		onPageChanged: (String) -> Unit = {},
		onSaveClicked: () -> Unit = {},
		onNavigateBack: () -> Unit = {}
	) {
		composeRule.setContent {
			ReadingTrackerTheme(themePreference = ThemePreference.LIGHT) {
				AddEditNoteScreen(
					uiState = uiState,
					onTypeSelected = onTypeSelected,
					onContentChanged = onContentChanged,
					onPageChanged = onPageChanged,
					onSaveClicked = onSaveClicked,
					onNavigateBack = onNavigateBack
				)
			}
		}
	}

	private fun createAddEditNoteUiState(
		isLoading: Boolean = false,
		isEditMode: Boolean = false,
		bookTitle: String = BOOK_TITLE,
		type: BookNoteType = BookNoteType.NOTE,
		content: String = "",
		page: String = "",
		errorMessage: String? = null
	): AddEditNoteUiState {
		return AddEditNoteUiState(
			isLoading = isLoading,
			isEditMode = isEditMode,
			bookTitle = bookTitle,
			type = type,
			content = content,
			page = page,
			errorMessage = errorMessage
		)
	}
}