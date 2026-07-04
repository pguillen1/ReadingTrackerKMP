package com.pguillen.readingtracker.presentation.booksessions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pguillen.readingtracker.domain.model.ReadingSession
import com.pguillen.readingtracker.presentation.testtag.ReadingTrackerTestTags.BookSessions
import com.pguillen.readingtracker.presentation.theme.ReadingTrackerColors
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun BookSessionsRoute(
	bookId: String,
	onNavigateBack: () -> Unit,
	onAddSessionClick: (String) -> Unit,
	onEditSessionClick: (String) -> Unit,
	viewModel: BookSessionsViewModel = koinViewModel {
		parametersOf(bookId)
	}
) {
	val uiState by viewModel.uiState.collectAsState()

	BookSessionsScreen(
		uiState = uiState,
		onNavigateBack = onNavigateBack,
		onAddSessionClick = { onAddSessionClick(bookId) },
		onSessionClick = onEditSessionClick,
		onDeleteSessionClick = viewModel::onDeleteSessionClick,
		onDismissDeleteDialog = viewModel::onDismissDeleteDialog,
		onConfirmDeleteSession = viewModel::onConfirmDeleteSession
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookSessionsScreen(
	uiState: BookSessionsUiState,
	onNavigateBack: () -> Unit,
	onAddSessionClick: () -> Unit,
	onSessionClick: (String) -> Unit,
	onDeleteSessionClick: (ReadingSession) -> Unit,
	onDismissDeleteDialog: () -> Unit,
	onConfirmDeleteSession: () -> Unit
) {
	Scaffold(
		containerColor = ReadingTrackerColors.background,
		topBar = {
			TopAppBar(
				title = {
					Column {
						Text(
							modifier = Modifier.testTag(BookSessions.SCREEN_TITLE),
							text = "Reading sessions",
							fontWeight = FontWeight.SemiBold,
							color = ReadingTrackerColors.textPrimary
						)

						if (uiState.bookTitle.isNotBlank()) {
							Text(
								text = uiState.bookTitle,
								style = MaterialTheme.typography.bodySmall,
								color = ReadingTrackerColors.textSecondary,
								maxLines = 1,
								overflow = TextOverflow.Ellipsis
							)
						}
					}
				},
				navigationIcon = {
					IconButton(
						modifier = Modifier.testTag(BookSessions.BACK_BUTTON),
						onClick = onNavigateBack
					) {
						Icon(
							imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
							contentDescription = "Back",
							tint = ReadingTrackerColors.textPrimary
						)
					}
				},
				colors = TopAppBarDefaults.topAppBarColors(
					containerColor = ReadingTrackerColors.background
				)
			)
		},
		floatingActionButton = {
			FloatingActionButton(
				modifier = Modifier.testTag(BookSessions.ADD_BUTTON),
				onClick = onAddSessionClick,
				containerColor = ReadingTrackerColors.primaryGreen,
				contentColor = ReadingTrackerColors.onPrimary
			) {
				Icon(
					imageVector = Icons.Outlined.Add,
					contentDescription = "Add session"
				)
			}
		}
	) { innerPadding ->
		Box(
			modifier = Modifier
				.fillMaxSize()
				.background(ReadingTrackerColors.background)
				.padding(innerPadding)
				.testTag(BookSessions.SCREEN)
		) {
			when {
				uiState.isLoading -> {
					Text(
						text = "Loading sessions...",
						color = ReadingTrackerColors.textSecondary,
						modifier = Modifier.align(Alignment.Center)
							.testTag(BookSessions.LOADING_TEXT)
					)
				}

				uiState.errorMessage != null && uiState.sessions.isEmpty() -> {
					Text(
						text = uiState.errorMessage,
						color = ReadingTrackerColors.textSecondary,
						modifier = Modifier.align(Alignment.Center)
					)
				}

				uiState.isEmpty -> {
					EmptySessionsState(
						modifier = Modifier.align(Alignment.Center)
							.testTag(BookSessions.EMPTY_SCREEN)
					)
				}

				else -> {
					SessionsList(
						sessions = uiState.sessions,
						onSessionClick = onSessionClick,
						onDeleteSessionClick = onDeleteSessionClick
					)
				}
			}
		}
	}

	val sessionPendingDelete = uiState.sessionPendingDelete
	if (sessionPendingDelete != null) {
		DeleteSessionDialog(
			session = sessionPendingDelete,
			onDismiss = onDismissDeleteDialog,
			onConfirm = onConfirmDeleteSession
		)
	}
}

@Composable
private fun SessionsList(
	sessions: List<ReadingSession>,
	onSessionClick: (String) -> Unit,
	onDeleteSessionClick: (ReadingSession) -> Unit
) {
	LazyColumn(
		modifier = Modifier.fillMaxSize().testTag(BookSessions.SESSIONS_LIST),
		contentPadding = PaddingValues(
			start = 20.dp,
			end = 20.dp,
			top = 12.dp,
			bottom = 96.dp
		),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		items(
			items = sessions,
			key = { it.id }
		) { session ->
			SessionCard(
				session = session,
				onClick = {
					onSessionClick(session.id)
				},
				onDeleteClick = {
					onDeleteSessionClick(session)
				}
			)
		}
	}
}

@Composable
private fun SessionCard(
	session: ReadingSession,
	onClick: () -> Unit,
	onDeleteClick: () -> Unit
) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.clickable(onClick = onClick)
			.testTag(BookSessions.sessionCard(session.id)),
		shape = RoundedCornerShape(24.dp),
		colors = CardDefaults.cardColors(
			containerColor = ReadingTrackerColors.card
		),
		elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
			verticalAlignment = Alignment.Top
		) {
			SessionIcon()

			Spacer(modifier = Modifier.padding(horizontal = 6.dp))

			Column(
				modifier = Modifier.weight(1f)
			) {
				Text(
					text = formatDate(session),
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.SemiBold,
					color = ReadingTrackerColors.textPrimary
				)

				Spacer(modifier = Modifier.height(4.dp))

				Text(
					text = sessionProgressText(session),
					style = MaterialTheme.typography.bodyMedium,
					color = ReadingTrackerColors.textSecondary
				)

				if (session.minutes != null) {
					Spacer(modifier = Modifier.height(4.dp))

					Text(
						text = "${session.minutes} minutes",
						style = MaterialTheme.typography.bodySmall,
						color = ReadingTrackerColors.primaryGreen,
						fontWeight = FontWeight.SemiBold
					)
				}

				if (!session.note.isNullOrBlank()) {
					Spacer(modifier = Modifier.height(8.dp))

					Text(
						text = session.note,
						style = MaterialTheme.typography.bodySmall,
						color = ReadingTrackerColors.textSecondary,
						maxLines = 2,
						overflow = TextOverflow.Ellipsis
					)
				}
			}

			IconButton(
				onClick = onDeleteClick
			) {
				Icon(
					imageVector = Icons.Outlined.Delete,
					contentDescription = "Delete session",
					tint = ReadingTrackerColors.textSecondary
				)
			}
		}
	}
}

