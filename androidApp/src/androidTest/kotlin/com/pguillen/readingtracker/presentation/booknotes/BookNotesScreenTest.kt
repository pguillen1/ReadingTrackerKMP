package com.pguillen.readingtracker.presentation.booknotes

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.pguillen.readingtracker.domain.model.BookNote
import com.pguillen.readingtracker.domain.model.BookNoteType
import com.pguillen.readingtracker.domain.model.ThemePreference
import com.pguillen.readingtracker.presentation.testtag.ReadingTrackerTestTags
import com.pguillen.readingtracker.presentation.theme.ReadingTrackerTheme
import kotlinx.datetime.LocalDateTime
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BookNotesScreenTest {

	@get:Rule
	val composeRule = createComposeRule()

	@Test
	fun displaysMainElements() {
		setContent(
			uiState = createBookNotesUiState(
				notes = listOf(
					createBookNote(id = NOTE_ID, type = BookNoteType.NOTE),
					createBookNote(id = QUOTE_ID, type = BookNoteType.QUOTE)
				)
			)
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.BookNotes.SCREEN)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.BookNotes.SCREEN_TITLE)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.BookNotes.BACK_BUTTON)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.BookNotes.FILTER_ALL)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.BookNotes.FILTER_NOTES)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.BookNotes.FILTER_QUOTES)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.BookNotes.noteCard(NOTE_ID))
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.BookNotes.noteCard(QUOTE_ID))
			.assertIsDisplayed()
	}

	@Test
	fun displaysNotes() {
		setContent(
			uiState = createBookNotesUiState(
				notes = listOf(
					createBookNote(id = NOTE_ID, type = BookNoteType.NOTE),
					createBookNote(id = QUOTE_ID, type = BookNoteType.QUOTE)
				)
			)
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.BookNotes.noteCard(NOTE_ID))
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.BookNotes.noteCard(QUOTE_ID))
			.assertIsDisplayed()
	}

	@Test
	fun clickingNoteCardCallsCallbackWithNoteId() {
		var clickedNoteId: String? = null

		setContent(
			uiState = createBookNotesUiState(
				notes = listOf(
					createBookNote(id = NOTE_ID)
				)
			),
			onNoteClick = { noteId ->
				clickedNoteId = noteId
			}
		)

		composeRule.onNodeWithTag(
			ReadingTrackerTestTags.BookNotes.noteCard(NOTE_ID)
		).performClick()

		assertEquals(NOTE_ID, clickedNoteId)
	}

	@Test
	fun clickingAllFilterCallsCallback() {
		var selectedFilter: BookNoteFilter? = null

		setContent(
			onFilterSelected = { filter ->
				selectedFilter = filter
			}
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.BookNotes.FILTER_ALL)
			.performClick()

		assertEquals(BookNoteFilter.ALL, selectedFilter)
	}

	@Test
	fun clickingNotesFilterCallsCallback() {
		var selectedFilter: BookNoteFilter? = null

		setContent(
			onFilterSelected = { filter ->
				selectedFilter = filter
			}
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.BookNotes.FILTER_NOTES)
			.performClick()

		assertEquals(BookNoteFilter.NOTES, selectedFilter)
	}

	@Test
	fun clickingQuotesFilterCallsCallback() {
		var selectedFilter: BookNoteFilter? = null

		setContent(
			onFilterSelected = { filter ->
				selectedFilter = filter
			}
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.BookNotes.FILTER_QUOTES)
			.performClick()

		assertEquals(BookNoteFilter.QUOTES, selectedFilter)
	}

	@Test
	fun clickingBackCallsCallback() {
		var clicked = false

		setContent(
			onNavigateBack = {
				clicked = true
			}
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.BookNotes.BACK_BUTTON)
			.performClick()

		assertTrue(clicked)
	}

	@Test
	fun clickingAddNoteCallsCallback() {
		var clicked = false

		setContent(
			onAddNoteClick = {
				clicked = true
			}
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.BookNotes.ADD_BUTTON)
			.performClick()

		assertTrue(clicked)
	}

	private fun setContent(
		uiState: BookNotesUiState = createBookNotesUiState(),
		onNavigateBack: () -> Unit = {},
		onNoteClick: (String) -> Unit = {},
		onDeleteNoteClick: (BookNote) -> Unit = {},
		onDismissDeleteDialog: () -> Unit = {},
		onConfirmDeleteNote: () -> Unit = {},
		onAddNoteClick: () -> Unit = {},
		onFilterSelected: (BookNoteFilter) -> Unit = {}
	) {
		composeRule.setContent {
			ReadingTrackerTheme(themePreference = ThemePreference.LIGHT) {
				BookNotesScreen(
					uiState = uiState,
					onNavigateBack = onNavigateBack,
					onNoteClick = onNoteClick,
					onDeleteNoteClick = onDeleteNoteClick,
					onDismissDeleteDialog = onDismissDeleteDialog,
					onConfirmDeleteNote = onConfirmDeleteNote,
					onAddNoteClick = onAddNoteClick,
					onFilterSelected = onFilterSelected
				)
			}
		}
	}

	private fun createBookNotesUiState(
		isLoading: Boolean = false,
		bookTitle: String = BOOK_TITLE,
		selectedFilter: BookNoteFilter = BookNoteFilter.ALL,
		notes: List<BookNote> = emptyList(),
		notePendingDelete: BookNote? = null,
		errorMessage: String? = null
	): BookNotesUiState {
		return BookNotesUiState(
			isLoading = isLoading,
			bookTitle = bookTitle,
			selectedFilter = selectedFilter,
			notes = notes,
			notePendingDelete = notePendingDelete,
			errorMessage = errorMessage
		)
	}

	private fun createBookNote(
		id: String = NOTE_ID,
		bookId: String = BOOK_ID,
		type: BookNoteType = BookNoteType.NOTE,
		content: String = NOTE_CONTENT,
		page: Int? = 42
	): BookNote {
		return BookNote(
			id = id,
			bookId = bookId,
			type = type,
			content = content,
			page = page,
			createdAt = LocalDateTime(2026, 1, 15, 10, 0),
			updatedAt = LocalDateTime(2026, 1, 15, 10, 0)
		)
	}

	private companion object {
		const val BOOK_ID = "book-1"
		const val BOOK_TITLE = "Dune"

		const val NOTE_ID = "note-1"
		const val QUOTE_ID = "quote-1"
		const val NOTE_CONTENT = "Interesting note"
	}
}