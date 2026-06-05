package com.pguillen.readingtracker.di

import com.pguillen.readingtracker.core.date.DateTimeProvider
import com.pguillen.readingtracker.core.id.IdGenerator
import com.pguillen.readingtracker.data.core.FakeDateTimeProvider
import com.pguillen.readingtracker.data.core.FakeIdGenerator
import org.koin.dsl.module

val dataModule = module {

	single<IdGenerator> {
		FakeIdGenerator()
	}

	single<DateTimeProvider> {
		FakeDateTimeProvider()
	}
}