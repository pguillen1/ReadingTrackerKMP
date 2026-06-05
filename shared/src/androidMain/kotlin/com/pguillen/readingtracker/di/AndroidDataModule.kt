package com.pguillen.readingtracker.di

import com.pguillen.readingtracker.data.datastore.DataStoreUserPreferencesRepository
import com.pguillen.readingtracker.data.datastore.createUserPreferencesDataStore
import com.pguillen.readingtracker.data.local.AndroidDatabaseDriverFactory
import com.pguillen.readingtracker.data.local.repository.SqlDelightBookNoteRepository
import com.pguillen.readingtracker.data.local.repository.SqlDelightBookRepository
import com.pguillen.readingtracker.data.local.repository.SqlDelightReadingSessionRepository
import com.pguillen.readingtracker.database.ReadingTrackerDatabase
import com.pguillen.readingtracker.domain.repository.BookNoteRepository
import com.pguillen.readingtracker.domain.repository.BookRepository
import com.pguillen.readingtracker.domain.repository.ReadingSessionRepository
import com.pguillen.readingtracker.domain.repository.UserPreferencesRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidDataModule = module {
	single {
		createUserPreferencesDataStore(
			context = androidContext()
		)
	}

	single<UserPreferencesRepository> {
		DataStoreUserPreferencesRepository(
			dataStore = get()
		)
	}

	single {
		AndroidDatabaseDriverFactory(
			context = androidContext()
		).createDriver()
	}

	single {
		ReadingTrackerDatabase(
			driver = get()
		)
	}

	single<BookRepository> {
		SqlDelightBookRepository(
			database = get()
		)
	}

	single<ReadingSessionRepository> {
		SqlDelightReadingSessionRepository(
			database = get()
		)
	}

	single<BookNoteRepository> {
		SqlDelightBookNoteRepository(
			database = get()
		)
	}
}