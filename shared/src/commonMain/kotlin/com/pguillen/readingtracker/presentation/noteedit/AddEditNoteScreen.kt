package com.pguillen.readingtracker.presentation.noteedit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pguillen.readingtracker.domain.model.BookNoteType
import com.pguillen.readingtracker.presentation.theme.ReadingTrackerColors
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AddEditNoteRoute(
	mode: AddEditNoteMode,
	onNavigateBack: () -> Unit,
	viewModel: AddEditNoteViewModel = koinViewModel {
		parametersOf(mode)
	}
) {
	val uiState by viewModel.uiState.collectAsState()

	LaunchedEffect(viewModel) {
		viewModel.effects.collect { effect ->
			when (effect) {
				AddEditNoteUiEffect.NavigateBack -> onNavigateBack()
			}
		}
	}

	AddEditNoteScreen(
		uiState = uiState,
		onNavigateBack = onNavigateBack,
		onTypeSelected = viewModel::onTypeSelected,
		onContentChanged = viewModel::onContentChanged,
		onPageChanged = viewModel::onPageChanged,
		onSaveClicked = viewModel::onSaveClicked
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteScreen(
	uiState: AddEditNoteUiState,
	onNavigateBack: () -> Unit,
	onTypeSelected: (BookNoteType) -> Unit,
	onContentChanged: (String) -> Unit,
	onPageChanged: (String) -> Unit,
	onSaveClicked: () -> Unit
) {
	val focusManager = LocalFocusManager.current

	Scaffold(
		containerColor = ReadingTrackerColors.background,
		topBar = {
			TopAppBar(
				title = {
					Column {
						Text(
							text = uiState.title,
							fontWeight = FontWeight.SemiBold,
							color = ReadingTrackerColors.textPrimary
						)

						if (uiState.bookTitle.isNotBlank()) {
							Text(
								text = uiState.bookTitle,
								style = MaterialTheme.typography.bodySmall,
								color = ReadingTrackerColors.textSecondary
							)
						}
					}
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
				colors = TopAppBarDefaults.topAppBarColors(
					containerColor = ReadingTrackerColors.background
				)
			)
		}
	) { innerPadding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.background(ReadingTrackerColors.background)
				.padding(innerPadding)
				.padding(horizontal = 20.dp),
			verticalArrangement = Arrangement.spacedBy(18.dp)
		) {
			Spacer(modifier = Modifier.height(4.dp))

			NoteTypeSelector(
				selectedType = uiState.type,
				onTypeSelected = onTypeSelected
			)

			OutlinedTextField(
				value = uiState.content,
				onValueChange = onContentChanged,
				modifier = Modifier
					.fillMaxWidth()
					.height(180.dp),
				label = {
					Text("Content")
				},
				placeholder = {
					Text("Write your note or quote...")
				},
				shape = RoundedCornerShape(18.dp),
				keyboardOptions = KeyboardOptions(
					keyboardType = KeyboardType.Text,
					imeAction = ImeAction.Next
				),
				keyboardActions = KeyboardActions(
					onNext = {
						focusManager.moveFocus(FocusDirection.Down)
					}
				),
				colors = noteTextFieldColors()
			)

			OutlinedTextField(
				value = uiState.page,
				onValueChange = onPageChanged,
				modifier = Modifier.fillMaxWidth(),
				label = {
					Text("Page")
				},
				placeholder = {
					Text("Optional")
				},
				singleLine = true,
				shape = RoundedCornerShape(18.dp),
				keyboardOptions = KeyboardOptions(
					keyboardType = KeyboardType.Number,
					imeAction = ImeAction.Done
				),
				keyboardActions = KeyboardActions(
					onDone = {
						focusManager.clearFocus()
					}
				),
				colors = noteTextFieldColors()
			)

			if (uiState.errorMessage != null) {
				Text(
					text = uiState.errorMessage,
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.error
				)
			}

			Spacer(modifier = Modifier.weight(1f))

			Button(
				onClick = onSaveClicked,
				enabled = uiState.canSave,
				modifier = Modifier.fillMaxWidth(),
				shape = RoundedCornerShape(18.dp),
				colors = ButtonDefaults.buttonColors(
					containerColor = ReadingTrackerColors.primaryGreen,
					contentColor = ReadingTrackerColors.onPrimary
				),
				contentPadding = PaddingValues(vertical = 14.dp)
			) {
				Icon(
					imageVector = Icons.Outlined.Save,
					contentDescription = null
				)

				Spacer(modifier = Modifier.padding(horizontal = 4.dp))

				Text(
					text = uiState.saveButtonText,
					fontWeight = FontWeight.SemiBold
				)
			}

			Spacer(modifier = Modifier.height(20.dp))
		}
	}
}

@Composable
private fun NoteTypeSelector(
	selectedType: BookNoteType,
	onTypeSelected: (BookNoteType) -> Unit
) {
	FlowRow(
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		NoteTypeChip(
			text = "Note",
			selected = selectedType == BookNoteType.NOTE,
			onClick = { onTypeSelected(BookNoteType.NOTE) }
		)

		NoteTypeChip(
			text = "Quote",
			selected = selectedType == BookNoteType.QUOTE,
			onClick = { onTypeSelected(BookNoteType.QUOTE) }
		)
	}
}

@Composable
private fun NoteTypeChip(
	text: String,
	selected: Boolean,
	onClick: () -> Unit
) {
	FilterChip(
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
private fun noteTextFieldColors() = OutlinedTextFieldDefaults.colors(
	focusedContainerColor = ReadingTrackerColors.card,
	unfocusedContainerColor = ReadingTrackerColors.card,
	disabledContainerColor = ReadingTrackerColors.card,
	focusedBorderColor = ReadingTrackerColors.primaryGreen,
	unfocusedBorderColor = ReadingTrackerColors.surfaceSoft,
	cursorColor = ReadingTrackerColors.primaryGreen,
	focusedLabelColor = ReadingTrackerColors.primaryGreen,
	unfocusedLabelColor = ReadingTrackerColors.textSecondary,
	focusedPlaceholderColor = ReadingTrackerColors.textSecondary,
	unfocusedPlaceholderColor = ReadingTrackerColors.textSecondary
)