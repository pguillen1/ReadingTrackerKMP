package com.pguillen.readingtracker.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

private const val USER_PREFERENCES_FILE_NAME = "user_preferences.preferences_pb"

fun createUserPreferencesDataStore(
	context: Context
): DataStore<Preferences> {
	return PreferenceDataStoreFactory.createWithPath(
		produceFile = {
			context.filesDir
				.resolve(USER_PREFERENCES_FILE_NAME)
				.absolutePath
				.toPath()
		}
	)
}