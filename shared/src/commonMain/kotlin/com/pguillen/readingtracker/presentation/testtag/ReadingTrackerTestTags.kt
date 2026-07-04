package com.pguillen.readingtracker.presentation.testtag

object ReadingTrackerTestTags {
	object Library {
		const val SCREEN = "library_screen"
		const val SCREEN_TITLE = "library_screen_title"
		const val SEARCH_FIELD = "library_search_field"
		const val ADD_BOOK_FAB = "library_add_book_fab"
		const val FILTER_READING = "library_filter_reading"
		const val FILTER_FINISHED = "library_filter_finished"
		const val FILTER_WANT_TO_READ = "library_filter_want_to_read"
		const val NO_BOOKS_FOUND = "library_no_books_found"
		const val LOADING_TEXT = "library_loading_text"

		fun bookCard(bookId: String): String = "library_book_card_$bookId"
	}

	object AddEditBook {
		const val SCREEN = "add_edit_book_screen"
		const val SCREEN_TITLE = "add_edit_book_screen_title"
		const val BACK_BUTTON = "add_edit_book_back_button"
		const val TITLE_FIELD = "add_edit_book_title_field"
		const val AUTHOR_FIELD = "add_edit_book_author_field"
		const val TOTAL_PAGES_FIELD = "add_edit_book_total_pages_field"
		const val CURRENT_PAGE_FIELD = "add_edit_book_current_page_field"
		const val STATUS_READING = "add_edit_book_status_reading"
		const val STATUS_FINISHED = "add_edit_book_status_finished"
		const val STATUS_WANT_TO_READ = "add_edit_book_status_want_to_read"
		const val SAVE_BUTTON = "add_edit_book_save_button"
	}

	object BookDetail {
		const val SCREEN = "book_detail_screen"
		const val SCREEN_TITLE = "book_detail_screen_title"
		const val BACK_BUTTON = "book_detail_back_button"
		const val EDIT_BUTTON = "book_detail_edit_button"
		const val DELETE_BUTTON = "book_detail_delete_button"
		const val BOOK_CARD = "book_detail_book_card"
		const val ADD_SESSION_BUTTON = "book_detail_add_session_button"
		const val ADD_NOTE_BUTTON = "book_detail_add_note_button"
		const val RECENT_SESSIONS_CARD = "book_detail_recent_sessions_card"
		const val RECENT_NOTES_CARD = "book_detail_recent_notes_card"
	}

	object AddEditSession {
		const val SCREEN = "add_edit_session_screen"
		const val SCREEN_TITLE = "add_edit_session_screen_title"
		const val BACK_BUTTON = "add_edit_session_back_button"
		const val TOTAL_PAGES_FIELD = "add_edit_session_total_pages"
		const val START_PAGE_FIELD = "add_edit_session_start_page_field"
		const val END_PAGE_FIELD = "add_edit_session_end_page_field"
		const val MINUTES_FIELD = "add_edit_session_minutes_field"
		const val NOTE_FIELD = "add_edit_session_note_field"
		const val SAVE_BUTTON = "add_edit_session_save_button"
	}

	object AddEditNote {
		const val SCREEN = "add_edit_note_screen"
		const val SCREEN_TITLE = "add_edit_note_screen_title"
		const val BACK_BUTTON = "add_edit_note_back_button"
		const val TYPE_NOTE = "add_edit_note_type_note"
		const val TYPE_QUOTE = "add_edit_note_type_quote"
		const val CONTENT_FIELD = "add_edit_note_content_field"
		const val PAGE_FIELD = "add_edit_note_page_field"
		const val SAVE_BUTTON = "add_edit_note_save_button"
	}

	object BookSessions {
		const val SCREEN = "book_sessions_screen"
		const val SCREEN_TITLE = "book_sessions_screen_title"
		const val BACK_BUTTON = "book_sessions_back_button"
		const val LOADING_TEXT = "book_sessions_loading_text"
		const val EMPTY_SCREEN = "book_sessions_empty_screen"
		const val SESSIONS_LIST = "book_sessions_sessions_list"
		const val ADD_BUTTON = "book_sessions_add_button"

		fun sessionCard(sessionId: String): String = "book_sessions_session_card_$sessionId"
	}

	object BookNotes {
		const val SCREEN = "book_notes_screen"
		const val SCREEN_TITLE = "book_notes_screen_title"
		const val BACK_BUTTON = "book_notes_back_button"
		const val LOADING_TEXT = "book_notes_loading_text"
		const val EMPTY_SCREEN = "book_notes_empty_screen"
		const val NOTES_LIST = "book_notes_notes_list"
		const val FILTER_ALL = "book_notes_filter_all"
		const val FILTER_NOTES = "book_notes_filter_notes"
		const val FILTER_QUOTES = "book_notes_filter_quotes"
		const val ADD_BUTTON = "book_notes_add_button"

		fun noteCard(noteId: String): String = "book_notes_note_card_$noteId"
	}

	object Stats {
		const val SCREEN = "stats_screen"
		const val SCREEN_TITLE = "stats_screen_title"
		const val TOTAL_BOOKS_CARD = "stats_total_books_card"
		const val FINISHED_CARD = "stats_finished_card"
		const val READING_CARD = "stats_reading_card"
		const val SESSIONS_CARD = "stats_sessions_card"
		const val PAGES_READ_CARD = "stats_pages_read_card"
		const val MINUTES_CARD = "stats_minutes_card"
		const val RECENT_ACTIVITY_CARD = "stats_recent_activity_card"
	}

	object Settings {
		const val SCREEN = "settings_screen"
		const val SCREEN_TITLE = "settings_screen_title"
		const val THEME_CARD = "settings_theme_card"
		const val THEME_SYSTEM = "settings_theme_system"
		const val THEME_LIGHT = "settings_theme_light"
		const val THEME_DARK = "settings_theme_dark"
		const val DEFAULT_SORT_CARD = "settings_default_sort_card"
		const val SORT_BY_UPDATED = "settings_default_sort_by_updated"
		const val SORT_BY_ADDED = "settings_default_sort_by_added"
		const val SORT_BY_TITLE = "settings_default_sort_by_title"
		const val SORT_BY_AUTHOR = "settings_default_sort_by_author"
		const val SORT_BY_PROGRESS = "settings_default_sort_by_progress"
		const val ABOUT_CARD = "settings_about_card"
	}
}