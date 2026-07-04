package com.pguillen.readingtracker.presentation.booknotes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FormatQuote
import androidx.compose.material.icons.outlined.NoteAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import com.pguillen.readingtracker.domain.model.BookNote
import com.pguillen.readingtracker.domain.model.BookNoteType
import com.pguillen.readingtracker.presentation.testtag.ReadingTrackerTestTags.BookNotes
import com.pguillen.readingtracker.presentation.theme.ReadingTrackerColors
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun BookNotesRoute(
	bookId: String,
	onNavigateBack: () -> Unit,
	onAddNoteClick: (String) -> Unit,
	onEditNoteClick: (String) -> Unit,
	viewModel: BookNotesViewModel = koinViewModel {
		parametersOf(bookId)
	}
) {
	val uiState by viewModel.uiState.collectAsState()

	BookNotesScreen(
		uiState = uiState,
		onNavigateBack = onNavigateBack,
		onAddNoteClick = { onAddNoteClick(bookId) },
		onNoteClick = onEditNoteClick,
		onFilterSelected = viewModel::onFilterSelected,
		onDeleteNoteClick = viewModel::onDeleteNoteClick,
		onDismissDeleteDialog = viewModel::onDismissDeleteDialog,
		onConfirmDeleteNote = viewModel::onConfirmDeleteNote
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookNotesScreen(
	uiState: BookNotesUiState,
	onNavigateBack: () -> Unit,
	onAddNoteClick: () -> Unit,
	onNoteClick: (String) -> Unit,
	onFilterSelected: (BookNoteFilter) -> Unit,
	onDeleteNoteClick: (BookNote) -> Unit,
	onDismissDeleteDialog: () -> Unit,
	onConfirmDeleteNote: () -> Unit
) {
	Scaffold(
		containerColor = ReadingTrackerColors.background,
		topBar = {
			TopAppBar(
				title = {
					Column {
						Text(
							modifier = Modifier.testTag(BookNotes.SCREEN_TITLE),
							text = "Notes & quotes",
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
						modifier = Modifier.testTag(BookNotes.BACK_BUTTON),
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
				modifier = Modifier.testTag(BookNotes.ADD_BUTTON),
				onClick = onAddNoteClick,
				containerColor = ReadingTrackerColors.primaryGreen,
				contentColor = ReadingTrackerColors.onPrimary
			) {
				Icon(
					imageVector = Icons.Outlined.Add,
					contentDescription = "Add note"
				)
			}
		}
	) { innerPadding ->
		Box(
			modifier = Modifier
				.fillMaxSize()
				.background(ReadingTrackerColors.background)
				.padding(innerPadding)
				.testTag(BookNotes.SCREEN)
		) {
			when {
				uiState.isLoading -> {
					Text(
						text = "Loading notes...",
						color = ReadingTrackerColors.textSecondary,
						modifier = Modifier.align(Alignment.Center).testTag(BookNotes.LOADING_TEXT)
					)
				}

				uiState.errorMessage != null && uiState.notes.isEmpty() -> {
					Text(
						text = uiState.errorMessage,
						color = ReadingTrackerColors.textSecondary,
						modifier = Modifier.align(Alignment.Center)
					)
				}

				else -> {
					NotesContent(
						uiState = uiState,
						onFilterSelected = onFilterSelected,
						onNoteClick = onNoteClick,
						onDeleteNoteClick = onDeleteNoteClick
					)
				}
			}
		}
	}

	val notePendingDelete = uiState.notePendingDelete
	if (notePendingDelete != null) {
		DeleteNoteDialog(
			note = notePendingDelete,
			onDismiss = onDismissDeleteDialog,
			onConfirm = onConfirmDeleteNote
		)
	}
}

@Composable
private fun NotesContent(
	uiState: BookNotesUiState,
	onFilterSelected: (BookNoteFilter) -> Unit,
	onNoteClick: (String) -> Unit,
	onDeleteNoteClick: (BookNote) -> Unit
) {
	Column(
		modifier = Modifier.fillMaxSize()
	) {
		NoteFilterRow(
			selectedFilter = uiState.selectedFilter,
			onFilterSelected = onFilterSelected,
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 20.dp, vertical = 12.dp)
		)

		if (uiState.isEmpty) {
			EmptyNotesState(
				selectedFilter = uiState.selectedFilter,
				modifier = Modifier
					.fillMaxSize()
					.padding(horizontal = 32.dp)
					.testTag(BookNotes.EMPTY_SCREEN)
			)
		}
		else {
			NotesList(
				notes = uiState.filteredNotes,
				onNoteClick = onNoteClick,
				onDeleteNoteClick = onDeleteNoteClick
			)
		}
	}
}

@Composable
private fun NoteFilterRow(
	selectedFilter: BookNoteFilter,
	onFilterSelected: (BookNoteFilter) -> Unit,
	modifier: Modifier = Modifier
) {
	FlowRow(
		modifier = modifier,
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		NoteFilterChip(
			modifier = Modifier.testTag(BookNotes.FILTER_ALL),
			text = "All",
			selected = selectedFilter == BookNoteFilter.ALL,
			onClick = { onFilterSelected(BookNoteFilter.ALL) }
		)

		NoteFilterChip(
			modifier = Modifier.testTag(BookNotes.FILTER_NOTES),
			text = "Notes",
			selected = selectedFilter == BookNoteFilter.NOTES,
			onClick = { onFilterSelected(BookNoteFilter.NOTES) }
		)

		NoteFilterChip(
			modifier = Modifier.testTag(BookNotes.FILTER_QUOTES),
			text = "Quotes",
			selected = selectedFilter == BookNoteFilter.QUOTES,
			onClick = { onFilterSelected(BookNoteFilter.QUOTES) }
		)
	}
}

@Composable
private fun NoteFilterChip(
	modifier: Modifier,
	text: String,
	selected: Boolean,
	onClick: () -> Unit
) {
	FilterChip(
		modifier = modifier,
		selected = selected,
		onClick = onClick,
		label = {
			Text(text)
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

@Composable
private fun NotesList(
	notes: List<BookNote>,
	onNoteClick: (String) -> Unit,
	onDeleteNoteClick: (BookNote) -> Unit
) {
	LazyColumn(
		modifier = Modifier.fillMaxSize().testTag(BookNotes.NOTES_LIST),
		contentPadding = PaddingValues(
			start = 20.dp,
			end = 20.dp,
			top = 4.dp,
			bottom = 96.dp
		),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		items(
			items = notes,
			key = { it.id }
		) { note ->
			NoteCard(
				note = note,
				onClick = {
					onNoteClick(note.id)
				},
				onDeleteClick = {
					onDeleteNoteClick(note)
				}
			)
		}
	}
}

@Composable
private fun NoteCard(
	note: BookNote,
	onClick: () -> Unit,
	onDeleteClick: () -> Unit
) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.clickable(onClick = onClick)
			.testTag(BookNotes.noteCard(note.id)),
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
			NoteTypeIcon(type = note.type)

			Spacer(modifier = Modifier.width(12.dp))

			Column(
				modifier = Modifier.weight(1f)
			) {
				NoteTypeBadge(type = note.type)

				Spacer(modifier = Modifier.height(8.dp))

				Text(
					text = note.content,
					style = MaterialTheme.typography.bodyMedium,
					color = ReadingTrackerColors.textPrimary,
					maxLines = 4,
					overflow = TextOverflow.Ellipsis
				)

				if (note.page != null) {
					Spacer(modifier = Modifier.height(8.dp))

					Text(
						text = "Page ${note.page}",
						style = MaterialTheme.typography.bodySmall,
						color = ReadingTrackerColors.textSecondary
					)
				}

				Spacer(modifier = Modifier.height(4.dp))

				Text(
					text = "Updated ${formatDate(note.updatedAt)}",
					style = MaterialTheme.typography.bodySmall,
					color = ReadingTrackerColors.textSecondary
				)
			}

			IconButton(
				onClick = onDeleteClick
			) {
				Icon(
					imageVector = Icons.Outlined.Delete,
					contentDescription = "Delete note",
					tint = ReadingTrackerColors.textSecondary
				)
			}
		}
	}
}

@Composable
private fun NoteTypeIcon(
	type: BookNoteType
) {
	val icon = when (type) {
		BookNoteType.NOTE -> Icons.Outlined.NoteAlt
		BookNoteType.QUOTE -> Icons.Outlined.FormatQuote
	}

	Surface(
		shape = CircleShape,
		color = ReadingTrackerColors.chipSelected
	) {
		Icon(
			imageVector = icon,
			contentDescription = null,
			tint = ReadingTrackerColors.primaryGreen,
			modifier = Modifier.padding(10.dp)
		)
	}
}

@Composable
private fun NoteTypeBadge(
	type: BookNoteType
) {
	val text = when (type) {
		BookNoteType.NOTE -> "Note"
		BookNoteType.QUOTE -> "Quote"
	}

	Surface(
		shape = CircleShape,
		color = ReadingTrackerColors.surfaceSoft
	) {
		Text(
			text = text,
			style = MaterialTheme.typography.labelSmall,
			color = ReadingTrackerColors.textPrimary,
			modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
		)
	}
}

@Composable
private fun EmptyNotesState(
	selectedFilter: BookNoteFilter,
	modifier: Modifier = Modifier
) {
	val message = when (selectedFilter) {
		BookNoteFilter.ALL -> "No notes or quotes yet"
		BookNoteFilter.NOTES -> "No notes yet"
		BookNoteFilter.QUOTES -> "No quotes yet"
	}

	val description = when (selectedFilter) {
		BookNoteFilter.ALL -> "Add your first note or quote using the + button."
		BookNoteFilter.NOTES -> "Add your first note using the + button."
		BookNoteFilter.QUOTES -> "Add your first quote using the + button."
	}

	Column(
		modifier = modifier,
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		Icon(
			imageVector = Icons.AutoMirrored.Outlined.Notes,
			contentDescription = null,
			tint = ReadingTrackerColors.primaryGreen
		)

		Spacer(modifier = Modifier.height(12.dp))

		Text(
			text = message,
			style = MaterialTheme.typography.titleMedium,
			fontWeight = FontWeight.SemiBold,
			color = ReadingTrackerColors.textPrimary
		)

		Spacer(modifier = Modifier.height(4.dp))

		Text(
			text = description,
			style = MaterialTheme.typography.bodyMedium,
			color = ReadingTrackerColors.textSecondary
		)
	}
}

@Composable
private fun DeleteNoteDialog(
	note: BookNote,
	onDismiss: () -> Unit,
	onConfirm: () -> Unit
) {
	AlertDialog(
		onDismissRequest = onDismiss,
		title = {
			Text("Delete note?")
		},
		text = {
			Text(
				text = when (note.type) {
					BookNoteType.NOTE -> "This note will be permanently deleted."
					BookNoteType.QUOTE -> "This quote will be permanently deleted."
				}
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

private fun formatDate(dateTime: kotlinx.datetime.LocalDateTime): String {
	return "${dateTime.dayOfMonth}/${dateTime.monthNumber}/${dateTime.year}"
}