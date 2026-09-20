package com.pguillen.readingtracker.presentation.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId

actual fun Modifier.enableTestTagsAsResourceId(): Modifier {
	return this.semantics {
		testTagsAsResourceId = true
	}
}