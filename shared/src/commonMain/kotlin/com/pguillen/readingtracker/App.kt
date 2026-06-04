package com.pguillen.readingtracker

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.pguillen.readingtracker.di.appModules
import com.pguillen.readingtracker.presentation.navigation.AppNavigation
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration

@Composable
fun App() {
	KoinApplication(configuration = koinConfiguration {
		modules(appModules())
	}) {
		MaterialTheme {
			AppNavigation()
		}
	}
}