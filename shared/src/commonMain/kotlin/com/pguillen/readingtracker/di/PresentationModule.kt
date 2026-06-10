package com.pguillen.readingtracker.di

import com.pguillen.readingtracker.presentation.app.AppViewModel
import com.pguillen.readingtracker.presentation.bookdetail.BookDetailViewModel
import com.pguillen.readingtracker.presentation.bookedit.AddEditBookMode
import com.pguillen.readingtracker.presentation.bookedit.AddEditBookViewModel
import com.pguillen.readingtracker.presentation.booknotes.BookNotesViewModel
import com.pguillen.readingtracker.presentation.booksessions.BookSessionsViewModel
import com.pguillen.readingtracker.presentation.library.LibraryViewModel
import com.pguillen.readingtracker.presentation.noteedit.AddEditNoteMode
import com.pguillen.readingtracker.presentation.noteedit.AddEditNoteViewModel
import com.pguillen.readingtracker.presentation.sessionedit.AddEditReadingSessionViewModel
import com.pguillen.readingtracker.presentation.settings.SettingsViewModel
import com.pguillen.readingtracker.presentation.stats.StatsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
	viewModel { LibraryViewModel(get(), get()) }
	viewModel { StatsViewModel(get()) }
	viewModel { SettingsViewModel(get(), get(), get()) }
	viewModel { parameters ->
		AddEditBookViewModel(
			parameters.get<AddEditBookMode>(),
			get(),
			get(),
			get()
		)
	}
	viewModel { parameters -> BookDetailViewModel(parameters.get(), get()) }
	viewModel { parameters ->
		AddEditReadingSessionViewModel(
			parameters.get(),
			get(),
			get(),
			get(),
			get(),
			get()
		)
	}
	viewModel { parameters -> BookSessionsViewModel(parameters.get(), get(), get(), get()) }
	viewModel { parameters -> BookNotesViewModel(parameters.get(), get(), get(), get()) }
	viewModel { parameters ->
		AddEditNoteViewModel(
			parameters.get<AddEditNoteMode>(),
			get(),
			get(),
			get(),
			get()
		)
	}
	viewModel { AppViewModel(get()) }
}