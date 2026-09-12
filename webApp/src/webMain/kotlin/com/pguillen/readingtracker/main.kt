package com.pguillen.readingtracker

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.pguillen.readingtracker.di.appModules
import com.pguillen.readingtracker.presentation.components.PhoneDemoFrame
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform.startKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    startKoin {
        modules(appModules())
    }
    ComposeViewport {
        PhoneDemoFrame {
            App()
        }
    }
}