package com.pguillen.readingtracker.di

import com.pguillen.readingtracker.core.date.DateTimeProvider
import com.pguillen.readingtracker.core.id.IdGenerator
import com.pguillen.readingtracker.data.core.FakeDateTimeProvider
import com.pguillen.readingtracker.data.core.FakeIdGenerator
import com.pguillen.readingtracker.data.fake.DemoData
import com.pguillen.readingtracker.data.fake.FakeUserPreferencesRepository
import com.pguillen.readingtracker.data.repository.FakeBookNoteRepository
import com.pguillen.readingtracker.data.repository.FakeBookRepository
import com.pguillen.readingtracker.data.repository.FakeReadingSessionRepository
import com.pguillen.readingtracker.domain.repository.BookNoteRepository
import com.pguillen.readingtracker.domain.repository.BookRepository
import com.pguillen.readingtracker.domain.repository.ReadingSessionRepository
import com.pguillen.readingtracker.domain.repository.UserPreferencesRepository
import org.koin.dsl.module

val dataModule = module {
	single<BookRepository> {
		FakeBookRepository(initialBooks = DemoData.books)
	}

	single<ReadingSessionRepository> {
		FakeReadingSessionRepository(initialSessions = DemoData.sessions)
	}

	single<BookNoteRepository> {
		FakeBookNoteRepository(initialNotes = DemoData.notes)
	}

	single<IdGenerator> {
		FakeIdGenerator()
	}

	single<DateTimeProvider> {
		FakeDateTimeProvider()
	}

	single<UserPreferencesRepository> {
		FakeUserPreferencesRepository()
	}
}