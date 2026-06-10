package com.pguillen.readingtracker.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class ReadingTrackerPalette(
	val background: Color,
	val card: Color,
	val surfaceSoft: Color,
	val primaryGreen: Color,
	val onPrimary: Color,
	val chipSelected: Color,
	val chipUnselected: Color,
	val readingChip: Color,
	val cover: Color,
	val progressTrack: Color,
	val textPrimary: Color,
	val textSecondary: Color
)

val LightReadingTrackerPalette = ReadingTrackerPalette(
	background = Color(0xFFFBF6ED),
	card = Color(0xFFFFFCF7),
	surfaceSoft = Color(0xFFF3EDE2),
	primaryGreen = Color(0xFF0F5C45),
	onPrimary = Color(0xFFFFFFFF),
	chipSelected = Color(0xFFCDE8D7),
	chipUnselected = Color(0xFFE8EEDB),
	readingChip = Color(0xFFFFE7AE),
	cover = Color(0xFFDDE8DC),
	progressTrack = Color(0xFFE4DDD1),
	textPrimary = Color(0xFF1F2522),
	textSecondary = Color(0xFF70756F)
)

val DarkReadingTrackerPalette = ReadingTrackerPalette(
	background = Color(0xFF101714),
	card = Color(0xFF18211D),
	surfaceSoft = Color(0xFF223029),
	primaryGreen = Color(0xFF8DDBB2),
	onPrimary = Color(0xFF082116),
	chipSelected = Color(0xFF284B38),
	chipUnselected = Color(0xFF303A32),
	readingChip = Color(0xFF5A4720),
	cover = Color(0xFF26382F),
	progressTrack = Color(0xFF33423A),
	textPrimary = Color(0xFFEAF1EC),
	textSecondary = Color(0xFFA9B5AD)
)

val LocalReadingTrackerColors = staticCompositionLocalOf {
	LightReadingTrackerPalette
}

object ReadingTrackerColors {
	val background: Color
		@Composable get() = LocalReadingTrackerColors.current.background

	val card: Color
		@Composable get() = LocalReadingTrackerColors.current.card

	val surfaceSoft: Color
		@Composable get() = LocalReadingTrackerColors.current.surfaceSoft

	val primaryGreen: Color
		@Composable get() = LocalReadingTrackerColors.current.primaryGreen

	val onPrimary: Color
		@Composable get() = LocalReadingTrackerColors.current.onPrimary

	val chipSelected: Color
		@Composable get() = LocalReadingTrackerColors.current.chipSelected

	val chipUnselected: Color
		@Composable get() = LocalReadingTrackerColors.current.chipUnselected

	val readingChip: Color
		@Composable get() = LocalReadingTrackerColors.current.readingChip

	val cover: Color
		@Composable get() = LocalReadingTrackerColors.current.cover

	val progressTrack: Color
		@Composable get() = LocalReadingTrackerColors.current.progressTrack

	val textPrimary: Color
		@Composable get() = LocalReadingTrackerColors.current.textPrimary

	val textSecondary: Color
		@Composable get() = LocalReadingTrackerColors.current.textSecondary
}