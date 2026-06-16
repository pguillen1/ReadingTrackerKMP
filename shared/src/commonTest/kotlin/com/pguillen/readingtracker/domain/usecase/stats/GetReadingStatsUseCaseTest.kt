package com.pguillen.readingtracker.domain.usecase.stats

import com.pguillen.readingtracker.domain.model.ReadingStatus
import com.pguillen.readingtracker.fake.BOOK_ID
import com.pguillen.readingtracker.fake.FakeBookRepository
import com.pguillen.readingtracker.fake.FakeReadingSessionRepository
import com.pguillen.readingtracker.fake.SESSION_ID
import com.pguillen.readingtracker.fake.SESSION_ID_2
import com.pguillen.readingtracker.fake.SESSION_ID_3
import com.pguillen.readingtracker.fake.createBook
import com.pguillen.readingtracker.fake.createSession
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetReadingStatsUseCaseTest {

	private lateinit var fakeBookRepository: FakeBookRepository
	private lateinit var fakeReadingSessionRepository: FakeReadingSessionRepository
	private lateinit var getReadingStatsUseCase: GetReadingStatsUseCase

	@BeforeTest
	fun setUp() {
		fakeBookRepository = FakeBookRepository()
		fakeReadingSessionRepository = FakeReadingSessionRepository()

		getReadingStatsUseCase = GetReadingStatsUseCase(
			bookRepository = fakeBookRepository,
			readingSessionRepository = fakeReadingSessionRepository
		)
	}

	@Test
	fun `invoke returns book counters`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(id = "book-1", status = ReadingStatus.WANT_TO_READ),
			createBook(id = "book-2", status = ReadingStatus.READING),
			createBook(id = "book-3", status = ReadingStatus.FINISHED)
		)

		val stats = getReadingStatsUseCase().first()

		assertEquals(3, stats.totalBooks)
		assertEquals(1, stats.readingBooks)
		assertEquals(1, stats.finishedBooks)
	}

	@Test
	fun `invoke returns total sessions pages and minutes`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(id = BOOK_ID)
		)

		fakeReadingSessionRepository.sessions.value = listOf(
			createSession(
				id = "session-1",
				bookId = BOOK_ID,
				startPage = 0,
				endPage = 20,
				minutes = 30
			),
			createSession(
				id = "session-2",
				bookId = BOOK_ID,
				startPage = 20,
				endPage = 50,
				minutes = 40
			)
		)

		val stats = getReadingStatsUseCase().first()

		assertEquals(2, stats.totalSessions)
		assertEquals(50, stats.totalPagesRead)
		assertEquals(70, stats.totalMinutesRead)
	}

	@Test
	fun `invoke groups recent daily summaries by date`() = runTest {
		fakeBookRepository.books.value = listOf(
			createBook(id = BOOK_ID)
		)

		fakeReadingSessionRepository.sessions.value = listOf(
			createSession(
				id = SESSION_ID,
				bookId = BOOK_ID,
				date = LocalDate(2026, 1, 10),
				startPage = 0,
				endPage = 20,
				minutes = 30
			),
			createSession(
				id = SESSION_ID_2,
				bookId = BOOK_ID,
				date = LocalDate(2026, 1, 10),
				startPage = 20,
				endPage = 35,
				minutes = 20
			),
			createSession(
				id = SESSION_ID_3,
				bookId = BOOK_ID,
				date = LocalDate(2026, 1, 11),
				startPage = 35,
				endPage = 50,
				minutes = null
			)
		)

		val stats = getReadingStatsUseCase().first()

		val summaryJan10 = stats.recentDailySummaries.first {
			it.date == LocalDate(2026, 1, 10)
		}

		val summaryJan11 = stats.recentDailySummaries.first {
			it.date == LocalDate(2026, 1, 11)
		}

		assertEquals(35, summaryJan10.pagesRead)
		assertEquals(50, summaryJan10.minutesRead)
		assertEquals(2, summaryJan10.sessionsCount)

		assertEquals(15, summaryJan11.pagesRead)
		assertEquals(0, summaryJan11.minutesRead)
		assertEquals(1, summaryJan11.sessionsCount)
	}
}