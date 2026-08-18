package com.pguillen.readingtracker.presentation.components

import androidx.compose.runtime.Composable
import com.pguillen.readingtracker.domain.storage.SelectedImage

class BookCoverPicker(
    val isSupported: Boolean,
    private val launchAction: () -> Unit,
) {
    fun launch() {
        launchAction()
    }
}

@Composable
expect fun rememberBookCoverPicker(
    onImageSelected: (SelectedImage) -> Unit,
): BookCoverPicker