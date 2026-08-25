package com.pguillen.readingtracker.domain.usecase.book

import com.pguillen.readingtracker.fake.BOOK_ID
import com.pguillen.readingtracker.fake.FakeBookCoverStorage
import com.pguillen.readingtracker.fake.FakeBookRepository
import com.pguillen.readingtracker.fake.FakeSelectedImage
import com.pguillen.readingtracker.fake.createBook
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ChangeBookCoverUseCaseTest {

	private val testDispatcher = StandardTestDispatcher()
	private lateinit var repository: FakeBookRepository
	private lateinit var storage: FakeBookCoverStorage

	private lateinit var useCase: ChangeBookCoverUseCase

	@BeforeTest
	fun setUp() {
		Dispatchers.setMain(testDispatcher)
		repository = FakeBookRepository()
		storage = FakeBookCoverStorage()

		useCase = ChangeBookCoverUseCase(
			bookRepository = repository,
			bookCoverStorage = storage,
		)
	}

	@AfterTest
	fun tearDown() {
		Dispatchers.resetMain()
	}

	@Test
	fun `given book without cover when changing cover then saves and updates repository`() =
		runTest {

			val book = createBook(
				id = BOOK_ID,
				coverFileName = null,
			)

			repository.books.value += book

			val image = FakeSelectedImage()

			storage.fileNameToReturn =
				"new_cover.webp"

			useCase(
				bookId = BOOK_ID,
				image = image,
			)
			testDispatcher.scheduler.advanceUntilIdle()

			assertEquals(
				listOf(BOOK_ID to image),
				storage.savedImages.toList(),
			)

			assertEquals(
				listOf(
					BOOK_ID to "new_cover.webp"
				),
				repository.coverUpdates.toList(),
			)

			assertTrue(
				storage.deletedFiles.isEmpty()
			)
		}


	@Test
	fun `given book with cover when changing cover then replaces old cover`() =
		runTest {

			val book = createBook(
				id = BOOK_ID,
				coverFileName = "old_cover.webp",
			)

			repository.books.value += book

			storage.fileNameToReturn =
				"new_cover.webp"

			val image = FakeSelectedImage()

			useCase(
				bookId = BOOK_ID,
				image = image,
			)
			testDispatcher.scheduler.advanceUntilIdle()

			assertEquals(
				listOf(
					BOOK_ID to "new_cover.webp"
				),
				repository.coverUpdates.toList(),
			)

			assertEquals(
				listOf("old_cover.webp"),
				storage.deletedFiles,
			)
		}


	@Test
	fun `given database update fails when changing cover then deletes new file`() =
		runTest {

			val book = createBook(
				id = BOOK_ID,
				coverFileName = "old_cover.webp",
			)

			repository.books.value += book

			storage.fileNameToReturn =
				"new_cover.webp"

			repository.updateCoverShouldFail = true

			assertFailsWith<IllegalStateException> {
				useCase(
					bookId = BOOK_ID,
					image = FakeSelectedImage(),
				)
			}

			assertEquals(
				listOf("new_cover.webp"),
				storage.deletedFiles,
			)

			assertEquals(
				"old_cover.webp",
				repository
					.books
					.first()
					.first()
					.coverFileName,
			)
		}


	@Test
	fun `given book does not exist when changing cover then throws`() =
		runTest {

			assertFailsWith<IllegalArgumentException> {
				useCase(
					bookId = BOOK_ID,
					image = FakeSelectedImage(),
				)
			}

			assertTrue(
				storage.savedImages.isEmpty()
			)

			assertTrue(
				repository.coverUpdates.isEmpty()
			)

			assertTrue(
				storage.deletedFiles.isEmpty()
			)
		}

	@Test
	fun `given old cover deletion fails when changing cover then new cover remains assigned`() =
		runTest {

			val book = createBook(
				id = BOOK_ID,
				coverFileName = "old_cover.webp",
			)

			repository.books.value += book

			storage.fileNameToReturn =
				"new_cover.webp"

			storage.deleteShouldFail = true

			useCase(
				bookId = BOOK_ID,
				image = FakeSelectedImage(),
			)

			assertEquals(
				"new_cover.webp",
				repository
					.books
					.first()
					.first()
					.coverFileName,
			)

			assertEquals(
				listOf("old_cover.webp"),
				storage.deletedFiles,
			)
		}
}