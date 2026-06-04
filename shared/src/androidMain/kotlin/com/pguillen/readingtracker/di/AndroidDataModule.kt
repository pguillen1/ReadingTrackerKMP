package com.pguillen.readingtracker.di

import com.pguillen.readingtracker.data.datastore.DataStoreUserPreferencesRepository
import com.pguillen.readingtracker.data.datastore.createUserPreferencesDataStore
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
}