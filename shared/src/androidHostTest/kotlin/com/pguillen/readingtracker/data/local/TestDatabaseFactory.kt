package com.pguillen.readingtracker.data.local

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.pguillen.readingtracker.database.ReadingTrackerDatabase

fun createInMemoryDatabase(): ReadingTrackerDatabase {
	val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
	ReadingTrackerDatabase.Schema.create(driver)
	return ReadingTrackerDatabase(driver)
}