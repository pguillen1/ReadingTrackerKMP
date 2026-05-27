package com.pguillen.readingtracker.data.core

import com.pguillen.readingtracker.core.id.IdGenerator

class FakeIdGenerator : IdGenerator {

	private var currentId = 0

	override fun generateId(): String {
		currentId += 1
		return currentId.toString()
	}
}