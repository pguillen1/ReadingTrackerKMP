package com.pguillen.readingtracker.presentation.bookdetail

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.pguillen.readingtracker.domain.model.Book
import com.pguillen.readingtracker.domain.model.BookNote
import com.pguillen.readingtracker.domain.model.BookNoteType
import com.pguillen.readingtracker.domain.model.ReadingSession
import com.pguillen.readingtracker.domain.model.ReadingStatus
import com.pguillen.readingtracker.domain.model.ThemePreference
import com.pguillen.readingtracker.presentation.testtag.ReadingTrackerTestTags
import com.pguillen.readingtracker.presentation.theme.ReadingTrackerTheme
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertTrue

class BookDetailScreenTest {

	@get:Rule
	val composeRule = createComposeRule()

	@Test
	fun displaysMainElements() {
		setContent(
			uiState = createBookDetailUiState(
				book = createBook(),
				recentSessions = listOf(
					createReadingSession(id = SESSION_ID)
				),
				recentNotes = listOf(
					createBookNote(id = NOTE_ID)
				)
			)
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.BookDetail.SCREEN)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.BookDetail.SCREEN_TITLE)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.BookDetail.EDIT_BUTTON)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.BookDetail.DELETE_BUTTON)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.BookDetail.BOOK_CARD)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.BookDetail.ADD_SESSION_BUTTON)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.BookDetail.ADD_NOTE_BUTTON)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.BookDetail.RECENT_SESSIONS_CARD)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.BookDetail.RECENT_NOTES_CARD)
			.assertIsDisplayed()
	}

	@Test
	fun displaysBookInfo() {
		setContent(
			uiState = createBookDetailUiState(
				book = createBook(
					title = BOOK_TITLE,
					author = BOOK_AUTHOR
				)
			)
		)

		composeRule.onNodeWithText(BOOK_TITLE)
			.assertIsDisplayed()

		composeRule.onNodeWithText(BOOK_AUTHOR)
			.assertIsDisplayed()
	}

	@Test
	fun displaysRecentSessionsAndNotes() {
		setContent(
			uiState = createBookDetailUiState(
				book = createBook(),
				recentSessions = listOf(
					createReadingSession(
						id = SESSION_ID,
						startPage = 10,
						endPage = 30,
						minutes = 25
					)
				),
				recentNotes = listOf(
					createBookNote(
						id = NOTE_ID,
						content = NOTE_CONTENT
					)
				)
			)
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.BookDetail.RECENT_SESSIONS_CARD)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.BookDetail.RECENT_NOTES_CARD)
			.assertIsDisplayed()

		composeRule.onNodeWithText(NOTE_CONTENT)
			.assertIsDisplayed()
	}

	@Test
	fun clickingEditCallsCallback() {
		var clicked = false

		setContent(
			uiState = createBookDetailUiState(
				book = createBook()
			),
			onEditBookClick = {
				clicked = true
			}
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.BookDetail.EDIT_BUTTON)
			.performClick()

		assertTrue(clicked)
	}

	@Test
	fun clickingDeleteCallsCallback() {
		var clicked = false

		setContent(
			uiState = createBookDetailUiState(
				book = createBook()
			),
			onDeleteBookClick = {
				clicked = true
			}
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.BookDetail.DELETE_BUTTON)
			.performClick()

		assertTrue(clicked)
	}

	@Test
	fun clickingLogSessionCallsCallback() {
		var clicked = false

		setContent(
			uiState = createBookDetailUiState(
				book = createBook()
			),
			onLogSessionClick = {
				clicked = true
			}
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.BookDetail.ADD_SESSION_BUTTON)
			.performClick()

		assertTrue(clicked)
	}

	@Test
	fun clickingAddNoteCallsCallback() {
		var clicked = false

		setContent(
			uiState = createBookDetailUiState(
				book = createBook()
			),
			onAddNoteClick = {
				clicked = true
			}
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.BookDetail.ADD_NOTE_BUTTON)
			.performClick()

		assertTrue(clicked)
	}

	private fun setContent(
		uiState: BookDetailUiState = createBookDetailUiState(),
		onNavigateBack: () -> Unit = {},
		onEditBookClick: (String) -> Unit = {},
		onLogSessionClick: (String) -> Unit = {},
		onAddNoteClick: (String) -> Unit = {},
		onSeeAllSessionsClick: (String) -> Unit = {},
		onSeeAllNotesClick: (String) -> Unit = {},
		onDeleteBookClick: () -> Unit = {},
		onDismissDeleteDialog: () -> Unit = {},
		onConfirmDeleteBook: () -> Unit = {}
	) {
		composeRule.setContent {
			ReadingTrackerTheme(themePreference = ThemePreference.LIGHT) {
				BookDetailScreen(
					uiState = uiState,
					onNavigateBack = onNavigateBack,
					onEditBookClick = onEditBookClick,
					onDeleteBookClick = onDeleteBookClick,
					onDismissDeleteDialog = onDismissDeleteDialog,
					onConfirmDeleteBook = onConfirmDeleteBook,
					onLogSessionClick = onLogSessionClick,
					onAddNoteClick = onAddNoteClick,
					onSeeAllSessionsClick = onSeeAllSessionsClick,
					onSeeAllNotesClick = onSeeAllNotesClick
				)
			}
		}
	}

	private fun createBookDetailUiState(
		isLoading: Boolean = false,
		book: Book? = createBook(),
		recentSessions: List<ReadingSession> = emptyList(),
		recentNotes: List<BookNote> = emptyList(),
		errorMessage: String? = null,
		showDeleteDialog: Boolean = false
	): BookDetailUiState {
		return BookDetailUiState(
			isLoading = isLoading,
			book = book,
			recentSessions = recentSessions,
			recentNotes = recentNotes,
			errorMessage = errorMessage,
			showDeleteDialog = showDeleteDialog
		)
	}

	private fun createBook(
		id: String = BOOK_ID,
		title: String = BOOK_TITLE,
		author: String = BOOK_AUTHOR,
		totalPages: Int? = BOOK_TOTAL_PAGES,
		currentPage: Int = BOOK_CURRENT_PAGE,
		status: ReadingStatus = ReadingStatus.READING
	): Book {
		return Book(
			id = id,
			title = title,
			author = author,
			totalPages = totalPages,
			currentPage = currentPage,
			status = status,
			startedAt = null,
			finishedAt = null,
			addedAt = LocalDateTime(2026, 1, 1, 10, 0),
			updatedAt = LocalDateTime(2026, 1, 1, 10, 0)
		)
	}

	private fun createReadingSession(
		id: String = SESSION_ID,
		bookId: String = BOOK_ID,
		date: LocalDate = LocalDate(2026, 1, 15),
		startPage: Int? = 10,
		endPage: Int = 30,
		minutes: Int? = 25,
		note: String? = null
	): ReadingSession {
		return ReadingSession(
			id = id,
			bookId = bookId,
			date = date,
			startPage = startPage,
			endPage = endPage,
			minutes = minutes,
			note = note,
			createdAt = LocalDateTime(2026, 1, 15, 10, 0)
		)
	}

	private fun createBookNote(
		id: String = NOTE_ID,
		bookId: String = BOOK_ID,
		type: BookNoteType = BookNoteType.NOTE,
		content: String = NOTE_CONTENT,
		page: Int? = null
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
		const val BOOK_AUTHOR = "Frank Herbert"
		const val BOOK_TOTAL_PAGES = 500
		const val BOOK_CURRENT_PAGE = 120

		const val SESSION_ID = "session-1"

		const val NOTE_ID = "note-1"
		const val NOTE_CONTENT = "This is an interesting note"
	}
}