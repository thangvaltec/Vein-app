/*
* GUICallback.kt
*All Rights Reserved, Copyright(c) FUJITSU FRONTECH LIMITED 2025
*	All Rights Reserved, Copyright(c) FUJITSU FRONTECH LIMITED 2025
*/

package com.fujitsu.frontech.palmsecure_gui_sample

import android.graphics.BitmapFactory
import com.fujitsu.frontech.palmsecure.JAVA_BioAPI_GUI_BITMAP
import com.fujitsu.frontech.palmsecure.JAVA_BioAPI_GUI_STATE_CALLBACK_EX_IF
import com.fujitsu.frontech.palmsecure.JAVA_BioAPI_GUI_STATE_CALLBACK_IF
import com.fujitsu.frontech.palmsecure.JAVA_BioAPI_GUI_STREAMING_CALLBACK_IF
import com.fujitsu.frontech.palmsecure.JAVA_PvAPI_GUI_GUIDE
import com.fujitsu.frontech.palmsecure.util.PalmSecureConstant
import com.fujitsu.frontech.palmsecure_sample.service.PsService


class GUIStateCallback(private val listener: GUISampleListener, private val service: PsService)
    : JAVA_BioAPI_GUI_STATE_CALLBACK_EX_IF
{
    private val context = service.baseContext

//    private val TAG = "PsStateCallback"
    private var mMessage: Long = 0
    private var mPosture: Posture = Posture(0f, 0f, 0f, 0f, 0f, 0f, Status.NONE)

    data class Counter(val message: Long) {
        var count: Int = 0
    }
    private var counter = arrayOf(
        Counter(PalmSecureConstant.JAVA_PvAPI_NOTIFY_CAP_GUID_NO_HANDS),
        Counter(PalmSecureConstant.JAVA_PvAPI_NOTIFY_CAP_GUID_LESSINFO)
    )

    companion object {
        private val GUI_STATE_FLAG = intArrayOf(
            PalmSecureConstant.JAVA_PvAPI_CALLBACK_GUID_STATE_STEP0.toInt(),
            PalmSecureConstant.JAVA_PvAPI_CALLBACK_GUID_STATE_STEP1.toInt(),
            PalmSecureConstant.JAVA_PvAPI_CALLBACK_GUID_STATE_STEP2.toInt(),
            PalmSecureConstant.JAVA_PvAPI_CALLBACK_GUID_STATE_STEP3.toInt()
        )
    }

    override fun JAVA_BioAPI_GUI_STATE_CALLBACK(
        guiStateCallbackCtx: Any?,
        guiState: Long,
        response: Short,
        message: Long,
        progress: Short,
        sampleBuffer: JAVA_BioAPI_GUI_BITMAP?,
        guideBuffer: JAVA_PvAPI_GUI_GUIDE?
    ): Long {

        if ((guiState and PalmSecureConstant.JAVA_PvAPI_APPEND_GUIDE) == PalmSecureConstant.JAVA_PvAPI_APPEND_GUIDE) {

            //Get guide info
            if (guideBuffer != null) {
                val status = when {
                    (guideBuffer.State.toInt() and GUI_STATE_FLAG[3]) !=0 -> Status.ALL
                    (guideBuffer.State.toInt() and GUI_STATE_FLAG[2]) !=0 -> Status.ONLY_XYZ_AND_YAW
                    (guideBuffer.State.toInt() and GUI_STATE_FLAG[1]) !=0 -> Status.ONLY_XYZ
                    (guideBuffer.State.toInt() and GUI_STATE_FLAG[0]) !=0 -> Status.ONLY_Z
                    else -> Status.NONE
                }
                //Log.d(TAG,"St:{$status}")

                var posture = Posture(
                    guideBuffer.Pos[0].toFloat(), guideBuffer.Pos[1].toFloat(), guideBuffer.Pos[2].toFloat(),
                    guideBuffer.Deg[0].toFloat(), guideBuffer.Deg[1].toFloat(), guideBuffer.Deg[2].toFloat(),
                    status
                )
                if ((posture.status == Status.ONLY_Z)
                    && (Constants.DIST_MIN_VERIFY < posture.z && posture.z < Constants.DIST_OFFSET_VERIFY)) {
                    if (mPosture.status == Status.ALL) posture = mPosture // recovery
                }
                listener.guiSampleNotifyPosture(posture)
                mPosture = posture
            }
            return PalmSecureConstant.JAVA_BioAPI_OK
        }

        return JAVA_BioAPI_GUI_STATE_CALLBACK(guiStateCallbackCtx,
            guiState, response, message, progress, sampleBuffer)
    }

    override fun JAVA_BioAPI_GUI_STATE_CALLBACK(
        guiStateCallbackCtx: Any?,
        guiState: Long,
        response: Short,
        message: Long,
        progress: Short,
        sampleBuffer: JAVA_BioAPI_GUI_BITMAP?
    ): Long {

        if ((guiState and PalmSecureConstant.JAVA_BioAPI_MESSAGE_PROVIDED) == PalmSecureConstant.JAVA_BioAPI_MESSAGE_PROVIDED) {

            //Get number of capture
            if ((message and -0x100) == PalmSecureConstant.JAVA_PvAPI_NOTIFY_CAP_GUID_START) {
                if (service.enrollFlg) {
                    val count = (message and 0x0000000f).toInt()
                    val s = String.format(
                        context.getString(R.string.WorkEnroll), count
                    )
                    listener.guiSampleNotifyProgressMessage(s)
                    listener.guiSampleNotifyCount(count)
                }

                mMessage = 0
                mPosture = Posture(0f, 0f, 0f, 0f, 0f, 0f, Status.NONE)
                return PalmSecureConstant.JAVA_BioAPI_OK
            }

            when (message) {
                PalmSecureConstant.JAVA_PvAPI_NOTIFY_CAP_GUID_NO_HANDS -> {
                    //Notify NO_HANDS
                    listener.guiSampleNotifyPosture(Posture(0f, 0f, 0f, 0f, 0f, 0f, Status.NO_HANDS))
                }
                PalmSecureConstant.JAVA_PvAPI_NOTIFY_CAP_GUID_BADIMAGE -> {
                    //Notify AWAY_HANDS
                    listener.guiSampleNotifyPosture(Posture(0f, 0f, 0f, 0f, 0f, 0f, Status.AWAY_HANDS))
                }
            }
            counter.forEach { it.count = if (it.message == message) it.count+1 else 0 }
            val flow = counter.any { it.count >= 2 }

            if (message == mMessage && !flow) return PalmSecureConstant.JAVA_BioAPI_OK
            mMessage = message

            //Get a guidance message
            val key = when (message) {
                PalmSecureConstant.JAVA_PvAPI_NOTIFY_CAP_GUID_MOVING -> {
                    R.string.NOTIFY_CAP_GUID_MOVING
                }
                PalmSecureConstant.JAVA_PvAPI_NOTIFY_CAP_GUID_NO_HANDS -> {
                    R.string.NOTIFY_CAP_GUID_NO_HANDS
                }
                PalmSecureConstant.JAVA_PvAPI_NOTIFY_CAP_GUID_LESSINFO -> {
                    R.string.NOTIFY_CAP_GUID_LESSINFO
                }
                PalmSecureConstant.JAVA_PvAPI_NOTIFY_CAP_GUID_FAR -> {
                    R.string.NOTIFY_CAP_GUID_FAR
                }
                PalmSecureConstant.JAVA_PvAPI_NOTIFY_CAP_GUID_NEAR -> {
                    R.string.NOTIFY_CAP_GUID_NEAR
                }
                PalmSecureConstant.JAVA_PvAPI_NOTIFY_CAP_GUID_CAPTURING -> {
                    R.string.NOTIFY_CAP_GUID_CAPTURING
                }
                PalmSecureConstant.JAVA_PvAPI_NOTIFY_CAP_GUID_PHASE_END -> {
                    R.string.NOTIFY_CAP_GUID_PHASE_END
                }
                PalmSecureConstant.JAVA_PvAPI_NOTIFY_CAP_GUID_CAPTURING_START -> {
                    R.string.NOTIFY_CAP_GUID_START
                }
                PalmSecureConstant.JAVA_PvAPI_NOTIFY_CAP_GUID_CAPTURING_END -> {
                    R.string.NOTIFY_CAP_GUID_CAPTURING_END
                }
                PalmSecureConstant.JAVA_PvAPI_NOTIFY_CAP_GUID_RIGHT -> {
                    R.string.NOTIFY_CAP_GUID_RIGHT
                }
                PalmSecureConstant.JAVA_PvAPI_NOTIFY_CAP_GUID_LEFT -> {
                    R.string.NOTIFY_CAP_GUID_LEFT
                }
                PalmSecureConstant.JAVA_PvAPI_NOTIFY_CAP_GUID_DOWN -> {
                    R.string.NOTIFY_CAP_GUID_DOWN
                }
                PalmSecureConstant.JAVA_PvAPI_NOTIFY_CAP_GUID_UP -> {
                    R.string.NOTIFY_CAP_GUID_UP
                }
                PalmSecureConstant.JAVA_PvAPI_NOTIFY_CAP_GUID_PITCH_DOWN -> {
                    R.string.NOTIFY_CAP_GUID_PITCH_DOWN
                }
                PalmSecureConstant.JAVA_PvAPI_NOTIFY_CAP_GUID_PITCH_UP -> {
                    R.string.NOTIFY_CAP_GUID_PITCH_UP
                }
                PalmSecureConstant.JAVA_PvAPI_NOTIFY_CAP_GUID_ROLL_RIGHT -> {
                    R.string.NOTIFY_CAP_GUID_ROLL_RIGHT
                }
                PalmSecureConstant.JAVA_PvAPI_NOTIFY_CAP_GUID_ROLL_LEFT -> {
                    R.string.NOTIFY_CAP_GUID_ROLL_LEFT
                }
                PalmSecureConstant.JAVA_PvAPI_NOTIFY_CAP_GUID_YAW_RIGHT -> {
                    R.string.NOTIFY_CAP_GUID_YAW_RIGHT
                }
                PalmSecureConstant.JAVA_PvAPI_NOTIFY_CAP_GUID_YAW_LEFT -> {
                    R.string.NOTIFY_CAP_GUID_YAW_LEFT
                }
                PalmSecureConstant.JAVA_PvAPI_NOTIFY_CAP_GUID_ROUND -> {
                    R.string.NOTIFY_CAP_GUID_ROUND
                }
                PalmSecureConstant.JAVA_PvAPI_NOTIFY_CAP_GUID_ADJUST_LIGHT -> {
                    R.string.NOTIFY_CAP_GUID_ADJUST_LIGHT
                }
                PalmSecureConstant.JAVA_PvAPI_NOTIFY_CAP_GUID_ADJUST_NG -> {
                    R.string.NOTIFY_CAP_GUID_ADJUST_NG
                }
                PalmSecureConstant.JAVA_PvAPI_NOTIFY_CAP_GUID_BADIMAGE -> {
                    R.string.NOTIFY_CAP_GUID_BADIMAGE
                }
                else -> {
                    0
                }
            }
            if (!flow && counter.any { it.message == message }
                && (mPosture.status.ordinal >= Status.ONLY_Z.ordinal)
                && (Constants.DIST_MIN_VERIFY < mPosture.z && mPosture.z < Constants.DIST_OFFSET_VERIFY)) {
                // ignore
            }
            else {
                if (key > 0) {
                    listener.guiSampleNotifyGuidance(context.getString(key))
                }
                counter.forEach { it.count = 0 }
            }
        }

        return PalmSecureConstant.JAVA_BioAPI_OK
    }
}

class GUIStreamingCallback(private val listener: GUISampleListener)
    : JAVA_BioAPI_GUI_STREAMING_CALLBACK_IF {

    override fun JAVA_BioAPI_GUI_STREAMING_CALLBACK(
        guiStremingCallbackCtx: Any?,
        bitmap: JAVA_BioAPI_GUI_BITMAP?
    ): Long {

        if (bitmap != null) {
            val bmp = BitmapFactory.decodeByteArray(
                bitmap.Bitmap.Data, 0,
                bitmap.Bitmap.Length.toInt()
            )
            listener.guiSampleNotifySilhouette(bmp)
        }
        return PalmSecureConstant.JAVA_BioAPI_OK
    }
}