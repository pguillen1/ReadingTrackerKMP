package com.pguillen.readingtracker.di

import com.pguillen.readingtracker.domain.usecase.book.AddBookUseCase
import com.pguillen.readingtracker.domain.usecase.book.ChangeBookStatusUseCase
import com.pguillen.readingtracker.domain.usecase.book.DeleteBookUseCase
import com.pguillen.readingtracker.domain.usecase.book.ObserveBookByIdUseCase
import com.pguillen.readingtracker.domain.usecase.book.ObserveBookDetailUseCase
import com.pguillen.readingtracker.domain.usecase.book.ObserveBooksUseCase
import com.pguillen.readingtracker.domain.usecase.book.UpdateBookUseCase
import com.pguillen.readingtracker.domain.usecase.note.AddBookNoteUseCase
import com.pguillen.readingtracker.domain.usecase.note.DeleteBookNoteUseCase
import com.pguillen.readingtracker.domain.usecase.note.ObserveBookNotesUseCase
import com.pguillen.readingtracker.domain.usecase.note.UpdateBookNoteUseCase
import com.pguillen.readingtracker.domain.usecase.session.DeleteReadingSessionUseCase
import com.pguillen.readingtracker.domain.usecase.session.ObserveBookSessionsUseCase
import com.pguillen.readingtracker.domain.usecase.session.RegisterReadingSessionUseCase
import com.pguillen.readingtracker.domain.usecase.session.UpdateReadingSessionUseCase
import com.pguillen.readingtracker.domain.usecase.settings.ObserveUserPreferencesUseCase
import com.pguillen.readingtracker.domain.usecase.settings.UpdateDefaultSortOptionUseCase
import com.pguillen.readingtracker.domain.usecase.settings.UpdateLibraryViewModeUseCase
import com.pguillen.readingtracker.domain.usecase.settings.UpdateThemePreferenceUseCase
import com.pguillen.readingtracker.domain.usecase.stats.GetReadingStatsUseCase
import org.koin.dsl.module

val domainModule = module {
	factory { ObserveBooksUseCase(get()) }
	factory { ObserveBookByIdUseCase(get()) }
	factory { AddBookUseCase(get(), get(), get()) }
	factory { UpdateBookUseCase(get(), get()) }
	factory { DeleteBookUseCase(get()) }
	factory { ChangeBookStatusUseCase(get(), get()) }

	factory { ObserveBookSessionsUseCase(get()) }
	factory { RegisterReadingSessionUseCase(get(), get(), get(), get()) }
	factory { UpdateReadingSessionUseCase(get(), get(), get()) }
	factory { DeleteReadingSessionUseCase(get()) }

	factory { ObserveBookNotesUseCase(get()) }
	factory { AddBookNoteUseCase(get(), get(), get(), get()) }
	factory { UpdateBookNoteUseCase(get(), get(), get()) }
	factory { DeleteBookNoteUseCase(get()) }

	factory { GetReadingStatsUseCase(get(), get()) }

	factory { ObserveUserPreferencesUseCase(get()) }
	factory { UpdateDefaultSortOptionUseCase(get()) }
	factory { UpdateLibraryViewModeUseCase(get()) }
	factory { UpdateThemePreferenceUseCase(get()) }

	factory { ObserveBookDetailUseCase(get(), get(), get()) }
}