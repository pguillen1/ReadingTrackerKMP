package com.pguillen.readingtracker.core.error

sealed class DomainException(message: String) : Exception(message) {

	class Validation(message: String) : DomainException(message)

	class NotFound(message: String) : DomainException(message)
}