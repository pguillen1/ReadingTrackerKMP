package com.pguillen.readingtracker.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import java.io.File

@Composable
actual fun CustomBookCover(
	fileName: String,
	modifier: Modifier,
) {
	val context = LocalContext.current

	val coverFile = remember(fileName) {
		File(
			File(context.filesDir, "book_covers"),
			fileName,
		)
	}

	AsyncImage(
		model = coverFile,
		contentDescription = "Book cover",
		modifier = modifier,
		contentScale = ContentScale.Crop,
	)
}