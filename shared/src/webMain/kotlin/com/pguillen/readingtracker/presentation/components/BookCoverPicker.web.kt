package com.pguillen.readingtracker.presentation.components

import androidx.compose.runtime.Composable
import com.pguillen.readingtracker.domain.storage.SelectedImage

@Composable
actual fun rememberBookCoverPicker(onImageSelected: (SelectedImage) -> Unit): BookCoverPicker {
    return BookCoverPicker(
        isSupported = false,
        isOpen = false,
        launchAction = {}
    )
}