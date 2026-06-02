package com.pguillen.readingtracker.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavDestination(
	val route: String,
	val label: String,
	val icon: ImageVector
)

val bottomNavDestinations = listOf(
	BottomNavDestination(
		route = AppRoute.Library.route,
		label = "Library",
		icon = Icons.AutoMirrored.Outlined.LibraryBooks
	),
	BottomNavDestination(
		route = AppRoute.Stats.route,
		label = "Stats",
		icon = Icons.Outlined.BarChart
	),
	BottomNavDestination(
		route = AppRoute.Settings.route,
		label = "Settings",
		icon = Icons.Outlined.Settings
	)
)