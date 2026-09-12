package com.pguillen.readingtracker

import android.app.Application
import com.pguillen.readingtracker.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ReadingTrackerApplication: Application() {
	override fun onCreate() {
		super.onCreate()

		startKoin {
			androidContext(this@ReadingTrackerApplication)
			modules(appModules())
		}
	}
}