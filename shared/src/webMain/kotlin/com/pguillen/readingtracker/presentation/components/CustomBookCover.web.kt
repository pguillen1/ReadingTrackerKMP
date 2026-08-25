package com.pguillen.readingtracker.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pguillen.readingtracker.presentation.theme.ReadingTrackerColors

@Composable
actual fun CustomBookCover(fileName: String, modifier: Modifier) {
	Icon(
		modifier = modifier,
		imageVector = Icons.Outlined.Book,
		contentDescription = null,
		tint = ReadingTrackerColors.primaryGreen
	)
}