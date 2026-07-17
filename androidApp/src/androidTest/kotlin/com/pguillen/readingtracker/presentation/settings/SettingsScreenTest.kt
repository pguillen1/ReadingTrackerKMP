package com.pguillen.readingtracker.presentation.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.pguillen.readingtracker.domain.model.BookSortOption
import com.pguillen.readingtracker.domain.model.ThemePreference
import com.pguillen.readingtracker.presentation.testtag.ReadingTrackerTestTags
import com.pguillen.readingtracker.presentation.theme.ReadingTrackerTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class SettingsScreenTest {

	@get:Rule
	val composeRule = createComposeRule()

	@Test
	fun displaysMainElements() {
		setContent(
			uiState = createSettingsUiState()
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Settings.SCREEN)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Settings.SCREEN_TITLE)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Settings.THEME_CARD)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Settings.THEME_SYSTEM)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Settings.THEME_LIGHT)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Settings.THEME_DARK)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Settings.DEFAULT_SORT_CARD)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Settings.SORT_BY_UPDATED)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Settings.SORT_BY_ADDED)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Settings.SORT_BY_TITLE)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Settings.SORT_BY_AUTHOR)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Settings.SORT_BY_PROGRESS)
			.assertIsDisplayed()

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Settings.PRIVACY_POLICY)
			.assertIsDisplayed()
	}

	@Test
	fun clickingThemeCardCallsCallback() {
		var themeSelected: ThemePreference? = null

		setContent(
			onThemePreferenceChanged = {
				themeSelected = it
			}
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Settings.THEME_DARK)
			.performClick()

		assertEquals(ThemePreference.DARK, themeSelected)
	}

	@Test
	fun clickingDefaultSortCardCallsCallback() {
		var sortSelected: BookSortOption? = null

		setContent(
			onDefaultSortOptionChanged = {
				sortSelected = it
			}
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Settings.SORT_BY_TITLE)
			.performClick()

		assertEquals(BookSortOption.TITLE, sortSelected)
	}

	@Test
	fun clickingPrivacyPolicyCardCallsCallback() {
		var privacyPolicyClicked = false

		setContent(
			onPrivacyPolicyClicked = {
				privacyPolicyClicked = true
			}
		)

		composeRule.onNodeWithTag(ReadingTrackerTestTags.Settings.PRIVACY_POLICY)
			.performClick()

		assertTrue(privacyPolicyClicked)
	}

	private fun setContent(
		uiState: SettingsUiState = createSettingsUiState(),
		onThemePreferenceChanged: (ThemePreference) -> Unit = {},
		onDefaultSortOptionChanged: (BookSortOption) -> Unit = {},
		onPrivacyPolicyClicked: () -> Unit = {}
	) {
		composeRule.setContent {
			ReadingTrackerTheme(themePreference = ThemePreference.LIGHT) {
				SettingsScreen(
					uiState = uiState,
					onThemePreferenceChanged = onThemePreferenceChanged,
					onDefaultSortOptionChanged = onDefaultSortOptionChanged,
					onPrivacyPolicyClicked = onPrivacyPolicyClicked
				)
			}
		}
	}

	private fun createSettingsUiState(
		isLoading: Boolean = false,
		themePreference: ThemePreference = ThemePreference.SYSTEM,
		defaultSortOption: BookSortOption = BookSortOption.RECENTLY_UPDATED
	): SettingsUiState {
		return SettingsUiState(
			isLoading = isLoading,
			themePreference = themePreference,
			defaultSortOption = defaultSortOption
		)
	}
}