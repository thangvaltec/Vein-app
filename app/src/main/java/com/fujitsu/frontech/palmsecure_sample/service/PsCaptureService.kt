package com.fujitsu.frontech.palmsecure_sample.service

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import com.fujitsu.frontech.palmsecure_sample.util.ImageSaver

/**
 * Lightweight compatibility stub for PsCaptureService.
 * If code still instantiates this, it will attempt to save the
 * latest silhouette bytes provided in PsService.silhouette. This
 * avoids calling low-level PalmSecure APIs that may not be exposed.
 */
class PsCaptureService(private val context: Context, private val service: PsService) {
    companion object {
        private const val TAG = "PsCaptureService"
    }

    fun captureAndSave(): String? {
        try {
            val bytes = service.silhouette
            if (bytes == null || bytes.isEmpty()) {
                Log.w(TAG, "No silhouette available to save")
                return null
            }
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            if (bmp == null) return null
            return ImageSaver(context).savePalmImage(bmp)
        } catch (e: Exception) {
            Log.e(TAG, "captureAndSave failed", e)
            return null
        }
    }
}