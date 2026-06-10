package com.pguillen.readingtracker.presentation.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pguillen.readingtracker.domain.usecase.settings.ObserveUserPreferencesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AppViewModel(
	observeUserPreferencesUseCase: ObserveUserPreferencesUseCase
) : ViewModel() {

	val uiState: StateFlow<AppUiState> =
		observeUserPreferencesUseCase()
			.map { preferences ->
				AppUiState(
					themePreference = preferences.themePreference
				)
			}
			.stateIn(
				scope = viewModelScope,
				started = SharingStarted.WhileSubscribed(5_000),
				initialValue = AppUiState()
			)
}