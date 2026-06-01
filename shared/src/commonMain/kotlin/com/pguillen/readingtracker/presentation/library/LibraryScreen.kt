package com.pguillen.readingtracker.presentation.library

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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pguillen.readingtracker.domain.model.Book
import com.pguillen.readingtracker.domain.model.ReadingStatus
import com.pguillen.readingtracker.presentation.theme.ReadingTrackerColors
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LibraryRoute(
	viewModel: LibraryViewModel = koinViewModel(),
	onBookClick: (String) -> Unit = {},
	onAddBookClick: () -> Unit = {}
) {
	val uiState by viewModel.uiState.collectAsState()

	LibraryScreen(
		uiState = uiState,
		onSearchQueryChanged = viewModel::onSearchQueryChanged,
		onFilterSelected = viewModel::onFilterSelected,
		onBookClick = onBookClick,
		onAddBookClick = onAddBookClick
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
	uiState: LibraryUiState,
	onSearchQueryChanged: (String) -> Unit,
	onFilterSelected: (LibraryFilter) -> Unit,
	onBookClick: (String) -> Unit,
	onAddBookClick: () -> Unit
) {
	val listState = rememberLazyListState()

	LaunchedEffect(
		uiState.selectedFilter,
		uiState.searchQuery
	) {
		listState.scrollToItem(0)
	}
	Scaffold(
		floatingActionButton = {
			FloatingActionButton(
				onClick = onAddBookClick,
				containerColor = ReadingTrackerColors.primaryGreen,
				contentColor = ReadingTrackerColors.onPrimary
			) {
				Icon(
					imageVector = Icons.Outlined.Add,
					contentDescription = "Add book"
				)
			}
		},
		containerColor = ReadingTrackerColors.background
	) { innerPadding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.background(ReadingTrackerColors.background)
				.padding(innerPadding)
				.padding(horizontal = 20.dp, vertical = 16.dp)
		) {
			LibraryHeader()

			Spacer(modifier = Modifier.height(20.dp))

			SearchField(
				value = uiState.searchQuery,
				onValueChange = onSearchQueryChanged
			)

			Spacer(modifier = Modifier.height(16.dp))

			FilterRow(
				selectedFilter = uiState.selectedFilter,
				onFilterSelected = onFilterSelected
			)

			Spacer(modifier = Modifier.height(16.dp))

			when {
				uiState.isLoading -> {
					Text(
						text = "Loading books...",
						style = MaterialTheme.typography.bodyMedium,
						color = ReadingTrackerColors.textSecondary
					)
				}

				uiState.isEmpty -> {
					EmptyLibraryState()
				}

				else -> {
					BookList(
						books = uiState.books,
						listState = listState,
						onBookClick = onBookClick
					)
				}
			}
		}
	}
}

