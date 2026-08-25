package com.pguillen.readingtracker.presentation.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.pguillen.readingtracker.domain.storage.AndroidSelectedImage
import com.pguillen.readingtracker.domain.storage.SelectedImage

@Composable
actual fun rememberBookCoverPicker(onImageSelected: (SelectedImage) -> Unit): BookCoverPicker {

	var isOpen by rememberSaveable { mutableStateOf(false) }

	val launcher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.PickVisualMedia()
	) { uri ->
		isOpen = false
		uri?.let {
			onImageSelected(
				AndroidSelectedImage(it)
			)
		}
	}

	return BookCoverPicker(
		isSupported = true,
		isOpen = isOpen,
		launchAction = {
			if (!isOpen) {
				isOpen = true

				try {
					launcher.launch(
						PickVisualMediaRequest(
							ActivityResultContracts
								.PickVisualMedia
								.ImageOnly
						)
					)
				}
				catch (exception: Exception) {
					isOpen = false
					throw exception
				}
			}
		}
	)
}