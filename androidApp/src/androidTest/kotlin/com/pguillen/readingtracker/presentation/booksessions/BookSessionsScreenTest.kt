package com.pguillen.readingtracker.presentation.booksessions

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.pguillen.readingtracker.domain.model.ReadingSession
import com.pguillen.readingtracker.domain.model.ThemePreference
import com.pguillen.readingtracker.presentation.testtag.ReadingTrackerTestTags
import com.pguillen.readingtracker.presentation.theme.ReadingTrackerTheme
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BookSessionsScreenTest {

	@get:Rule
	val composeRule = createComposeRule()

	@Test
	fun displaysMainElements() {
		setContent(
			uiState = createBookSessionsUiState(
				sessions = listOf(
					createReadingSession(id = SESSION_ID),
					createReadingSession(id = SECOND_SESSION_ID)
				)
			)
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.BookSessions.SCREEN)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.BookSessions.SCREEN_TITLE)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.BookSessions.BACK_BUTTON)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.BookSessions.sessionCard(SESSION_ID))
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.BookSessions.sessionCard(SECOND_SESSION_ID))
			.assertIsDisplayed()
	}

	@Test
	fun displaysSessions() {
		setContent(
			uiState = createBookSessionsUiState(
				sessions = listOf(
					createReadingSession(id = SESSION_ID),
					createReadingSession(id = SECOND_SESSION_ID)
				)
			)
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.BookSessions.sessionCard(SESSION_ID))
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.BookSessions.sessionCard(SECOND_SESSION_ID))
			.assertIsDisplayed()
	}

	@Test
	fun clickingSessionCardCallsCallbackWithSessionId() {
		var clickedSessionId: String? = null

		setContent(
			uiState = createBookSessionsUiState(
				sessions = listOf(
					createReadingSession(id = SESSION_ID)
				)
			),
			onSessionClick = { sessionId ->
				clickedSessionId = sessionId
			}
		)

		composeRule.onNodeWithTag(
			ReadingTrackerTestTags.BookSessions.sessionCard(SESSION_ID)
		).performClick()

		assertEquals(SESSION_ID, clickedSessionId)
	}

	@Test
	fun clickingBackCallsCallback() {
		var clicked = false

		setContent(
			onNavigateBack = {
				clicked = true
			}
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.BookSessions.BACK_BUTTON)
			.performClick()

		assertTrue(clicked)
	}

	@Test
	fun clickingAddSessionCallsCallback() {
		var clicked = false

		setContent(
			onAddSessionClick = {
				clicked = true
			}
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.BookSessions.ADD_BUTTON)
			.performClick()

		assertTrue(clicked)
	}

	private fun setContent(
		uiState: BookSessionsUiState = createBookSessionsUiState(),
		onNavigateBack: () -> Unit = {},
		onSessionClick: (String) -> Unit = {},
		onDeleteSessionClick: (ReadingSession) -> Unit = {},
		onDismissDeleteDialog: () -> Unit = {},
		onConfirmDeleteSession: () -> Unit = {},
		onAddSessionClick: () -> Unit = {}
	) {
		composeRule.setContent {
			ReadingTrackerTheme(themePreference = ThemePreference.LIGHT) {
				BookSessionsScreen(
					uiState = uiState,
					onNavigateBack = onNavigateBack,
					onSessionClick = onSessionClick,
					onDeleteSessionClick = onDeleteSessionClick,
					onDismissDeleteDialog = onDismissDeleteDialog,
					onConfirmDeleteSession = onConfirmDeleteSession,
					onAddSessionClick = onAddSessionClick
				)
			}
		}
	}

	private fun createBookSessionsUiState(
		isLoading: Boolean = false,
		bookTitle: String = BOOK_TITLE,
		sessions: List<ReadingSession> = emptyList(),
		sessionPendingDelete: ReadingSession? = null,
		errorMessage: String? = null
	): BookSessionsUiState {
		return BookSessionsUiState(
			isLoading = isLoading,
			bookTitle = bookTitle,
			sessions = sessions,
			sessionPendingDelete = sessionPendingDelete,
			errorMessage = errorMessage
		)
	}

	private fun createReadingSession(
		id: String = SESSION_ID,
		bookId: String = BOOK_ID,
		date: LocalDate = LocalDate(2026, 1, 15),
		startPage: Int? = 100,
		endPage: Int = 120,
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

	private companion object {
		const val BOOK_ID = "book-1"
		const val BOOK_TITLE = "Dune"

		const val SESSION_ID = "session-1"
		const val SECOND_SESSION_ID = "session-2"
	}
}