@Composable
private fun SessionIcon() {
	Surface(
		shape = CircleShape,
		color = ReadingTrackerColors.chipSelected
	) {
		Icon(
			imageVector = Icons.Outlined.Timer,
			contentDescription = null,
			tint = ReadingTrackerColors.primaryGreen,
			modifier = Modifier.padding(10.dp)
		)
	}
}

@Composable
private fun EmptySessionsState(
	modifier: Modifier = Modifier
) {
	Column(
		modifier = modifier.padding(horizontal = 32.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Icon(
			imageVector = Icons.AutoMirrored.Outlined.MenuBook,
			contentDescription = null,
			tint = ReadingTrackerColors.primaryGreen
		)

		Spacer(modifier = Modifier.height(12.dp))

		Text(
			text = "No sessions yet",
			style = MaterialTheme.typography.titleMedium,
			fontWeight = FontWeight.SemiBold,
			color = ReadingTrackerColors.textPrimary
		)

		Spacer(modifier = Modifier.height(4.dp))

		Text(
			text = "Log your first reading session using the + button.",
			style = MaterialTheme.typography.bodyMedium,
			color = ReadingTrackerColors.textSecondary
		)
	}
}

@Composable
private fun DeleteSessionDialog(
	session: ReadingSession,
	onDismiss: () -> Unit,
	onConfirm: () -> Unit
) {
	AlertDialog(
		onDismissRequest = onDismiss,
		title = {
			Text("Delete session?")
		},
		text = {
			Text(
				text = "This will delete the session from ${formatDate(session)}. Your book progress will not be recalculated in this version."
			)
		},
		confirmButton = {
			TextButton(
				onClick = onConfirm
			) {
				Text(
					text = "Delete",
					color = MaterialTheme.colorScheme.error
				)
			}
		},
		dismissButton = {
			TextButton(
				onClick = onDismiss
			) {
				Text("Cancel")
			}
		},
		containerColor = ReadingTrackerColors.card
	)
}

private fun formatDate(session: ReadingSession): String {
	return "${session.date.dayOfMonth}/${session.date.monthNumber}/${session.date.year}"
}

private fun sessionProgressText(session: ReadingSession): String {
	val startPage = session.startPage

	return if (startPage != null) {
		"Pages $startPage - ${session.endPage}"
	}
	else {
		"Until page ${session.endPage}"
	}
}