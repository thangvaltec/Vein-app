package com.fujitsu.frontech.palmsecure_sample.service

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.fujitsu.frontech.palmsecure_sample.data.PsThreadResult
import com.fujitsu.frontech.palmsecure_sample.util.ImageSaver
import java.io.File

class PsThreadSaveImage(
    private val context: Context,
    private val bitmap: Bitmap
) : Thread() {
    companion object {
        private const val TAG = "PsThreadSaveImage"
    }

    private var result: PsThreadResult? = null
    private var savedFilePath: String? = null

    override fun run() {
        val stResult = PsThreadResult()
        try {
            val imageSaver = ImageSaver(context)
            savedFilePath = imageSaver.savePalmImage(bitmap)
            
            if (savedFilePath != null) {
                Log.d(TAG, "Image saved successfully at: $savedFilePath")
                stResult.result = 0L // Success
            } else {
                Log.e(TAG, "Failed to save image")
                stResult.result = 1L // Failed
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error saving image", e)
            stResult.result = 1L
        }
        
        result = stResult
    }

    fun getResult(): PsThreadResult? {
        return result
    }

    fun getSavedFilePath(): String? {
        return savedFilePath
    }
}