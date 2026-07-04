package com.pguillen.readingtracker.presentation.bookedit

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pguillen.readingtracker.domain.model.ReadingStatus
import com.pguillen.readingtracker.presentation.testtag.ReadingTrackerTestTags.AddEditBook
import com.pguillen.readingtracker.presentation.theme.ReadingTrackerColors
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AddEditBookRoute(
	mode: AddEditBookMode,
	onNavigateBack: () -> Unit,
	viewModel: AddEditBookViewModel = koinViewModel {
		parametersOf(mode)
	}
) {
	val uiState by viewModel.uiState.collectAsState()

	LaunchedEffect(viewModel) {
		viewModel.effects.collect { effect ->
			when (effect) {
				AddEditBookUiEffect.NavigateBack -> onNavigateBack()
			}
		}
	}

	AddEditBookScreen(
		uiState = uiState,
		onNavigateBack = onNavigateBack,
		onTitleChanged = viewModel::onTitleChanged,
		onAuthorChanged = viewModel::onAuthorChanged,
		onTotalPagesChanged = viewModel::onTotalPagesChanged,
		onCurrentPageChanged = viewModel::onCurrentPageChanged,
		onStatusSelected = viewModel::onStatusSelected,
		onSaveClicked = viewModel::onSaveClicked
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditBookScreen(
	uiState: AddEditBookUiState,
	onNavigateBack: () -> Unit,
	onTitleChanged: (String) -> Unit,
	onAuthorChanged: (String) -> Unit,
	onTotalPagesChanged: (String) -> Unit,
	onCurrentPageChanged: (String) -> Unit,
	onStatusSelected: (ReadingStatus) -> Unit,
	onSaveClicked: () -> Unit
) {
	val focusManager = LocalFocusManager.current

	Scaffold(
		containerColor = ReadingTrackerColors.background,
		topBar = {
			TopAppBar(
				title = {
					Text(
						modifier = Modifier.testTag(AddEditBook.SCREEN_TITLE),
						text = uiState.screenTitle,
						fontWeight = FontWeight.SemiBold,
						color = ReadingTrackerColors.textPrimary
					)
				},
				navigationIcon = {
					IconButton(
						modifier = Modifier.testTag(AddEditBook.BACK_BUTTON),
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
				.testTag(AddEditBook.SCREEN),
			verticalArrangement = Arrangement.spacedBy(18.dp)
		) {
			Spacer(modifier = Modifier.height(4.dp))

			BookTextField(
				modifier = Modifier.testTag(AddEditBook.TITLE_FIELD),
				value = uiState.title,
				onValueChange = onTitleChanged,
				label = "Title",
				placeholder = "The Hobbit",
				onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
			)

			BookTextField(
				modifier = Modifier.testTag(AddEditBook.AUTHOR_FIELD),
				value = uiState.author,
				onValueChange = onAuthorChanged,
				label = "Author",
				placeholder = "J.R.R. Tolkien",
				onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
			)

			BookTextField(
				modifier = Modifier.testTag(AddEditBook.TOTAL_PAGES_FIELD),
				value = uiState.totalPages,
				onValueChange = onTotalPagesChanged,
				label = "Total pages",
				placeholder = "310",
				keyboardType = KeyboardType.Number,
				onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
			)

			BookTextField(
				modifier = Modifier.testTag(AddEditBook.CURRENT_PAGE_FIELD),
				value = uiState.currentPage,
				onValueChange = onCurrentPageChanged,
				label = "Current page",
				placeholder = "0",
				keyboardType = KeyboardType.Number,
				imeAction = ImeAction.Done,
				onImeAction = { focusManager.clearFocus() }
			)

			Column(
				verticalArrangement = Arrangement.spacedBy(10.dp)
			) {
				Text(
					text = "Status",
					style = MaterialTheme.typography.labelLarge,
					fontWeight = FontWeight.SemiBold,
					color = ReadingTrackerColors.textPrimary
				)

				ReadingStatusSelector(
					selectedStatus = uiState.selectedStatus,
					onStatusSelected = onStatusSelected
				)
			}

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
				modifier = Modifier.fillMaxWidth()
					.testTag(AddEditBook.SAVE_BUTTON),
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
private fun BookTextField(
	modifier: Modifier,
	value: String,
	onValueChange: (String) -> Unit,
	label: String,
	placeholder: String,
	keyboardType: KeyboardType = KeyboardType.Text,
	imeAction: ImeAction = ImeAction.Next,
	onImeAction: () -> Unit = {}
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
			onNext = {
				onImeAction()
			},
			onDone = {
				onImeAction()
			}
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

@Composable
private fun ReadingStatusSelector(
	selectedStatus: ReadingStatus,
	onStatusSelected: (ReadingStatus) -> Unit
) {
	FlowRow(
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		StatusChip(
			modifier = Modifier.testTag(AddEditBook.STATUS_WANT_TO_READ),
			text = "Want to read",
			selected = selectedStatus == ReadingStatus.WANT_TO_READ,
			onClick = { onStatusSelected(ReadingStatus.WANT_TO_READ) }
		)

		StatusChip(
			modifier = Modifier.testTag(AddEditBook.STATUS_READING),
			text = "Reading",
			selected = selectedStatus == ReadingStatus.READING,
			onClick = { onStatusSelected(ReadingStatus.READING) }
		)

		StatusChip(
			modifier = Modifier.testTag(AddEditBook.STATUS_FINISHED),
			text = "Finished",
			selected = selectedStatus == ReadingStatus.FINISHED,
			onClick = { onStatusSelected(ReadingStatus.FINISHED) }
		)
	}
}

@Composable
private fun StatusChip(
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