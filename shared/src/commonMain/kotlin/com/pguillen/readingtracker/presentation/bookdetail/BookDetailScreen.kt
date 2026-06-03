package com.pguillen.readingtracker.presentation.bookdetail

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.NoteAlt
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pguillen.readingtracker.domain.model.Book
import com.pguillen.readingtracker.domain.model.BookNote
import com.pguillen.readingtracker.domain.model.BookNoteType
import com.pguillen.readingtracker.domain.model.ReadingSession
import com.pguillen.readingtracker.domain.model.ReadingStatus
import com.pguillen.readingtracker.presentation.theme.ReadingTrackerColors
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun BookDetailRoute(
	bookId: String,
	onNavigateBack: () -> Unit,
	onEditBookClick: (String) -> Unit,
	onLogSessionClick: (String) -> Unit,
	onAddNoteClick: (String) -> Unit,
	viewModel: BookDetailViewModel = koinViewModel {
		parametersOf(bookId)
	}
) {
	val uiState by viewModel.uiState.collectAsState()

	BookDetailScreen(
		uiState = uiState,
		onNavigateBack = onNavigateBack,
		onEditBookClick = onEditBookClick,
		onLogSessionClick = onLogSessionClick,
		onAddNoteClick = onAddNoteClick
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
	uiState: BookDetailUiState,
	onNavigateBack: () -> Unit,
	onEditBookClick: (String) -> Unit,
	onLogSessionClick: (String) -> Unit,
	onAddNoteClick: (String) -> Unit
) {
	Scaffold(
		containerColor = ReadingTrackerColors.background,
		topBar = {
			TopAppBar(
				title = {
					Text(
						text = "Book details",
						fontWeight = FontWeight.SemiBold,
						color = ReadingTrackerColors.textPrimary
					)
				},
				navigationIcon = {
					IconButton(onClick = onNavigateBack) {
						Icon(
							imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
							contentDescription = "Back",
							tint = ReadingTrackerColors.textPrimary
						)
					}
				},
				actions = {
					val bookId = uiState.book?.id
					if (bookId != null) {
						IconButton(
							onClick = {
								onEditBookClick(bookId)
							}
						) {
							Icon(
								imageVector = Icons.Outlined.Edit,
								contentDescription = "Edit book",
								tint = ReadingTrackerColors.textPrimary
							)
						}
					}
				},
				colors = TopAppBarDefaults.topAppBarColors(
					containerColor = ReadingTrackerColors.background
				)
			)
		}
	) { innerPadding ->
		when {
			uiState.isLoading -> {
				Box(
					modifier = Modifier
						.fillMaxSize()
						.background(ReadingTrackerColors.background)
						.padding(innerPadding),
					contentAlignment = Alignment.Center
				) {
					Text(
						text = "Loading book...",
						color = ReadingTrackerColors.textSecondary
					)
				}
			}

			uiState.book == null -> {
				Box(
					modifier = Modifier
						.fillMaxSize()
						.background(ReadingTrackerColors.background)
						.padding(innerPadding),
					contentAlignment = Alignment.Center
				) {
					Text(
						text = uiState.errorMessage ?: "Book not found",
						color = ReadingTrackerColors.textSecondary
					)
				}
			}

			else -> {
				BookDetailContent(
					book = uiState.book,
					recentSessions = uiState.recentSessions,
					recentNotes = uiState.recentNotes,
					onLogSessionClick = onLogSessionClick,
					onAddNoteClick = onAddNoteClick,
					modifier = Modifier.padding(innerPadding)
				)
			}
		}
	}
}

@Composable
private fun BookDetailContent(
	book: Book,
	recentSessions: List<ReadingSession>,
	recentNotes: List<BookNote>,
	onLogSessionClick: (String) -> Unit,
	onAddNoteClick: (String) -> Unit,
	modifier: Modifier = Modifier
) {
	LazyColumn(
		modifier = modifier
			.fillMaxSize()
			.background(ReadingTrackerColors.background),
		contentPadding = PaddingValues(
			start = 20.dp,
			end = 20.dp,
			top = 12.dp,
			bottom = 28.dp
		),
		verticalArrangement = Arrangement.spacedBy(18.dp)
	) {
		item {
			BookHeroCard(book = book)
		}

		item {
			BookActionButtons(
				bookId = book.id,
				onLogSessionClick = onLogSessionClick,
				onAddNoteClick = onAddNoteClick
			)
		}

		item {
			RecentSessionsCard(
				sessions = recentSessions
			)
		}

		item {
			RecentNotesCard(
				notes = recentNotes
			)
		}
	}
}

@Composable
private fun BookHeroCard(
	book: Book
) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		shape = RoundedCornerShape(28.dp),
		colors = CardDefaults.cardColors(
			containerColor = ReadingTrackerColors.card
		),
		elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
	) {
		Column(
			modifier = Modifier.padding(18.dp)
		) {
			Row(
				verticalAlignment = Alignment.Top
			) {
				BookCoverLarge()

				Spacer(modifier = Modifier.width(16.dp))

				Column(
					modifier = Modifier.weight(1f)
				) {
					Text(
						text = book.title,
						style = MaterialTheme.typography.headlineSmall,
						fontWeight = FontWeight.Bold,
						color = ReadingTrackerColors.textPrimary,
						maxLines = 2,
						overflow = TextOverflow.Ellipsis
					)

					Spacer(modifier = Modifier.height(4.dp))

					Text(
						text = book.author.ifBlank { "Unknown author" },
						style = MaterialTheme.typography.bodyMedium,
						color = ReadingTrackerColors.textSecondary,
						maxLines = 1,
						overflow = TextOverflow.Ellipsis
					)

					Spacer(modifier = Modifier.height(12.dp))

					StatusBadge(status = book.status)
				}
			}

			Spacer(modifier = Modifier.height(22.dp))

			Text(
				text = "Progress",
				style = MaterialTheme.typography.labelLarge,
				fontWeight = FontWeight.SemiBold,
				color = ReadingTrackerColors.textPrimary
			)

			Spacer(modifier = Modifier.height(8.dp))

			BookProgressBar(
				progress = book.progressPercentage
			)

			Spacer(modifier = Modifier.height(8.dp))

			Text(
				text = progressText(book),
				style = MaterialTheme.typography.bodySmall,
				color = ReadingTrackerColors.textSecondary
			)
		}
	}
}

