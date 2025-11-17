package com.fujitsu.frontech.palmsecure_sample.util

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility class for saving PalmSecure capture data to .dat files
 */
class PsCaptureDataSaver(private val context: Context) {
    companion object {
        private const val TAG = "PsCaptureDataSaver"
        private const val DIRECTORY_NAME = "PalmSecure"
        private const val FILE_EXTENSION = ".dat"
    }

    private val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

    /**
     * Saves palm vein capture data to a .dat file
     * @param captureData The raw palm vein capture data to save
     * @return The absolute path to the saved file, or null if saving failed
     */
    fun saveCaptureData(captureData: ByteArray): String? {
        val directory = getOutputDirectory()
        if (!directory.exists() && !directory.mkdirs()) {
            Log.e(TAG, "Failed to create directory")
            return null
        }

        val filename = "CaptureData_${dateFormat.format(Date())}$FILE_EXTENSION"
        val file = File(directory, filename)

        return try {
            FileOutputStream(file).use { out ->
                out.write(captureData)
            }
            file.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save capture data", e)
            null
        }
    }

    private fun getOutputDirectory(): File {
        val mediaDir = context.getExternalFilesDir(null)?.let {
            File(it, DIRECTORY_NAME).apply { mkdirs() }
        }
        return mediaDir ?: context.filesDir
    }
}