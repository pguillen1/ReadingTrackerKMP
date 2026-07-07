package com.pguillen.readingtracker.data.core

import com.pguillen.readingtracker.core.id.IdGenerator
import kotlin.random.Random
import kotlin.time.Clock

class RandomIdGenerator : IdGenerator {

	override fun generateId(): String {
		val timestamp = Clock.System.now().toEpochMilliseconds()
		val randomPart = Random.nextLong(from = 0, until = Long.MAX_VALUE)

		return "$timestamp-$randomPart"
	}
}