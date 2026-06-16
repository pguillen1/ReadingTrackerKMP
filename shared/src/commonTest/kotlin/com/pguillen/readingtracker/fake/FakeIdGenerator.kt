package com.pguillen.readingtracker.fake

import com.pguillen.readingtracker.core.id.IdGenerator

class FakeIdGenerator(
	private val ids: MutableList<String> = mutableListOf("id-1", "id-2", "id-3")
) : IdGenerator {

	override fun generateId(): String {
		return ids.removeFirst()
	}
}