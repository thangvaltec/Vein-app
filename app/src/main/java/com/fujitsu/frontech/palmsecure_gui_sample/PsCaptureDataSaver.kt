package com.fujitsu.frontech.palmsecure_gui_sample

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility class to save PalmSecure capture data as .dat files
 */
class PsCaptureDataSaver(private val context: Context) {
    companion object {
        private const val FILE_PREFIX = "capture_"
        private const val FILE_EXTENSION = ".dat"
        private const val DATE_FORMAT = "yyyyMMdd_HHmmss"
    }

    /**
     * Saves raw capture data to a .dat file in the app's external files directory
     * @param captureData The raw palm vein capture data from PalmSecure
     * @return The absolute path to the saved file, or null if saving failed
     */
    fun saveCaptureData(captureData: ByteArray): String? {
        try {
            // Create timestamp for unique filename
            val timestamp = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(Date())
            val filename = "$FILE_PREFIX$timestamp$FILE_EXTENSION"

            // Get app's external files directory
            val directory = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                ?: return null

            // Ensure directory exists
            if (!directory.exists()) {
                directory.mkdirs()
            }

            // Create output file
            val outputFile = File(directory, filename)

            // Write raw capture data to file
            FileOutputStream(outputFile).use { fos ->
                fos.write(captureData)
                fos.flush()
            }

            return outputFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}