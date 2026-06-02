package com.pguillen.readingtracker.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pguillen.readingtracker.domain.model.BookSortOption
import com.pguillen.readingtracker.domain.model.LibraryViewMode
import com.pguillen.readingtracker.domain.model.ThemePreference
import com.pguillen.readingtracker.presentation.theme.ReadingTrackerColors
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsRoute(
	viewModel: SettingsViewModel = koinViewModel()
) {
	val uiState by viewModel.uiState.collectAsState()

	SettingsScreen(
		uiState = uiState,
		onThemePreferenceChanged = viewModel::onThemePreferenceChanged,
		onLibraryViewModeChanged = viewModel::onLibraryViewModeChanged,
		onDefaultSortOptionChanged = viewModel::onDefaultSortOptionChanged
	)
}

@Composable
fun SettingsScreen(
	uiState: SettingsUiState,
	onThemePreferenceChanged: (ThemePreference) -> Unit,
	onLibraryViewModeChanged: (LibraryViewMode) -> Unit,
	onDefaultSortOptionChanged: (BookSortOption) -> Unit
) {
	Scaffold(
		containerColor = ReadingTrackerColors.background
	) { innerPadding ->
		LazyColumn(
			modifier = Modifier
				.fillMaxSize()
				.background(ReadingTrackerColors.background)
				.padding(innerPadding),
			contentPadding = PaddingValues(
				start = 20.dp,
				end = 20.dp,
				top = 16.dp,
				bottom = 96.dp
			),
			verticalArrangement = Arrangement.spacedBy(18.dp)
		) {
			item {
				SettingsHeader()
			}

			item {
				SettingsSectionTitle("Preferences")
			}

			item {
				SettingsCard {
					SettingsOptionHeader(
						icon = Icons.Outlined.DarkMode,
						title = "Theme",
						subtitle = "Choose app theme"
					)

					Spacer(modifier = Modifier.height(14.dp))

					ThemePreferenceSelector(
						selected = uiState.themePreference,
						onSelected = onThemePreferenceChanged
					)
				}
			}

			item {
				SettingsCard {
					SettingsOptionHeader(
						icon = Icons.Outlined.GridView,
						title = "Library view",
						subtitle = "Choose how books are displayed"
					)

					Spacer(modifier = Modifier.height(14.dp))

					LibraryViewModeSelector(
						selected = uiState.libraryViewMode,
						onSelected = onLibraryViewModeChanged
					)
				}
			}

			item {
				SettingsCard {
					SettingsOptionHeader(
						icon = Icons.Outlined.Sort,
						title = "Default sort",
						subtitle = "Choose default book sorting"
					)

					Spacer(modifier = Modifier.height(14.dp))

					SortOptionSelector(
						selected = uiState.defaultSortOption,
						onSelected = onDefaultSortOptionChanged
					)
				}
			}

			item {
				SettingsSectionTitle("More")
			}

			item {
				SettingsCard {
					SettingsOptionHeader(
						icon = Icons.Outlined.Info,
						title = "About Reading Tracker",
						subtitle = "Version 1.0.0"
					)
				}
			}
		}
	}
}

@Composable
private fun SettingsHeader() {
	Text(
		text = "Settings",
		style = MaterialTheme.typography.headlineMedium,
		fontWeight = FontWeight.Bold,
		color = ReadingTrackerColors.textPrimary
	)
}

@Composable
private fun SettingsSectionTitle(
	title: String
) {
	Text(
		text = title,
		style = MaterialTheme.typography.labelLarge,
		fontWeight = FontWeight.SemiBold,
		color = ReadingTrackerColors.primaryGreen,
		modifier = Modifier.padding(top = 4.dp)
	)
}

@Composable
private fun SettingsCard(
	content: @Composable ColumnScope.() -> Unit
) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		shape = RoundedCornerShape(24.dp),
		colors = CardDefaults.cardColors(
			containerColor = ReadingTrackerColors.card
		),
		elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
	) {
		Column(
			modifier = Modifier.padding(16.dp),
			content = content
		)
	}
}

@Composable
private fun SettingsOptionHeader(
	icon: ImageVector,
	title: String,
	subtitle: String
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		verticalAlignment = Alignment.CenterVertically
	) {
		Surface(
			shape = CircleShape,
			color = ReadingTrackerColors.chipSelected
		) {
			Icon(
				imageVector = icon,
				contentDescription = null,
				tint = ReadingTrackerColors.primaryGreen,
				modifier = Modifier.padding(8.dp)
			)
		}

		Spacer(modifier = Modifier.width(12.dp))

		Column {
			Text(
				text = title,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.SemiBold,
				color = ReadingTrackerColors.textPrimary
			)

			Text(
				text = subtitle,
				style = MaterialTheme.typography.bodySmall,
				color = ReadingTrackerColors.textSecondary
			)
		}
	}
}

@Composable
private fun ThemePreferenceSelector(
	selected: ThemePreference,
	onSelected: (ThemePreference) -> Unit
) {
	Row(
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		SettingsChip(
			text = "System",
			selected = selected == ThemePreference.SYSTEM,
			onClick = { onSelected(ThemePreference.SYSTEM) }
		)

		SettingsChip(
			text = "Light",
			selected = selected == ThemePreference.LIGHT,
			onClick = { onSelected(ThemePreference.LIGHT) }
		)

		SettingsChip(
			text = "Dark",
			selected = selected == ThemePreference.DARK,
			onClick = { onSelected(ThemePreference.DARK) }
		)
	}
}

@Composable
private fun LibraryViewModeSelector(
	selected: LibraryViewMode,
	onSelected: (LibraryViewMode) -> Unit
) {
	Row(
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		SettingsChip(
			text = "List",
			selected = selected == LibraryViewMode.LIST,
			onClick = { onSelected(LibraryViewMode.LIST) }
		)

		SettingsChip(
			text = "Grid",
			selected = selected == LibraryViewMode.GRID,
			onClick = { onSelected(LibraryViewMode.GRID) }
		)
	}
}

@Composable
private fun SortOptionSelector(
	selected: BookSortOption,
	onSelected: (BookSortOption) -> Unit
) {
	Column(
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		FlowRow(
			horizontalArrangement = Arrangement.spacedBy(8.dp)
		) {
			BookSortOption.entries.forEach { option ->
				val text = when (option) {
					BookSortOption.RECENTLY_UPDATED -> "Updated"
					BookSortOption.RECENTLY_ADDED -> "Added"
					BookSortOption.TITLE -> "Title"
					BookSortOption.AUTHOR -> "Author"
					BookSortOption.PROGRESS -> "Progress"
				}
				SettingsChip(
					text = text,
					selected = selected == option,
					onClick = { onSelected(option) }
				)
			}
		}
	}
}

@Composable
private fun SettingsChip(
	text: String,
	selected: Boolean,
	onClick: () -> Unit
) {
	FilterChip(
		selected = selected,
		onClick = onClick,
		label = {
			Text(text = text)
		},
		colors = FilterChipDefaults.filterChipColors(
			selectedContainerColor = ReadingTrackerColors.chipSelected,
			selectedLabelColor = ReadingTrackerColors.textPrimary,
			containerColor = ReadingTrackerColors.surfaceSoft,
			labelColor = ReadingTrackerColors.textPrimary
		),
		border = null
	)
}