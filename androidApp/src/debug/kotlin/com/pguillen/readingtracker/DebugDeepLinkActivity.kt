package com.pguillen.readingtracker

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.pguillen.readingtracker.domain.repository.BookNoteRepository
import com.pguillen.readingtracker.domain.repository.BookRepository
import com.pguillen.readingtracker.domain.repository.ReadingSessionRepository
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class DebugDeepLinkActivity : ComponentActivity() {

	private val bookRepository: BookRepository by inject()
	private val sessionRepository: ReadingSessionRepository by inject()
	private val noteRepository: BookNoteRepository by inject()

	val debugSeeder by lazy {
		DebugSeeder(
			bookRepository,
			sessionRepository,
			noteRepository
		)
	}

	override fun onCreate(
		savedInstanceState: Bundle?,
	) {
		super.onCreate(savedInstanceState)

		val uri = intent.data

		lifecycleScope.launch {
			when (uri?.path) {
				"/seed/single-book" -> {
					debugSeeder.seedSingleBook()
				}
				"/seed/single-book-with-session" -> {
					debugSeeder.seedBookWithSession()
				}
				"/seed/multiple-books" -> {
					debugSeeder.seedMultipleBooks()
				}
			}
			val mainIntent = Intent(
				this@DebugDeepLinkActivity,
				MainActivity::class.java
			).apply {
				flags =
					Intent.FLAG_ACTIVITY_CLEAR_TOP or
							Intent.FLAG_ACTIVITY_SINGLE_TOP
			}

			startActivity(mainIntent)
			finish()
		}
	}
}