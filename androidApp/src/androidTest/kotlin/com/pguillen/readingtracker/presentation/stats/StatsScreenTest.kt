package com.pguillen.readingtracker.presentation.stats

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.pguillen.readingtracker.domain.model.ThemePreference
import com.pguillen.readingtracker.presentation.testtag.ReadingTrackerTestTags
import com.pguillen.readingtracker.presentation.theme.ReadingTrackerTheme
import kotlinx.datetime.LocalDate
import org.junit.Rule
import org.junit.Test

class StatsScreenTest {

	@get:Rule
	val composeRule = createComposeRule()

	@Test
	fun displaysMainElements() {
		setContent()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Stats.SCREEN)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Stats.SCREEN_TITLE)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Stats.TOTAL_BOOKS_CARD)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Stats.FINISHED_CARD)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Stats.READING_CARD)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Stats.SESSIONS_CARD)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Stats.PAGES_READ_CARD)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Stats.MINUTES_CARD)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Stats.RECENT_ACTIVITY_CARD)
			.assertIsDisplayed()
	}

	@Test
	fun displaysStatsValues() {
		setContent()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Stats.TOTAL_BOOKS_CARD)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Stats.FINISHED_CARD)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Stats.READING_CARD)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Stats.SESSIONS_CARD)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Stats.PAGES_READ_CARD)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Stats.MINUTES_CARD)
			.assertIsDisplayed()
	}

	@Test
	fun displaysRecentActivityCardWhenThereIsActivity() {
		setContent()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Stats.RECENT_ACTIVITY_CARD)
			.assertIsDisplayed()
	}

	private fun setContent(
		uiState: StatsUiState = createStatsUiState()
	) {
		composeRule.setContent {
			ReadingTrackerTheme(themePreference = ThemePreference.LIGHT) {
				StatsScreen(
					uiState = uiState
				)
			}
		}
	}

	private fun createStatsUiState(
		isLoading: Boolean = false,
		totalBooks: Int = 3,
		readingBooks: Int = 1,
		finishedBooks: Int = 1,
		totalSessions: Int = 4,
		totalPagesRead: Int = 120,
		totalMinutesRead: Int = 180,
		recentDailySummaries: List<DailyReadingSummary> = listOf(
			DailyReadingSummary(
				date = LocalDate(2026, 1, 15),
				pagesRead = 25,
				minutesRead = 40,
				sessionsCount = 1
			)
		)
	): StatsUiState {
		return StatsUiState(
			isLoading = isLoading,
			totalBooks = totalBooks,
			readingBooks = readingBooks,
			finishedBooks = finishedBooks,
			totalSessions = totalSessions,
			totalPagesRead = totalPagesRead,
			totalMinutesRead = totalMinutesRead,
			recentDailySummaries = recentDailySummaries
		)
	}
}