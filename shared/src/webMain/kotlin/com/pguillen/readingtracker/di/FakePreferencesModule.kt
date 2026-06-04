package com.pguillen.readingtracker.di

import com.pguillen.readingtracker.data.repository.FakeUserPreferencesRepository
import com.pguillen.readingtracker.domain.repository.UserPreferencesRepository
import org.koin.dsl.module

val fakePreferencesModule = module {
	single<UserPreferencesRepository> {
		FakeUserPreferencesRepository()
	}
}