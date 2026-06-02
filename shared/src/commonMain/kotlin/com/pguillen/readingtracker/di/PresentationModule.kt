package com.pguillen.readingtracker.di

import com.pguillen.readingtracker.presentation.bookedit.AddEditBookViewModel
import com.pguillen.readingtracker.presentation.library.LibraryViewModel
import com.pguillen.readingtracker.presentation.settings.SettingsViewModel
import com.pguillen.readingtracker.presentation.stats.StatsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
	viewModel { LibraryViewModel(get()) }
	viewModel { StatsViewModel(get()) }
	viewModel { SettingsViewModel(get(), get(), get(), get()) }
	viewModel { AddEditBookViewModel(get()) }
}