@Composable
private fun BookCoverLarge() {
	Box(
		modifier = Modifier
			.width(86.dp)
			.height(126.dp)
			.clip(RoundedCornerShape(18.dp))
			.background(ReadingTrackerColors.cover),
		contentAlignment = Alignment.Center
	) {
		Icon(
			imageVector = Icons.Outlined.Book,
			contentDescription = null,
			tint = ReadingTrackerColors.primaryGreen
		)
	}
}

@Composable
private fun BookProgressBar(
	progress: Float
) {
	val safeProgress = progress.coerceIn(0f, 1f)

	Box(
		modifier = Modifier
			.fillMaxWidth()
			.height(7.dp)
			.clip(CircleShape)
			.background(ReadingTrackerColors.progressTrack)
	) {
		Box(
			modifier = Modifier
				.fillMaxWidth(safeProgress)
				.height(7.dp)
				.clip(CircleShape)
				.background(ReadingTrackerColors.primaryGreen)
		)
	}
}

@Composable
private fun BookActionButtons(
	bookId: String,
	onLogSessionClick: (String) -> Unit,
	onAddNoteClick: (String) -> Unit
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(10.dp)
	) {
		Button(
			onClick = {
				onLogSessionClick(bookId)
			},
			modifier = Modifier.weight(1f),
			shape = RoundedCornerShape(18.dp),
			colors = ButtonDefaults.buttonColors(
				containerColor = ReadingTrackerColors.primaryGreen,
				contentColor = ReadingTrackerColors.onPrimary
			)
		) {
			Icon(
				imageVector = Icons.Outlined.Timer,
				contentDescription = null
			)

			Spacer(modifier = Modifier.width(6.dp))

			Text("Log session")
		}

		OutlinedButton(
			onClick = {
				onAddNoteClick(bookId)
			},
			modifier = Modifier.weight(1f),
			shape = RoundedCornerShape(18.dp),
			colors = ButtonDefaults.outlinedButtonColors(
				contentColor = ReadingTrackerColors.primaryGreen
			)
		) {
			Icon(
				imageVector = Icons.Outlined.NoteAlt,
				contentDescription = null
			)

			Spacer(modifier = Modifier.width(6.dp))

			Text("Add note")
		}
	}
}

