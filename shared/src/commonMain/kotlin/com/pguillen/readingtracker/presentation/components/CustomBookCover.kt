package com.pguillen.readingtracker.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun CustomBookCover(
	fileName: String,
	modifier: Modifier = Modifier,
)