package com.pguillen.readingtracker.domain.storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.graphics.scale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class AndroidBookCoverStorage(
    private val context: Context
) : BookCoverStorage {
    private val coversDirectory: File
        get() = File(context.filesDir, "book_covers")

    override suspend fun saveCover(
        bookId: String,
        image: SelectedImage
    ): String = withContext(Dispatchers.IO) {
        val androidImage = image as? AndroidSelectedImage
            ?: throw IllegalArgumentException(
                "Expected AndroidSelectedImage"
            )
        val bitmap = context.contentResolver
            .openInputStream(androidImage.uri)
            ?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
            ?: throw IOException("Could not decode image")

        val croppedBitmap = centerCropToBookRatio(bitmap)
        val resizedBitmap = resizeBookCover(croppedBitmap)

        val coversDirectory = File(
            context.filesDir,
            "book_covers",
        )

        if (!coversDirectory.exists()) {
            coversDirectory.mkdirs()
        }

        val fileName =
            "book_${bookId}_${System.currentTimeMillis()}.webp"

        val outputFile = File(
            coversDirectory,
            fileName,
        )

        val success = outputFile
            .outputStream()
            .use { outputStream ->
                resizedBitmap.compress(
                    getWebpCompressFormat(),
                    85,
                    outputStream,
                )
            }

        if (!success) {
            outputFile.delete()
            throw IOException(
                "Could not compress book cover"
            )
        }

        fileName
    }

    override suspend fun deleteCover(fileName: String) = withContext(Dispatchers.IO) {
        val coverFile = File(
            coversDirectory,
            fileName,
        )

        if (!coverFile.exists()) {
            return@withContext
        }

        val deleted = coverFile.delete()

        if (!deleted) {
            throw IOException(
                "Could not delete book cover: $fileName"
            )
        }
    }
}

private fun centerCropToBookRatio(
    bitmap: Bitmap,
): Bitmap {
    val targetRatio = 86f / 126f
    val sourceRatio =
        bitmap.width.toFloat() / bitmap.height.toFloat()

    return if (sourceRatio > targetRatio) {
        // Imagen demasiado ancha
        val newWidth =
            (bitmap.height * targetRatio).toInt()

        val xOffset =
            (bitmap.width - newWidth) / 2

        Bitmap.createBitmap(
            bitmap,
            xOffset,
            0,
            newWidth,
            bitmap.height,
        )
    } else {
        // Imagen demasiado alta
        val newHeight =
            (bitmap.width / targetRatio).toInt()

        val yOffset =
            (bitmap.height - newHeight) / 2

        Bitmap.createBitmap(
            bitmap,
            0,
            yOffset,
            bitmap.width,
            newHeight,
        )
    }
}

private fun resizeBookCover(
    bitmap: Bitmap,
): Bitmap {
    return bitmap.scale(688, 1008)
}

private fun getWebpCompressFormat(): Bitmap.CompressFormat {
    return if (
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    ) {
        Bitmap.CompressFormat.WEBP_LOSSY
    } else {
        @Suppress("DEPRECATION")
        Bitmap.CompressFormat.WEBP
    }
}