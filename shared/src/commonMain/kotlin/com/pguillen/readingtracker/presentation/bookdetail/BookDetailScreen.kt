package com.pguillen.readingtracker.presentation.bookdetail

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.NoteAlt
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pguillen.readingtracker.domain.model.Book
import com.pguillen.readingtracker.domain.model.BookNote
import com.pguillen.readingtracker.domain.model.BookNoteType
import com.pguillen.readingtracker.domain.model.ReadingSession
import com.pguillen.readingtracker.domain.model.ReadingStatus
import com.pguillen.readingtracker.domain.storage.SelectedImage
import com.pguillen.readingtracker.presentation.components.CustomBookCover
import com.pguillen.readingtracker.presentation.components.rememberBookCoverPicker
import com.pguillen.readingtracker.presentation.testtag.ReadingTrackerTestTags.BookDetail
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
	onSeeAllSessionsClick: (String) -> Unit,
	onSeeAllNotesClick: (String) -> Unit,
	viewModel: BookDetailViewModel = koinViewModel {
		parametersOf(bookId)
	}
) {
	val uiState by viewModel.uiState.collectAsState()

	val coverPicker = rememberBookCoverPicker(
		onImageSelected = {
			viewModel.onCoverChange(it)
		}
	)

	LaunchedEffect(viewModel) {
		viewModel.effects.collect { effect ->
			when (effect) {
				BookDetailUiEffect.NavigateBack -> onNavigateBack()
			}
		}
	}

	BookDetailScreen(
		uiState = uiState,
		canChangeCover = coverPicker.isSupported,
		isPhotoPickerOpen = coverPicker.isOpen,
		onNavigateBack = onNavigateBack,
		onEditBookClick = onEditBookClick,
		onLogSessionClick = onLogSessionClick,
		onAddNoteClick = onAddNoteClick,
		onSeeAllSessionsClick = onSeeAllSessionsClick,
		onSeeAllNotesClick = onSeeAllNotesClick,
		onDeleteBookClick = viewModel::onDeleteBookClick,
		onDismissDeleteDialog = viewModel::onDismissDeleteDialog,
		onConfirmDeleteBook = viewModel::onConfirmDeleteBook,
		onCoverChangeClick = { coverPicker.launch() },
		onCoverChange = viewModel::onCoverChange
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
	uiState: BookDetailUiState,
	canChangeCover: Boolean,
	isPhotoPickerOpen: Boolean,
	onNavigateBack: () -> Unit,
	onEditBookClick: (String) -> Unit,
	onLogSessionClick: (String) -> Unit,
	onAddNoteClick: (String) -> Unit,
	onSeeAllSessionsClick: (String) -> Unit,
	onSeeAllNotesClick: (String) -> Unit,
	onDeleteBookClick: () -> Unit,
	onDismissDeleteDialog: () -> Unit,
	onConfirmDeleteBook: () -> Unit,
	onCoverChangeClick: () -> Unit,
	onCoverChange: (SelectedImage?) -> Unit
) {
	Scaffold(
		containerColor = ReadingTrackerColors.background,
		topBar = {
			TopAppBar(
				title = {
					Text(
						modifier = Modifier.testTag(BookDetail.SCREEN_TITLE),
						text = "Book details",
						fontWeight = FontWeight.SemiBold,
						color = ReadingTrackerColors.textPrimary
					)
				},
				navigationIcon = {
					IconButton(
						modifier = Modifier.testTag(BookDetail.BACK_BUTTON),
						onClick = onNavigateBack
					) {
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
							modifier = Modifier.testTag(BookDetail.EDIT_BUTTON),
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

						IconButton(
							modifier = Modifier.testTag(BookDetail.DELETE_BUTTON),
							onClick = onDeleteBookClick
						) {
							Icon(
								imageVector = Icons.Outlined.Delete,
								contentDescription = "Delete book",
								tint = MaterialTheme.colorScheme.error
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
					isUpdatingCover = uiState.isUpdatingCover,
					canChangeCover = canChangeCover,
					isPhotoPickerOpen = isPhotoPickerOpen,
					recentSessions = uiState.recentSessions,
					recentNotes = uiState.recentNotes,
					onLogSessionClick = onLogSessionClick,
					onAddNoteClick = onAddNoteClick,
					modifier = Modifier.padding(innerPadding),
					onSeeAllSessionsClick = onSeeAllSessionsClick,
					onSeeAllNotesClick = onSeeAllNotesClick,
					onCoverChangeClick = onCoverChangeClick,
					onCoverChange = onCoverChange
				)
			}
		}
	}

	val book = uiState.book
	if (uiState.showDeleteDialog && book != null) {
		DeleteBookDialog(
			bookTitle = book.title,
			isDeleting = uiState.isDeleting,
			onDismiss = onDismissDeleteDialog,
			onConfirm = onConfirmDeleteBook
		)
	}
}

@Composable
private fun BookDetailContent(
	book: Book,
	isUpdatingCover: Boolean,
	canChangeCover: Boolean,
	isPhotoPickerOpen: Boolean,
	recentSessions: List<ReadingSession>,
	recentNotes: List<BookNote>,
	onLogSessionClick: (String) -> Unit,
	onAddNoteClick: (String) -> Unit,
	modifier: Modifier = Modifier,
	onSeeAllSessionsClick: (String) -> Unit,
	onSeeAllNotesClick: (String) -> Unit,
	onCoverChangeClick: () -> Unit,
	onCoverChange: (SelectedImage?) -> Unit
) {
	LazyColumn(
		modifier = modifier
			.fillMaxSize()
			.background(ReadingTrackerColors.background)
			.testTag(BookDetail.SCREEN),
		contentPadding = PaddingValues(
			start = 20.dp,
			end = 20.dp,
			top = 12.dp,
			bottom = 28.dp
		),
		verticalArrangement = Arrangement.spacedBy(18.dp)
	) {
		item {
			BookHeroCard(
				book = book,
				isUpdatingCover = isUpdatingCover,
				isPhotoPickerOpen = isPhotoPickerOpen,
				canChangeCover = canChangeCover,
				onCoverChangeClick = onCoverChangeClick,
				onCoverChange = onCoverChange
			)
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
				bookId = book.id,
				sessions = recentSessions,
				onSeeAllClick = onSeeAllSessionsClick
			)
		}

		item {
			RecentNotesCard(
				bookId = book.id,
				notes = recentNotes,
				onSeeAllClick = onSeeAllNotesClick
			)
		}
	}
}

@Composable
private fun BookHeroCard(
	book: Book,
	isUpdatingCover: Boolean,
	isPhotoPickerOpen: Boolean,
	canChangeCover: Boolean,
	onCoverChangeClick: () -> Unit,
	onCoverChange: (SelectedImage?) -> Unit
) {
	var showCoverOptions by remember {
		mutableStateOf(false)
	}

	var showRemoveConfirmation by remember {
		mutableStateOf(false)
	}

	Card(
		modifier = Modifier.fillMaxWidth().testTag(BookDetail.BOOK_CARD),
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
				if (canChangeCover) {
					BookCoverLarge(
						coverFileName = book.coverFileName,
						isUpdatingCover = isUpdatingCover,
						isPhotoPickerOpen = isPhotoPickerOpen,
						onClick = {
							if (book.coverFileName == null) {
								onCoverChangeClick()
							}
							else {
								showCoverOptions = true
							}
						}
					)
				}
				else {
					BookCoverPlaceHolder()
				}

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
	if (showCoverOptions) {
		BookCoverOptionsBottomSheet(
			onCoverChange = {
				showCoverOptions = false
				onCoverChangeClick()
			},
			onRemoveCover = {
				showCoverOptions = false
				showRemoveConfirmation = true
			},
			onDismiss = {
				showCoverOptions = false
			}
		)
	}
	if (showRemoveConfirmation) {
		RemoveBookCoverDialog(
			onConfirm = {
				showRemoveConfirmation = false
				onCoverChange(null)
			},
			onDismiss = { showRemoveConfirmation = false }
		)
	}
}

@Composable
private fun BookCoverLarge(
	coverFileName: String?,
	isUpdatingCover: Boolean,
	isPhotoPickerOpen: Boolean,
	onClick: () -> Unit
) {

	Box(
		modifier = Modifier
			.width(86.dp)
			.height(126.dp)
			.clip(RoundedCornerShape(18.dp))
			.background(ReadingTrackerColors.cover)
			.clickable(enabled = !isUpdatingCover || !isPhotoPickerOpen) { onClick() }
	) {
		if (isUpdatingCover) {
			CircularProgressIndicator(
				modifier = Modifier.align(Alignment.Center)
			)
		}
		else {
			if (coverFileName != null) {
				CustomBookCover(
					fileName = coverFileName,
					modifier = Modifier.testTag(BookDetail.BOOK_CARD)
				)
			}
			else {
				Icon(
					modifier = Modifier.align(Alignment.Center),
					imageVector = Icons.Outlined.Book,
					contentDescription = null,
					tint = ReadingTrackerColors.primaryGreen
				)
			}
			CoverActionIndicator(
				hasCover = coverFileName != null,
				modifier = Modifier
					.align(Alignment.BottomEnd)
			)
		}
	}
}

@Composable
private fun BookCoverPlaceHolder() {
	Box(
		modifier = Modifier
			.width(86.dp)
			.height(126.dp)
			.clip(RoundedCornerShape(18.dp))
			.background(ReadingTrackerColors.cover),
		contentAlignment = Alignment.Center
	) {
		Icon(
			modifier = Modifier.align(Alignment.Center),
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
			modifier = Modifier.weight(1f).testTag(BookDetail.ADD_SESSION_BUTTON),
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
			modifier = Modifier.weight(1f).testTag(BookDetail.ADD_NOTE_BUTTON),
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
	bookId: String,
	sessions: List<ReadingSession>,
	onSeeAllClick: (String) -> Unit
) {
	Card(
		modifier = Modifier.fillMaxWidth().testTag(BookDetail.RECENT_SESSIONS_CARD),
		shape = RoundedCornerShape(24.dp),
		colors = CardDefaults.cardColors(
			containerColor = ReadingTrackerColors.card
		),
		elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
	) {
		Column(
			modifier = Modifier.padding(16.dp)
		) {
			SectionHeader(
				title = "Recent sessions",
				actionText = "See all",
				onActionClick = {
					onSeeAllClick(bookId)
				}
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
	bookId: String,
	notes: List<BookNote>,
	onSeeAllClick: (String) -> Unit
) {
	Card(
		modifier = Modifier.fillMaxWidth().testTag(BookDetail.RECENT_NOTES_CARD),
		shape = RoundedCornerShape(24.dp),
		colors = CardDefaults.cardColors(
			containerColor = ReadingTrackerColors.card
		),
		elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
	) {
		Column(
			modifier = Modifier.padding(16.dp)
		) {
			SectionHeader(
				title = "Recent notes",
				actionText = "See all",
				onActionClick = {
					onSeeAllClick(bookId)
				}
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
private fun SectionHeader(
	title: String,
	actionText: String,
	onActionClick: () -> Unit
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		verticalAlignment = Alignment.CenterVertically
	) {
		Text(
			text = title,
			style = MaterialTheme.typography.titleMedium,
			fontWeight = FontWeight.SemiBold,
			color = ReadingTrackerColors.textPrimary,
			modifier = Modifier.weight(1f)
		)

		TextButton(
			onClick = onActionClick,
			contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
		) {
			Text(
				text = actionText,
				color = ReadingTrackerColors.primaryGreen,
				fontWeight = FontWeight.SemiBold
			)
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

@Composable
private fun DeleteBookDialog(
	bookTitle: String,
	isDeleting: Boolean,
	onDismiss: () -> Unit,
	onConfirm: () -> Unit
) {
	AlertDialog(
		onDismissRequest = {
			if (!isDeleting) {
				onDismiss()
			}
		},
		title = {
			Text("Delete book?")
		},
		text = {
			Text(
				text = "This will permanently delete \"$bookTitle\", including its reading sessions and notes."
			)
		},
		confirmButton = {
			TextButton(
				onClick = onConfirm,
				enabled = !isDeleting
			) {
				Text(
					text = if (isDeleting) "Deleting..." else "Delete",
					color = MaterialTheme.colorScheme.error
				)
			}
		},
		dismissButton = {
			TextButton(
				onClick = onDismiss,
				enabled = !isDeleting
			) {
				Text("Cancel")
			}
		},
		containerColor = ReadingTrackerColors.card
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookCoverOptionsBottomSheet(
	onCoverChange: () -> Unit,
	onRemoveCover: () -> Unit,
	onDismiss: () -> Unit,
) {
	ModalBottomSheet(
		onDismissRequest = onDismiss,
	) {
		Text(
			text = "Book cover",
			style = MaterialTheme.typography.titleLarge,
			modifier = Modifier.padding(
				horizontal = 24.dp,
				vertical = 8.dp,
			),
		)

		ListItem(
			headlineContent = {
				Text("Change cover")
			},
			leadingContent = {
				Icon(
					imageVector = Icons.Default.Edit,
					contentDescription = null,
				)
			},
			modifier = Modifier.clickable(
				onClick = onCoverChange,
			),
		)

		ListItem(
			headlineContent = {
				Text(
					text = "Remove cover",
					color = MaterialTheme.colorScheme.error,
				)
			},
			leadingContent = {
				Icon(
					imageVector = Icons.Default.Delete,
					contentDescription = null,
					tint = MaterialTheme.colorScheme.error,
				)
			},
			modifier = Modifier.clickable(
				onClick = onRemoveCover,
			),
		)

		ListItem(
			headlineContent = {
				Text("Cancel")
			},
			leadingContent = {
				Icon(
					imageVector = Icons.Default.Close,
					contentDescription = null,
				)
			},
			modifier = Modifier.clickable(
				onClick = onDismiss,
			),
		)

		Spacer(
			modifier = Modifier.height(16.dp),
		)
	}
}

@Composable
fun RemoveBookCoverDialog(
	onConfirm: () -> Unit,
	onDismiss: () -> Unit,
) {
	AlertDialog(
		onDismissRequest = onDismiss,
		title = {
			Text("Remove cover?")
		},
		text = {
			Text(
				"The custom cover will be removed and the default cover will be shown."
			)
		},
		confirmButton = {
			TextButton(
				onClick = onConfirm,
			) {
				Text(
					text = "Remove",
					color = MaterialTheme.colorScheme.error,
				)
			}
		},
		dismissButton = {
			TextButton(
				onClick = onDismiss,
			) {
				Text("Cancel")
			}
		},
	)
}

@Composable
private fun CoverActionIndicator(
	hasCover: Boolean,
	modifier: Modifier = Modifier,
) {
	Box(
		modifier = modifier
			.size(22.dp)
			.background(
				color = MaterialTheme.colorScheme.primary,
				shape = CircleShape,
			),
		contentAlignment = Alignment.Center,
	) {
		Icon(
			imageVector = if (hasCover) {
				Icons.Default.Edit
			}
			else {
				Icons.Default.Add
			},
			contentDescription = null,
			tint = MaterialTheme.colorScheme.onPrimary,
			modifier = Modifier.size(18.dp),
		)
	}
}

private fun progressText(book: Book): String {
	val totalPages = book.totalPages

	return "${book.currentPage} / $totalPages pages"
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