@Composable
private fun RecentSessionsCard(
	sessions: List<ReadingSession>
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
			modifier = Modifier.padding(16.dp)
		) {
			Text(
				text = "Recent sessions",
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.SemiBold,
				color = ReadingTrackerColors.textPrimary
			)

			Spacer(modifier = Modifier.height(12.dp))

			if (sessions.isEmpty()) {
				Text(
					text = "No sessions yet",
					style = MaterialTheme.typography.bodyMedium,
					color = ReadingTrackerColors.textSecondary
				)
			}
			else {
				sessions.forEach { session ->
					SessionRow(session = session)

					Spacer(modifier = Modifier.height(10.dp))
				}
			}
		}
	}
}

@Composable
private fun SessionRow(
	session: ReadingSession
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		verticalAlignment = Alignment.CenterVertically
	) {
		Column(
			modifier = Modifier.weight(1f)
		) {
			Text(
				text = "${session.date.dayOfMonth}/${session.date.monthNumber}/${session.date.year}",
				style = MaterialTheme.typography.bodyMedium,
				fontWeight = FontWeight.SemiBold,
				color = ReadingTrackerColors.textPrimary
			)

			Text(
				text = sessionProgressText(session),
				style = MaterialTheme.typography.bodySmall,
				color = ReadingTrackerColors.textSecondary
			)
		}

		val minutes = session.minutes
		if (minutes != null) {
			Text(
				text = "$minutes min",
				style = MaterialTheme.typography.labelMedium,
				color = ReadingTrackerColors.primaryGreen,
				fontWeight = FontWeight.SemiBold
			)
		}
	}
}

@Composable
private fun RecentNotesCard(
	notes: List<BookNote>
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
			modifier = Modifier.padding(16.dp)
		) {
			Text(
				text = "Recent notes",
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.SemiBold,
				color = ReadingTrackerColors.textPrimary
			)

			Spacer(modifier = Modifier.height(12.dp))

			if (notes.isEmpty()) {
				Text(
					text = "No notes yet",
					style = MaterialTheme.typography.bodyMedium,
					color = ReadingTrackerColors.textSecondary
				)
			}
			else {
				notes.forEach { note ->
					NoteRow(note = note)

					Spacer(modifier = Modifier.height(10.dp))
				}
			}
		}
	}
}

@Composable
private fun NoteRow(
	note: BookNote
) {
	Column(
		modifier = Modifier.fillMaxWidth()
	) {
		FilterChip(
			selected = false,
			onClick = {},
			label = {
				Text(
					text = when (note.type) {
						BookNoteType.NOTE -> "Note"
						BookNoteType.QUOTE -> "Quote"
					}
				)
			},
			colors = FilterChipDefaults.filterChipColors(
				containerColor = ReadingTrackerColors.surfaceSoft,
				labelColor = ReadingTrackerColors.textPrimary
			),
			border = null
		)

		Spacer(modifier = Modifier.height(4.dp))

		Text(
			text = note.content,
			style = MaterialTheme.typography.bodyMedium,
			color = ReadingTrackerColors.textPrimary,
			maxLines = 3,
			overflow = TextOverflow.Ellipsis
		)

		if (note.page != null) {
			Spacer(modifier = Modifier.height(2.dp))

			Text(
				text = "Page ${note.page}",
				style = MaterialTheme.typography.bodySmall,
				color = ReadingTrackerColors.textSecondary
			)
		}
	}
}

@Composable
private fun StatusBadge(
	status: ReadingStatus
) {
	val text = when (status) {
		ReadingStatus.WANT_TO_READ -> "Want to read"
		ReadingStatus.READING -> "Reading"
		ReadingStatus.FINISHED -> "Finished"
	}

	val background = when (status) {
		ReadingStatus.WANT_TO_READ -> ReadingTrackerColors.chipUnselected
		ReadingStatus.READING -> ReadingTrackerColors.readingChip
		ReadingStatus.FINISHED -> ReadingTrackerColors.chipSelected
	}

	Surface(
		shape = CircleShape,
		color = background
	) {
		Text(
			text = text,
			style = MaterialTheme.typography.labelSmall,
			color = ReadingTrackerColors.textPrimary,
			modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
			maxLines = 1
		)
	}
}

private fun progressText(book: Book): String {
	val totalPages = book.totalPages

	return if (totalPages != null) {
		"${book.currentPage} / $totalPages pages"
	}
	else {
		"${book.currentPage} pages"
	}
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