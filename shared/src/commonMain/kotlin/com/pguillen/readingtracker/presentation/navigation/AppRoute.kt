package com.pguillen.readingtracker.presentation.navigation

sealed class AppRoute(
	val route: String
) {
	data object Library : AppRoute("library")
	data object Stats : AppRoute("stats")
	data object Settings : AppRoute("settings")

	data object BookDetail : AppRoute("bookDetail/{${NavArgs.BOOK_ID}}") {
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

	data object LogSession : AppRoute("logSession/{${NavArgs.BOOK_ID}}") {
		fun createRoute(bookId: String): String {
			return "logSession/$bookId"
		}
	}

	data object BookSessions : AppRoute("bookSessions/{${NavArgs.BOOK_ID}}") {
		fun createRoute(bookId: String): String {
			return "bookSessions/$bookId"
		}
	}

	data object EditSession : AppRoute("editSession/{${NavArgs.SESSION_ID}}") {
		fun createRoute(sessionId: String): String {
			return "editSession/$sessionId"
		}
	}

	data object BookNotes : AppRoute("bookNotes/{bookId}") {
		fun createRoute(bookId: String): String {
			return "bookNotes/$bookId"
		}
	}
}