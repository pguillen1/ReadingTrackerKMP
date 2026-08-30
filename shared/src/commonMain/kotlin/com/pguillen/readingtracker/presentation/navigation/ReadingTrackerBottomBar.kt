package com.pguillen.readingtracker.presentation.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.pguillen.readingtracker.presentation.testtag.ReadingTrackerTestTags
import com.pguillen.readingtracker.presentation.theme.ReadingTrackerColors

@Composable
fun ReadingTrackerBottomBar(
	currentRoute: String?,
	onDestinationClick: (String) -> Unit
) {
	NavigationBar(
		containerColor = ReadingTrackerColors.card
	) {
		bottomNavDestinations.forEach { destination ->
			val selected = currentRoute == destination.route
			val tag = when(destination.label) {
				"Library" -> ReadingTrackerTestTags.NavigationBar.LIBRARY
				"Stats" -> ReadingTrackerTestTags.NavigationBar.STATS
				"Settings" -> ReadingTrackerTestTags.NavigationBar.SETTINGS
				else -> ""
			}

			NavigationBarItem(
				modifier = Modifier.testTag(tag),
				selected = selected,
				onClick = {
					onDestinationClick(destination.route)
				},
				icon = {
					Icon(
						imageVector = destination.icon,
						contentDescription = destination.label
					)
				},
				label = {
					Text(destination.label)
				},
				colors = NavigationBarItemDefaults.colors(
					selectedIconColor = ReadingTrackerColors.primaryGreen,
					selectedTextColor = ReadingTrackerColors.primaryGreen,
					indicatorColor = ReadingTrackerColors.chipSelected,
					unselectedIconColor = ReadingTrackerColors.textSecondary,
					unselectedTextColor = ReadingTrackerColors.textSecondary
				)
			)
		}
	}
}