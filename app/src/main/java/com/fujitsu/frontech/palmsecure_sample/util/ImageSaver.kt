package com.fujitsu.frontech.palmsecure_sample.util

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ImageSaver(private val context: Context) {
    companion object {
        private const val TAG = "ImageSaver"
        private const val DIRECTORY_NAME = "PalmSecure"
    }

    private val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

    fun savePalmImage(bitmap: Bitmap): String? {
        val directory = getOutputDirectory()
        if (!directory.exists() && !directory.mkdirs()) {
            Log.e(TAG, "Failed to create directory")
            return null
        }

        val filename = "Palm_${dateFormat.format(Date())}.jpg"
        val file = File(directory, filename)

        return try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
            // Make the image visible in gallery
            MediaScannerConnection.scanFile(
                context,
                arrayOf(file.toString()),
                null,
                null
            )
            file.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save image", e)
            null
        }
    }

    private fun getOutputDirectory(): File {
        val mediaDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.let {
            File(it, DIRECTORY_NAME).apply { mkdirs() }
        }
        return mediaDir ?: context.filesDir
    }
}