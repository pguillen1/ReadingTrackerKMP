package com.pguillen.readingtracker.presentation.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pguillen.readingtracker.presentation.theme.ReadingTrackerColors
import kotlinx.datetime.number
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StatsRoute(
	viewModel: StatsViewModel = koinViewModel()
) {
	val uiState by viewModel.uiState.collectAsState()

	StatsScreen(
		uiState = uiState
	)
}

@Composable
fun StatsScreen(
	uiState: StatsUiState
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
				StatsHeader()
			}

			item {
				StatsCardsGrid(uiState = uiState)
			}

			item {
				RecentActivityCard(
					summaries = uiState.recentDailySummaries
				)
			}
		}
	}
}

@Composable
private fun StatsHeader() {
	Text(
		text = "Stats",
		style = MaterialTheme.typography.headlineMedium,
		fontWeight = FontWeight.Bold,
		color = ReadingTrackerColors.textPrimary
	)
}

@Composable
private fun StatsCardsGrid(
	uiState: StatsUiState
) {
	FlowRow(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(10.dp),
		verticalArrangement = Arrangement.spacedBy(10.dp),
		maxItemsInEachRow = 3
	) {
		StatCard(
			title = "Total books",
			value = uiState.totalBooks.toString(),
			icon = Icons.AutoMirrored.Outlined.MenuBook,
			modifier = Modifier.weight(1f)
		)

		StatCard(
			title = "Finished",
			value = uiState.finishedBooks.toString(),
			icon = Icons.Outlined.CheckCircle,
			modifier = Modifier.weight(1f)
		)

		StatCard(
			title = "Reading",
			value = uiState.readingBooks.toString(),
			icon = Icons.Outlined.AutoStories,
			modifier = Modifier.weight(1f)
		)

		StatCard(
			title = "Sessions",
			value = uiState.totalSessions.toString(),
			icon = Icons.Outlined.Schedule,
			modifier = Modifier.weight(1f)
		)

		StatCard(
			title = "Pages read",
			value = uiState.totalPagesRead.toString(),
			icon = Icons.AutoMirrored.Outlined.TrendingUp,
			modifier = Modifier.weight(1f)
		)

		StatCard(
			title = "Minutes",
			value = uiState.totalMinutesRead.toString(),
			icon = Icons.Outlined.Timer,
			modifier = Modifier.weight(1f)
		)
	}
}

@Composable
private fun StatCard(
	title: String,
	value: String,
	icon: ImageVector,
	modifier: Modifier = Modifier
) {
	Card(
		modifier = modifier
			.defaultMinSize(minHeight = 108.dp),
		shape = RoundedCornerShape(22.dp),
		colors = CardDefaults.cardColors(
			containerColor = ReadingTrackerColors.card
		),
		elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
	) {
		Column(
			modifier = Modifier.padding(14.dp),
			verticalArrangement = Arrangement.SpaceBetween
		) {
			Box(
				modifier = Modifier
					.height(34.dp)
					.width(34.dp)
					.clip(CircleShape)
					.background(ReadingTrackerColors.chipSelected),
				contentAlignment = Alignment.Center
			) {
				Icon(
					imageVector = icon,
					contentDescription = null,
					tint = ReadingTrackerColors.primaryGreen
				)
			}

			Spacer(modifier = Modifier.height(14.dp))

			Text(
				text = value,
				style = MaterialTheme.typography.headlineSmall,
				fontWeight = FontWeight.Bold,
				color = ReadingTrackerColors.textPrimary
			)

			Text(
				text = title,
				style = MaterialTheme.typography.bodySmall,
				color = ReadingTrackerColors.textSecondary
			)
		}
	}
}

@Composable
private fun RecentActivityCard(
	summaries: List<DailyReadingSummary>
) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		shape = RoundedCornerShape(26.dp),
		colors = CardDefaults.cardColors(
			containerColor = ReadingTrackerColors.card
		),
		elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
	) {
		Column(
			modifier = Modifier.padding(18.dp)
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				verticalAlignment = Alignment.Bottom
			) {
				Column(
					modifier = Modifier.weight(1f)
				) {
					Text(
						text = "Recent activity",
						style = MaterialTheme.typography.titleMedium,
						fontWeight = FontWeight.SemiBold,
						color = ReadingTrackerColors.textPrimary
					)

					Text(
						text = "Pages read in recent sessions",
						style = MaterialTheme.typography.bodySmall,
						color = ReadingTrackerColors.textSecondary
					)
				}

				Text(
					text = "${summaries.sumOf { it.pagesRead }} pages",
					style = MaterialTheme.typography.labelLarge,
					color = ReadingTrackerColors.primaryGreen,
					fontWeight = FontWeight.SemiBold
				)
			}

			Spacer(modifier = Modifier.height(24.dp))

			if (summaries.isEmpty()) {
				EmptyActivityState()
			}
			else {
				ActivityBars(
					summaries = summaries
				)
			}
		}
	}
}

@Composable
private fun ActivityBars(
	summaries: List<DailyReadingSummary>
) {
	val maxPages = summaries.maxOfOrNull { it.pagesRead }?.takeIf { it > 0 } ?: 1

	Row(
		modifier = Modifier
			.fillMaxWidth()
			.height(150.dp),
		horizontalArrangement = Arrangement.SpaceEvenly,
		verticalAlignment = Alignment.Bottom
	) {
		summaries.forEach { summary ->
			val barHeightFraction = summary.pagesRead.toFloat() / maxPages.toFloat()

			ActivityBarItem(
				label = formatDayLabel(summary),
				value = summary.pagesRead,
				heightFraction = barHeightFraction
			)
		}
	}
}

@Composable
private fun ActivityBarItem(
	label: String,
	value: Int,
	heightFraction: Float
) {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Bottom
	) {
		Text(
			text = value.toString(),
			style = MaterialTheme.typography.labelSmall,
			color = ReadingTrackerColors.textSecondary
		)

		Spacer(modifier = Modifier.height(6.dp))

		Box(
			modifier = Modifier
				.width(24.dp)
				.height((96.dp * heightFraction.coerceIn(0.1f, 1f)))
				.clip(RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp))
				.background(ReadingTrackerColors.primaryGreen)
		)

		Spacer(modifier = Modifier.height(8.dp))

		Text(
			text = label,
			style = MaterialTheme.typography.labelSmall,
			color = ReadingTrackerColors.textSecondary
		)
	}
}

@Composable
private fun EmptyActivityState() {
	Box(
		modifier = Modifier
			.fillMaxWidth()
			.height(120.dp),
		contentAlignment = Alignment.Center
	) {
		Text(
			text = "No reading activity yet",
			style = MaterialTheme.typography.bodyMedium,
			color = ReadingTrackerColors.textSecondary
		)
	}
}

private fun formatDayLabel(summary: DailyReadingSummary): String {
	return "${summary.date.dayOfMonth}/${summary.date.month.number}"
}