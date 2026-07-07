package com.pguillen.readingtracker.di

import com.pguillen.readingtracker.core.date.DateTimeProvider
import com.pguillen.readingtracker.core.id.IdGenerator
import com.pguillen.readingtracker.data.core.RandomIdGenerator
import com.pguillen.readingtracker.data.core.SystemDateTimeProvider
import org.koin.dsl.module

val dataModule = module {

	single<IdGenerator> {
		RandomIdGenerator()
	}

	single<DateTimeProvider> {
		SystemDateTimeProvider()
	}
}