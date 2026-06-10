package com.pguillen.readingtracker.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pguillen.readingtracker.domain.model.BookSortOption
import com.pguillen.readingtracker.domain.model.ThemePreference
import com.pguillen.readingtracker.domain.usecase.settings.ObserveUserPreferencesUseCase
import com.pguillen.readingtracker.domain.usecase.settings.UpdateDefaultSortOptionUseCase
import com.pguillen.readingtracker.domain.usecase.settings.UpdateThemePreferenceUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
	observeUserPreferencesUseCase: ObserveUserPreferencesUseCase,
	private val updateThemePreferenceUseCase: UpdateThemePreferenceUseCase,
	private val updateDefaultSortOptionUseCase: UpdateDefaultSortOptionUseCase
) : ViewModel() {

	val uiState: StateFlow<SettingsUiState> =
		observeUserPreferencesUseCase()
			.map { preferences ->
				SettingsUiState(
					themePreference = preferences.themePreference,
					defaultSortOption = preferences.defaultSortOption,
					isLoading = false
				)
			}
			.stateIn(
				scope = viewModelScope,
				started = SharingStarted.WhileSubscribed(5_000),
				initialValue = SettingsUiState()
			)

	fun onThemePreferenceChanged(themePreference: ThemePreference) {
		viewModelScope.launch {
			updateThemePreferenceUseCase(themePreference)
		}
	}

	fun onDefaultSortOptionChanged(sortOption: BookSortOption) {
		viewModelScope.launch {
			updateDefaultSortOptionUseCase(sortOption)
		}
	}
}