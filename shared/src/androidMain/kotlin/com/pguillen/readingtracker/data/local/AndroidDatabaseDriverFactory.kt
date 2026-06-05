package com.pguillen.readingtracker.data.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.pguillen.readingtracker.database.ReadingTrackerDatabase

class AndroidDatabaseDriverFactory(
	private val context: Context
) {
	fun createDriver(): SqlDriver {
		return AndroidSqliteDriver(
			schema = ReadingTrackerDatabase.Schema,
			context = context,
			name = "reading_tracker.db"
		)
	}
}