@Composable
private fun LibraryHeader() {
	Row(
		modifier = Modifier.fillMaxWidth(),
		verticalAlignment = Alignment.CenterVertically
	) {
		Text(
			text = "Library",
			style = MaterialTheme.typography.headlineMedium,
			fontWeight = FontWeight.Bold,
			color = ReadingTrackerColors.textPrimary,
			modifier = Modifier.weight(1f)
		)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchField(
	value: String,
	onValueChange: (String) -> Unit
) {
	OutlinedTextField(
		value = value,
		onValueChange = onValueChange,
		modifier = Modifier.fillMaxWidth(),
		placeholder = {
			Text("Search books...")
		},
		leadingIcon = {
			Icon(
				imageVector = Icons.Outlined.Search,
				contentDescription = null
			)
		},
		singleLine = true,
		shape = CircleShape,
		colors = OutlinedTextFieldDefaults.colors(
			focusedContainerColor = ReadingTrackerColors.surfaceSoft,
			unfocusedContainerColor = ReadingTrackerColors.surfaceSoft,
			disabledContainerColor = ReadingTrackerColors.surfaceSoft,
			focusedBorderColor = ReadingTrackerColors.surfaceSoft,
			unfocusedBorderColor = ReadingTrackerColors.surfaceSoft,
			cursorColor = ReadingTrackerColors.primaryGreen,
			focusedLeadingIconColor = ReadingTrackerColors.textSecondary,
			unfocusedLeadingIconColor = ReadingTrackerColors.textSecondary,
			focusedPlaceholderColor = ReadingTrackerColors.textSecondary,
			unfocusedPlaceholderColor = ReadingTrackerColors.textSecondary
		)
	)
}

@Composable
private fun FilterRow(
	selectedFilter: LibraryFilter,
	onFilterSelected: (LibraryFilter) -> Unit
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceEvenly,
		verticalAlignment = Alignment.CenterVertically
	) {
		LibraryFilterChip(
			text = "Want to read",
			selected = selectedFilter == LibraryFilter.WANT_TO_READ,
			onClick = { onFilterSelected(LibraryFilter.WANT_TO_READ) }
		)

		LibraryFilterChip(
			text = "Reading",
			selected = selectedFilter == LibraryFilter.READING,
			onClick = { onFilterSelected(LibraryFilter.READING) }
		)

		LibraryFilterChip(
			text = "Finished",
			selected = selectedFilter == LibraryFilter.FINISHED,
			onClick = { onFilterSelected(LibraryFilter.FINISHED) }
		)
	}
}

@Composable
private fun LibraryFilterChip(
	text: String,
	selected: Boolean,
	onClick: () -> Unit
) {
	AssistChip(
		onClick = onClick,
		label = {
			Text(
				text = text,
				fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
			)
		},
		colors = AssistChipDefaults.assistChipColors(
			containerColor = if (selected) {
				ReadingTrackerColors.chipSelected
			}
			else {
				ReadingTrackerColors.chipUnselected
			},
			labelColor = ReadingTrackerColors.textPrimary
		),
		border = null
	)
}

@Composable
private fun BookList(
	books: List<Book>,
	listState: LazyListState,
	onBookClick: (String) -> Unit
) {
	LazyColumn(
		state = listState,
		verticalArrangement = Arrangement.spacedBy(10.dp),
		contentPadding = PaddingValues(bottom = 96.dp)
	) {
		items(
			items = books,
			key = { it.id }
		) { book ->
			BookListItem(
				book = book,
				onClick = { onBookClick(book.id) }
			)
		}
	}
}

@Composable
private fun BookListItem(
	book: Book,
	onClick: () -> Unit
) {
	Card(
		onClick = onClick,
		modifier = Modifier.fillMaxWidth(),
		shape = RoundedCornerShape(22.dp),
		colors = CardDefaults.cardColors(
			containerColor = ReadingTrackerColors.card
		),
		elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(12.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			BookCoverPlaceholder(
				title = book.title
			)

			Spacer(modifier = Modifier.padding(horizontal = 6.dp))

			Column(
				modifier = Modifier.weight(1f)
			) {
				Row(
					verticalAlignment = Alignment.CenterVertically
				) {
					Column(
						modifier = Modifier.weight(1f)
					) {
						Text(
							text = book.title,
							style = MaterialTheme.typography.titleMedium,
							fontWeight = FontWeight.SemiBold,
							color = ReadingTrackerColors.textPrimary,
							maxLines = 1,
							overflow = TextOverflow.Ellipsis
						)

						Text(
							text = book.author.ifBlank { "Unknown author" },
							style = MaterialTheme.typography.bodySmall,
							color = ReadingTrackerColors.textSecondary,
							maxLines = 1,
							overflow = TextOverflow.Ellipsis
						)
					}

					StatusBadge(status = book.status)
				}

				Spacer(modifier = Modifier.height(10.dp))

				BookProgressBar(
					progress = book.progressPercentage,
					modifier = Modifier.fillMaxWidth()
				)

				Spacer(modifier = Modifier.height(6.dp))

				Text(
					text = progressText(book),
					style = MaterialTheme.typography.bodySmall,
					color = ReadingTrackerColors.textSecondary
				)
			}
		}
	}
}

@Composable
private fun BookCoverPlaceholder(
	title: String
) {
	Box(
		modifier = Modifier
			.height(82.dp)
			.width(56.dp)
			.clip(RoundedCornerShape(12.dp))
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
		Row(
			modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Text(
				text = text,
				style = MaterialTheme.typography.labelSmall,
				color = ReadingTrackerColors.textPrimary
			)
		}
	}
}

@Composable
private fun BookProgressBar(
	progress: Float,
	modifier: Modifier = Modifier
) {
	val safeProgress = progress.coerceIn(0f, 1f)

	Box(
		modifier = modifier
			.height(5.dp)
			.clip(CircleShape)
			.background(ReadingTrackerColors.progressTrack)
	) {
		Box(
			modifier = Modifier
				.fillMaxWidth(safeProgress)
				.height(5.dp)
				.clip(CircleShape)
				.background(ReadingTrackerColors.primaryGreen)
		)
	}
}

@Composable
private fun EmptyLibraryState() {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(top = 64.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Icon(
			imageVector = Icons.Outlined.Book,
			contentDescription = null,
			tint = ReadingTrackerColors.primaryGreen
		)

		Spacer(modifier = Modifier.height(12.dp))

		Text(
			text = "No books found",
			style = MaterialTheme.typography.titleMedium,
			fontWeight = FontWeight.SemiBold,
			color = ReadingTrackerColors.textPrimary
		)

		Spacer(modifier = Modifier.height(4.dp))

		Text(
			text = "Add a book or change your filters.",
			style = MaterialTheme.typography.bodyMedium,
			color = ReadingTrackerColors.textSecondary
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