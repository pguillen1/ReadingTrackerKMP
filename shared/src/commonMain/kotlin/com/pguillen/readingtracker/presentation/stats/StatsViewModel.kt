package com.pguillen.readingtracker.presentation.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pguillen.readingtracker.domain.usecase.stats.GetReadingStatsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class StatsViewModel(
	getReadingStatsUseCase: GetReadingStatsUseCase
) : ViewModel() {

	val uiState: StateFlow<StatsUiState> =
		getReadingStatsUseCase()
			.map { stats ->
				StatsUiState(
					totalBooks = stats.totalBooks,
					readingBooks = stats.readingBooks,
					finishedBooks = stats.finishedBooks,
					totalSessions = stats.totalSessions,
					totalPagesRead = stats.totalPagesRead,
					totalMinutesRead = stats.totalMinutesRead,
					recentDailySummaries = stats.recentDailySummaries,
					isLoading = false
				)
			}
			.stateIn(
				scope = viewModelScope,
				started = SharingStarted.WhileSubscribed(5_000),
				initialValue = StatsUiState()
			)
}