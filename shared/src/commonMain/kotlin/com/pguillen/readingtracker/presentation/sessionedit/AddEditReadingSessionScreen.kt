package com.pguillen.readingtracker.presentation.sessionedit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pguillen.readingtracker.presentation.testtag.ReadingTrackerTestTags.AddEditSession
import com.pguillen.readingtracker.presentation.theme.ReadingTrackerColors
import kotlinx.datetime.LocalDate
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AddEditReadingSessionRoute(
	mode: AddEditReadingSessionMode,
	onNavigateBack: () -> Unit,
	viewModel: AddEditReadingSessionViewModel = koinViewModel {
		parametersOf(mode)
	}
) {
	val uiState by viewModel.uiState.collectAsState()

	LaunchedEffect(viewModel) {
		viewModel.effects.collect { effect ->
			when (effect) {
				AddEditReadingSessionUiEffect.NavigateBack -> onNavigateBack()
			}
		}
	}

	AddEditReadingSessionScreen(
		uiState = uiState,
		onNavigateBack = onNavigateBack,
		onStartPageChanged = viewModel::onStartPageChanged,
		onEndPageChanged = viewModel::onEndPageChanged,
		onMinutesChanged = viewModel::onMinutesChanged,
		onNoteChanged = viewModel::onNoteChanged,
		onDateChanged = viewModel::onDateChanged,
		onSaveClicked = viewModel::onSaveClicked
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditReadingSessionScreen(
	uiState: AddEditReadingSessionUiState,
	onNavigateBack: () -> Unit,
	onStartPageChanged: (String) -> Unit,
	onEndPageChanged: (String) -> Unit,
	onMinutesChanged: (String) -> Unit,
	onNoteChanged: (String) -> Unit,
	onDateChanged: (String) -> Unit,
	onSaveClicked: () -> Unit
) {
	val focusManager = LocalFocusManager.current
	val showDatePicker = rememberSaveable { mutableStateOf(false) }

	Scaffold(
		containerColor = ReadingTrackerColors.background,
		topBar = {
			TopAppBar(
				title = {
					Column {
						Text(
							modifier = Modifier.testTag(AddEditSession.SCREEN_TITLE),
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
					IconButton(
						modifier = Modifier.testTag(AddEditSession.BACK_BUTTON),
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
		}
	) { innerPadding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.background(ReadingTrackerColors.background)
				.padding(innerPadding)
				.padding(horizontal = 20.dp)
				.testTag(AddEditSession.SCREEN),
			verticalArrangement = Arrangement.spacedBy(18.dp)
		) {
			Spacer(modifier = Modifier.height(4.dp))

			Text(
				modifier = Modifier.testTag(AddEditSession.TOTAL_PAGES_FIELD),
				text = "Total pages: ${uiState.totalPages}",
				style = MaterialTheme.typography.bodyMedium,
				color = ReadingTrackerColors.textSecondary
			)

			SessionTextField(
				modifier = Modifier
					.clickable { showDatePicker.value = true }
					.testTag(AddEditSession.DATE_FIELD),
				value = uiState.date.toString(),
				onValueChange = {},
				label = "Date",
				placeholder = "",
				keyboardType = KeyboardType.Number,
				imeAction = ImeAction.Next,
				onImeAction = {
					focusManager.moveFocus(FocusDirection.Down)
				}
			)

			if (showDatePicker.value) {
				SessionDatePickerDialog(
					selectedDate = uiState.date,
					today = uiState.date,
					onDateSelected = onDateChanged,
					onDismiss = { showDatePicker.value = false }
				)
			}

			SessionTextField(
				modifier = Modifier.testTag(AddEditSession.START_PAGE_FIELD),
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
				modifier = Modifier.testTag(AddEditSession.END_PAGE_FIELD),
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
				modifier = Modifier.testTag(AddEditSession.MINUTES_FIELD),
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
				modifier = Modifier.testTag(AddEditSession.NOTE_FIELD),
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
				modifier = Modifier.fillMaxWidth().testTag(AddEditSession.SAVE_BUTTON),
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
fun SessionDatePickerDialog(
	selectedDate: LocalDate,
	today: LocalDate,
	onDateSelected: (String) -> Unit,
	onDismiss: () -> Unit,
) {
	val todayMillis = today.toEpochDays()

//	val selectableDates = remember(todayMillis) {
//		object : SelectableDates {
//			override fun isSelectableDate(
//				utcTimeMillis: Long,
//			): Boolean {
//				return utcTimeMillis <= todayMillis
//			}
//
//			override fun isSelectableYear(year: Int): Boolean {
//				return year <= today.year
//			}
//		}
//	}

	val datePickerState = rememberDatePickerState(
		initialSelectedDateMillis = selectedDate.toEpochDays(),
//		selectableDates = selectableDates,
	)

	DatePickerDialog(
		onDismissRequest = onDismiss,
		confirmButton = {
			TextButton(
				onClick = {
					val selectedMillis =
						datePickerState.selectedDateMillis
							?: return@TextButton

					onDateSelected(
						LocalDate.fromEpochDays(selectedMillis).toString(),
					)
				},
			) {
				Text("Aceptar")
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) {
				Text("Cancelar")
			}
		},
	) {
		DatePicker(
			state = datePickerState,
		)
	}
}

@Composable
private fun SessionTextField(
	modifier: Modifier,
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
		modifier = modifier.fillMaxWidth(),
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