package com.pguillen.readingtracker.data.local.mapper

inline fun <reified T : Enum<T>> String.toEnumOrDefault(
	default: T
): T {
	return runCatching {
		enumValueOf<T>(this)
	}.getOrDefault(default)
}