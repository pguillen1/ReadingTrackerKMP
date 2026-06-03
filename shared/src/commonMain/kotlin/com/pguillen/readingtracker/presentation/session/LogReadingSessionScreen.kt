package com.pguillen.readingtracker.presentation.session

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.pguillen.readingtracker.presentation.theme.ReadingTrackerColors
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun LogReadingSessionRoute(
	bookId: String,
	onNavigateBack: () -> Unit,
	viewModel: LogReadingSessionViewModel = koinViewModel {
		parametersOf(bookId)
	}
) {
	val uiState by viewModel.uiState.collectAsState()

	LaunchedEffect(viewModel) {
		viewModel.effects.collect { effect ->
			when (effect) {
				LogReadingSessionUiEffect.NavigateBack -> onNavigateBack()
			}
		}
	}

	LogReadingSessionScreen(
		uiState = uiState,
		onNavigateBack = onNavigateBack,
		onStartPageChanged = viewModel::onStartPageChanged,
		onEndPageChanged = viewModel::onEndPageChanged,
		onMinutesChanged = viewModel::onMinutesChanged,
		onNoteChanged = viewModel::onNoteChanged,
		onSaveClicked = viewModel::onSaveClicked
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogReadingSessionScreen(
	uiState: LogReadingSessionUiState,
	onNavigateBack: () -> Unit,
	onStartPageChanged: (String) -> Unit,
	onEndPageChanged: (String) -> Unit,
	onMinutesChanged: (String) -> Unit,
	onNoteChanged: (String) -> Unit,
	onSaveClicked: () -> Unit
) {
	val focusManager = LocalFocusManager.current

	Scaffold(
		containerColor = ReadingTrackerColors.background,
		topBar = {
			TopAppBar(
				title = {
					Text(
						text = "Log session",
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

			if (uiState.bookTitle.isNotBlank()) {
				Text(
					text = uiState.bookTitle,
					style = MaterialTheme.typography.titleLarge,
					fontWeight = FontWeight.Bold,
					color = ReadingTrackerColors.textPrimary
				)

				val totalPages = uiState.totalPages
				if (totalPages != null) {
					Text(
						text = "Total pages: $totalPages",
						style = MaterialTheme.typography.bodyMedium,
						color = ReadingTrackerColors.textSecondary
					)
				}
			}

			SessionTextField(
				value = uiState.startPage,
				onValueChange = onStartPageChanged,
				label = "Start page",
				placeholder = "145",
				keyboardType = KeyboardType.Number,
				imeAction = ImeAction.Next,
				onImeAction = {
					focusManager.moveFocus(FocusDirection.Down)
				}
			)

			SessionTextField(
				value = uiState.endPage,
				onValueChange = onEndPageChanged,
				label = "End page",
				placeholder = "160",
				keyboardType = KeyboardType.Number,
				imeAction = ImeAction.Next,
				onImeAction = {
					focusManager.moveFocus(FocusDirection.Down)
				}
			)

			SessionTextField(
				value = uiState.minutes,
				onValueChange = onMinutesChanged,
				label = "Minutes",
				placeholder = "25",
				keyboardType = KeyboardType.Number,
				imeAction = ImeAction.Next,
				onImeAction = {
					focusManager.moveFocus(FocusDirection.Down)
				}
			)

			SessionTextField(
				value = uiState.note,
				onValueChange = onNoteChanged,
				label = "Note",
				placeholder = "Optional note",
				keyboardType = KeyboardType.Text,
				imeAction = ImeAction.Done,
				onImeAction = {
					focusManager.clearFocus()
				}
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
					text = if (uiState.isSaving) "Saving..." else "Save session",
					fontWeight = FontWeight.SemiBold
				)
			}

			Spacer(modifier = Modifier.height(20.dp))
		}
	}
}

@Composable
private fun SessionTextField(
	value: String,
	onValueChange: (String) -> Unit,
	label: String,
	placeholder: String,
	keyboardType: KeyboardType,
	imeAction: ImeAction,
	onImeAction: () -> Unit
) {
	OutlinedTextField(
		value = value,
		onValueChange = onValueChange,
		modifier = Modifier.fillMaxWidth(),
		label = {
			Text(label)
		},
		placeholder = {
			Text(placeholder)
		},
		singleLine = true,
		shape = RoundedCornerShape(18.dp),
		keyboardOptions = KeyboardOptions(
			keyboardType = keyboardType,
			imeAction = imeAction
		),
		keyboardActions = KeyboardActions(
			onNext = { onImeAction() },
			onDone = { onImeAction() }
		),
		colors = OutlinedTextFieldDefaults.colors(
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
	)
}