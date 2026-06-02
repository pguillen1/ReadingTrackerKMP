package com.pguillen.readingtracker.presentation.navigation

sealed class AppRoute(
	val route: String
) {
	data object Library : AppRoute("library")
	data object Stats : AppRoute("stats")
	data object Settings : AppRoute("settings")

	data object BookDetail : AppRoute("bookDetail/{bookId}") {
		fun createRoute(bookId: String): String {
			return "bookDetail/$bookId"
		}
	}

	data object AddBook : AppRoute("addBook")

	data object EditBook : AppRoute("editBook/{bookId}") {
		fun createRoute(bookId: String): String {
			return "editBook/$bookId"
		}
	}

	data object LogSession : AppRoute("logSession/{bookId}") {
		fun createRoute(bookId: String): String {
			return "logSession/$bookId"
		}
	}

	data object BookNotes : AppRoute("bookNotes/{bookId}") {
		fun createRoute(bookId: String): String {
			return "bookNotes/$bookId"
		}
	}
}