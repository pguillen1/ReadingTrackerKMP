package com.pguillen.readingtracker

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.pguillen.readingtracker.di.appModule
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration

@Composable
@Preview
fun App() {
	KoinApplication(configuration = koinConfiguration {
		modules(appModule)
	}) {
		MaterialTheme {

		}
	}

}