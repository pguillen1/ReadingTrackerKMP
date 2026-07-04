package com.pguillen.readingtracker.presentation.library

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.pguillen.readingtracker.domain.model.Book
import com.pguillen.readingtracker.domain.model.ThemePreference
import com.pguillen.readingtracker.presentation.testtag.ReadingTrackerTestTags
import com.pguillen.readingtracker.presentation.theme.ReadingTrackerTheme
import com.pguillen.readingtracker.test.BOOK_ID
import com.pguillen.readingtracker.test.BOOK_ID_2
import com.pguillen.readingtracker.test.BOOK_TITLE
import com.pguillen.readingtracker.test.BOOK_TITLE_2
import com.pguillen.readingtracker.test.createBook
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LibraryScreenTest {

	@get:Rule
	val composeRule = createComposeRule()

	@Test
	fun displaysMainElements() {
		val books = listOf(
			createBook(id = BOOK_ID, title = BOOK_TITLE),
			createBook(id = BOOK_ID_2, title = BOOK_TITLE_2)
		)

		setContent(
			uiState = createLibraryUiState(
				books = books
			)
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Library.SCREEN)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Library.SCREEN_TITLE)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Library.SEARCH_FIELD)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Library.FILTER_READING)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Library.FILTER_FINISHED)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Library.FILTER_WANT_TO_READ)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Library.ADD_BOOK_FAB)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Library.bookCard(BOOK_ID))
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Library.bookCard(BOOK_ID_2))
			.assertIsDisplayed()
	}

	@Test
	fun displaysBooks() {
		setContent(
			uiState = createLibraryUiState(
				books = listOf(
					createBook(id = BOOK_ID, title = BOOK_TITLE),
					createBook(id = BOOK_ID_2, title = BOOK_TITLE_2)
				)
			)
		)

		composeRule.onNodeWithText(BOOK_TITLE)
			.assertIsDisplayed()

		composeRule.onNodeWithText(BOOK_TITLE_2)
			.assertIsDisplayed()
	}

	@Test
	fun displaysEmptyStateWhenThereAreNoBooks() {
		setContent(
			uiState = createLibraryUiState(
				books = emptyList()
			)
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Library.NO_BOOKS_FOUND)
			.assertIsDisplayed()
	}

	@Test
	fun displaysLoadingState() {
		setContent(
			uiState = createLibraryUiState(
				isLoading = true,
				books = emptyList()
			)
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Library.LOADING_TEXT)
			.assertIsDisplayed()
	}

	@Test
	fun clickingAddBookFabCallsCallback() {
		var clicked = false

		setContent(
			onAddBookClick = {
				clicked = true
			}
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Library.ADD_BOOK_FAB)
			.performClick()

		assertTrue(clicked)
	}

	@Test
	fun clickingBookCardCallsCallbackWithBookId() {
		var clickedBookId: String? = null

		setContent(
			uiState = createLibraryUiState(
				books = listOf(
					createBook(id = BOOK_ID, title = BOOK_TITLE)
				)
			),
			onBookClick = { bookId ->
				clickedBookId = bookId
			}
		)

		composeRule.onNodeWithTag(
			ReadingTrackerTestTags.Library.bookCard(BOOK_ID)
		).performClick()

		assertEquals(BOOK_ID, clickedBookId)
	}

	@Test
	fun typingInSearchFieldCallsCallback() {
		var query = ""

		setContent(
			onSearchQueryChanged = { newQuery ->
				query = newQuery
			}
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Library.SEARCH_FIELD)
			.performTextInput("dune")

		assertEquals("dune", query)
	}

	@Test
	fun clickingReadingFilterCallsCallback() {
		var selectedFilter: LibraryFilter? = null

		setContent(
			onFilterSelected = { filter ->
				selectedFilter = filter
			}
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Library.FILTER_READING)
			.performClick()

		assertEquals(LibraryFilter.READING, selectedFilter)
	}

	private fun setContent(
		uiState: LibraryUiState = createLibraryUiState(),
		onBookClick: (String) -> Unit = {},
		onAddBookClick: () -> Unit = {},
		onSearchQueryChanged: (String) -> Unit = {},
		onFilterSelected: (LibraryFilter) -> Unit = {}
	) {
		composeRule.setContent {
			ReadingTrackerTheme(themePreference = ThemePreference.LIGHT) {
				LibraryScreen(
					uiState = uiState,
					onBookClick = onBookClick,
					onAddBookClick = onAddBookClick,
					onSearchQueryChanged = onSearchQueryChanged,
					onFilterSelected = onFilterSelected
				)
			}
		}
	}

	private fun createLibraryUiState(
		isLoading: Boolean = false,
		books: List<Book> = emptyList(),
		searchQuery: String = "",
		selectedFilter: LibraryFilter = LibraryFilter.ALL
	): LibraryUiState {
		return LibraryUiState(
			isLoading = isLoading,
			books = books,
			searchQuery = searchQuery,
			selectedFilter = selectedFilter
		)
	}
}