package com.pguillen.readingtracker.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.pguillen.readingtracker.domain.model.ThemePreference

@Composable
fun ReadingTrackerTheme(
	themePreference: ThemePreference,
	content: @Composable () -> Unit
) {
	val useDarkTheme = when (themePreference) {
		ThemePreference.SYSTEM -> isSystemInDarkTheme()
		ThemePreference.LIGHT -> false
		ThemePreference.DARK -> true
	}

	val palette = if (useDarkTheme) {
		DarkReadingTrackerPalette
	} else {
		LightReadingTrackerPalette
	}

	val colorScheme = if (useDarkTheme) {
		readingTrackerDarkColorScheme()
	} else {
		readingTrackerLightColorScheme()
	}

	CompositionLocalProvider(
		LocalReadingTrackerColors provides palette
	) {
		MaterialTheme(
			colorScheme = colorScheme,
			content = content
		)
	}
}

private fun readingTrackerLightColorScheme(): ColorScheme {
	return lightColorScheme(
		primary = LightReadingTrackerPalette.primaryGreen,
		onPrimary = LightReadingTrackerPalette.onPrimary,
		background = LightReadingTrackerPalette.background,
		surface = LightReadingTrackerPalette.card,
		onSurface = LightReadingTrackerPalette.textPrimary,
		error = androidx.compose.ui.graphics.Color(0xFFBA1A1A)
	)
}

private fun readingTrackerDarkColorScheme(): ColorScheme {
	return darkColorScheme(
		primary = DarkReadingTrackerPalette.primaryGreen,
		onPrimary = DarkReadingTrackerPalette.onPrimary,
		background = DarkReadingTrackerPalette.background,
		surface = DarkReadingTrackerPalette.card,
		onSurface = DarkReadingTrackerPalette.textPrimary,
		error = androidx.compose.ui.graphics.Color(0xFFFFB4AB)
	)
}