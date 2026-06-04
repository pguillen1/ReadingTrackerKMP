package com.pguillen.readingtracker.di

val commonAppModule = listOf(
	dataModule,
	domainModule,
	presentationModule
)

fun appModules() = commonAppModule + platformModules()