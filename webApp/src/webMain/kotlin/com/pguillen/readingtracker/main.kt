package com.pguillen.readingtracker

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.pguillen.readingtracker.presentation.components.PhoneDemoFrame

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        PhoneDemoFrame {
            App()
        }
    }
}