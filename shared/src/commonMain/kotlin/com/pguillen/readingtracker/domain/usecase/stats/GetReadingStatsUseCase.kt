package com.pguillen.readingtracker.domain.usecase.stats

import com.pguillen.readingtracker.domain.model.ReadingStats
import com.pguillen.readingtracker.domain.model.ReadingStatus
import com.pguillen.readingtracker.domain.repository.BookRepository
import com.pguillen.readingtracker.domain.repository.ReadingSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetReadingStatsUseCase(
	private val bookRepository: BookRepository,
	private val readingSessionRepository: ReadingSessionRepository
) {
	operator fun invoke(): Flow<ReadingStats> {
		return combine(
			bookRepository.observeBooks(),
			readingSessionRepository.observeAllSessions()
		) { books, sessions ->

			val totalPagesRead = sessions.sumOf { session ->
				val startPage = session.startPage
				if (startPage != null) {
					(session.endPage - startPage).coerceAtLeast(0)
				}
				else {
					0
				}
			}

			val totalMinutesRead = sessions.sumOf { session ->
				session.minutes ?: 0
			}

			ReadingStats(
				totalBooks = books.size,
				readingBooks = books.count { it.status == ReadingStatus.READING },
				finishedBooks = books.count { it.status == ReadingStatus.FINISHED },
				totalSessions = sessions.size,
				totalPagesRead = totalPagesRead,
				totalMinutesRead = totalMinutesRead
			)
		}
	}
}