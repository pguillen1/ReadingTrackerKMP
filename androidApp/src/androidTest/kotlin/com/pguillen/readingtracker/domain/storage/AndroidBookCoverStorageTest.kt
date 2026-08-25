package com.pguillen.readingtracker.domain.storage

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.IOException
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class AndroidBookCoverStorageTest {

    private lateinit var context: Context
    private lateinit var storage: AndroidBookCoverStorage
    private lateinit var testRoot: File

    @Before
    fun setUp() {
        val baseContext =
            InstrumentationRegistry
                .getInstrumentation()
                .targetContext

        testRoot = File(
            baseContext.cacheDir,
            "book_cover_storage_tests",
        ).apply {
            deleteRecursively()
            mkdirs()
        }

        // Usamos directorios aislados para que los tests
        // no toquen las portadas reales de la aplicación.
        context = object : ContextWrapper(baseContext) {

            override fun getFilesDir(): File {
                return File(
                    testRoot,
                    "files",
                ).apply {
                    mkdirs()
                }
            }

            override fun getCacheDir(): File {
                return File(
                    testRoot,
                    "cache",
                ).apply {
                    mkdirs()
                }
            }
        }

        storage = AndroidBookCoverStorage(
            context = context,
        )
    }

    @After
    fun tearDown() {
        testRoot.deleteRecursively()
    }


    @Test
    fun saveCover_horizontalImage_createsCorrectSizedCover() =
        runBlocking {

            val uri = createTestImage(
                width = 1200,
                height = 600,
            )

            val fileName = storage.saveCover(
                bookId = "book_1",
                image = AndroidSelectedImage(uri),
            )

            val outputFile = getCoverFile(fileName)

            assertTrue(outputFile.exists())
            assertTrue(fileName.endsWith(".webp"))

            val resultBitmap = requireNotNull(
                BitmapFactory.decodeFile(
                    outputFile.absolutePath,
                )
            )

            assertEquals(688, resultBitmap.width)
            assertEquals(1008, resultBitmap.height)
        }


    @Test
    fun saveCover_verticalImage_createsCorrectSizedCover() =
        runBlocking {

            val uri = createTestImage(
                width = 600,
                height = 1600,
            )

            val fileName = storage.saveCover(
                bookId = "book_1",
                image = AndroidSelectedImage(uri),
            )

            val outputFile = getCoverFile(fileName)

            assertTrue(outputFile.exists())

            val resultBitmap = requireNotNull(
                BitmapFactory.decodeFile(
                    outputFile.absolutePath,
                )
            )

            assertEquals(688, resultBitmap.width)
            assertEquals(1008, resultBitmap.height)
        }


    @Test
    fun saveCover_correctRatioImage_createsCorrectSizedCover() =
        runBlocking {

            // 860 / 1260 = 86 / 126
            val uri = createTestImage(
                width = 860,
                height = 1260,
            )

            val fileName = storage.saveCover(
                bookId = "book_1",
                image = AndroidSelectedImage(uri),
            )

            val outputFile = getCoverFile(fileName)

            assertTrue(outputFile.exists())

            val resultBitmap = requireNotNull(
                BitmapFactory.decodeFile(
                    outputFile.absolutePath,
                )
            )

            assertEquals(688, resultBitmap.width)
            assertEquals(1008, resultBitmap.height)
        }


    @Test
    fun saveCover_missingUri_throwsIOException() {

        val missingFile = File(
            context.cacheDir,
            "missing_image.png",
        )

        val uri = Uri.fromFile(missingFile)

        assertThrows(IOException::class.java) {
            runBlocking {
                storage.saveCover(
                    bookId = "book_1",
                    image = AndroidSelectedImage(uri),
                )
            }
        }
    }


    @Test
    fun saveCover_invalidImage_throwsIOException() {

        val invalidFile = File(
            context.cacheDir,
            "not_an_image.txt",
        ).apply {
            writeText(
                "This file is not an image."
            )
        }

        val uri = Uri.fromFile(invalidFile)

        assertThrows(IOException::class.java) {
            runBlocking {
                storage.saveCover(
                    bookId = "book_1",
                    image = AndroidSelectedImage(uri),
                )
            }
        }
    }


    @Test
    fun saveCover_nonAndroidImage_throwsIllegalArgumentException() {

        val image = FakeSelectedImage()

        assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                storage.saveCover(
                    bookId = "book_1",
                    image = image,
                )
            }
        }
    }


    @Test
    fun deleteCover_existingCover_deletesFile() =
        runBlocking {

            val uri = createTestImage(
                width = 800,
                height = 1200,
            )

            val fileName = storage.saveCover(
                bookId = "book_1",
                image = AndroidSelectedImage(uri),
            )

            val coverFile = getCoverFile(fileName)

            assertTrue(coverFile.exists())

            storage.deleteCover(fileName)

            assertFalse(coverFile.exists())
        }


    @Test
    fun deleteCover_missingCover_doesNotThrow() =
        runBlocking {

            storage.deleteCover(
                "cover_that_does_not_exist.webp",
            )

            val file = getCoverFile(
                "cover_that_does_not_exist.webp",
            )

            assertFalse(file.exists())
        }


    private fun createTestImage(
        width: Int,
        height: Int,
    ): Uri {

        val bitmap = Bitmap.createBitmap(
            width,
            height,
            Bitmap.Config.ARGB_8888,
        )

        bitmap.eraseColor(Color.RED)

        val sourceFile = File(
            context.cacheDir,
            "source_${width}x${height}_${System.nanoTime()}.png",
        )

        sourceFile.outputStream().use { outputStream ->
            val success = bitmap.compress(
                Bitmap.CompressFormat.PNG,
                100,
                outputStream,
            )

            check(success)
        }

        bitmap.recycle()

        return Uri.fromFile(sourceFile)
    }


    private fun getCoverFile(
        fileName: String,
    ): File {
        return File(
            File(
                context.filesDir,
                "book_covers",
            ),
            fileName,
        )
    }


    private class FakeSelectedImage : SelectedImage
}