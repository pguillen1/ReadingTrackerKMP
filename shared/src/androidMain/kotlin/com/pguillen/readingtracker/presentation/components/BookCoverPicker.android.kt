package com.pguillen.readingtracker.presentation.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import com.pguillen.readingtracker.domain.storage.AndroidSelectedImage
import com.pguillen.readingtracker.domain.storage.SelectedImage

@Composable
actual fun rememberBookCoverPicker(onImageSelected: (SelectedImage) -> Unit): BookCoverPicker {

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            onImageSelected(
                AndroidSelectedImage(it)
            )
        }
    }

    return BookCoverPicker(
        isSupported = true,
        launchAction = {
            launcher.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts
                        .PickVisualMedia
                        .ImageOnly
                )
            )
        }
    )
}