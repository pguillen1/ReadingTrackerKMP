package com.pguillen.readingtracker.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pguillen.readingtracker.presentation.bookdetail.BookDetailRoute
import com.pguillen.readingtracker.presentation.bookedit.AddEditBookRoute
import com.pguillen.readingtracker.presentation.booksessions.BookSessionsRoute
import com.pguillen.readingtracker.presentation.library.LibraryRoute
import com.pguillen.readingtracker.presentation.logsession.LogReadingSessionRoute
import com.pguillen.readingtracker.presentation.settings.SettingsRoute
import com.pguillen.readingtracker.presentation.stats.StatsRoute
import com.pguillen.readingtracker.presentation.theme.ReadingTrackerColors

@Composable
fun AppNavigation() {
	val navController = rememberNavController()

	val navBackStackEntry by navController.currentBackStackEntryAsState()
	val currentDestination = navBackStackEntry?.destination
	val currentRoute = currentDestination?.route

	val shouldShowBottomBar = currentDestination.shouldShowBottomBar()

	Scaffold(
		containerColor = ReadingTrackerColors.background,
		bottomBar = {
			if (shouldShowBottomBar) {
				ReadingTrackerBottomBar(
					currentRoute = currentRoute,
					onDestinationClick = { route ->
						navController.navigateToTopLevelDestination(route)
					}
				)
			}
		}
	) { innerPadding ->
		NavHost(
			navController = navController,
			startDestination = AppRoute.Library.route,
			modifier = Modifier
				.background(ReadingTrackerColors.background)
				.padding(innerPadding)
		) {
			composable(AppRoute.Library.route) {
				LibraryRoute(
					onBookClick = { bookId ->
						navController.navigate(AppRoute.BookDetail.createRoute(bookId))
					},
					onAddBookClick = { navController.navigate(AppRoute.AddBook.route) }
				)
			}

			composable(AppRoute.Stats.route) {
				StatsRoute()
			}

			composable(AppRoute.Settings.route) {
				SettingsRoute()
			}

			composable(AppRoute.AddBook.route) {
				AddEditBookRoute(
					onNavigateBack = { navController.popBackStack() }
				)
			}

			composable(
				route = AppRoute.BookDetail.route,
				arguments = listOf(
					navArgument(NavArgs.BOOK_ID) {
						type = NavType.StringType
					}
				)) { backStackEntry ->
				val bookId = backStackEntry.savedStateHandle.get<String>(NavArgs.BOOK_ID)

				if (bookId != null) {
					BookDetailRoute(
						bookId = bookId,
						onNavigateBack = {
							navController.popBackStack()
						},
						onEditBookClick = {},
						onLogSessionClick = { selectedBookId ->
							navController.navigate(AppRoute.LogSession.createRoute(selectedBookId))
						},
						onAddNoteClick = {},
						onSeeAllSessionsClick = { selectedBookId ->
							navController.navigate(AppRoute.BookSessions.createRoute(selectedBookId))
						},
						onSeeAllNotesClick = {}
					)
				}
			}

			composable(AppRoute.LogSession.route) { backStackEntry ->
				val bookId = backStackEntry.savedStateHandle.get<String>(NavArgs.BOOK_ID)

				if (bookId != null) {
					LogReadingSessionRoute(
						bookId = bookId,
						onNavigateBack = {
							navController.popBackStack()
						}
					)
				}
			}

			composable(AppRoute.BookSessions.route) { backStackEntry ->
				val bookId = backStackEntry.savedStateHandle.get<String>(NavArgs.BOOK_ID)

				if (bookId != null) {
					BookSessionsRoute(
						bookId = bookId,
						onNavigateBack = {
							navController.popBackStack()
						},
						onAddSessionClick = {},
						onEditSessionClick = {}
					)
				}
			}
		}
	}
}

private fun NavDestination?.shouldShowBottomBar(): Boolean {
	if (this == null) return false

	val topLevelRoutes = bottomNavDestinations.map { it.route }

	return hierarchy.any { destination ->
		destination.route in topLevelRoutes
	}
}

private fun NavHostController.navigateToTopLevelDestination(
	route: String
) {
	navigate(route) {
		popUpTo(graph.startDestinationRoute ?: AppRoute.Library.route) {
			saveState = true
		}

		launchSingleTop = true
		restoreState = true
	}
}