/*
* GUIHelper.kt
*
*	All Rights Reserved, Copyright(c) FUJITSU FRONTECH LIMITED 2025
*/

package com.fujitsu.frontech.palmsecure_gui_sample

import android.app.Activity
import android.util.Log
import com.fujitsu.frontech.palmsecure.util.PalmSecureConstant
import com.fujitsu.frontech.palmsecure_sample.data.PsThreadResult
import com.fujitsu.frontech.palmsecure_sample.xml.PsFileAccessorIni

data class InitParam (
    val appKey: String = "",
    val guideMode: Int = 0,
    val dataType: Int = 0
)

data class ThreadParam (
    val userId: String = "",
    val numberOfRetry: Int = 1,
    val sleepTime: Int = 0
)

class GUIHelper(private val activity: Activity) {
    private val ini: PsFileAccessorIni? = PsFileAccessorIni.GetInstance(activity)

    companion object {
        val PS_GEXTENDED_DATA_TYPE = intArrayOf(2, 3, 4)
    }

    fun createInitParam() : InitParam {
        if (ini == null) return InitParam()
        val usingGuideMode = ini.GetValueInteger(PsFileAccessorIni.GuideMode)
        val usingGExtendedMode = ini.GetValueInteger(PsFileAccessorIni.GExtendedMode)
        var usingDataType = usingGuideMode
        if (usingGExtendedMode >= 1) {
            usingDataType = PS_GEXTENDED_DATA_TYPE[usingGExtendedMode - 1]
        }

        return InitParam(
            ini.GetValueString(PsFileAccessorIni.ApplicationKey),
            usingGuideMode, usingDataType)
    }

    fun createThreadParam(userId: String) : ThreadParam {
        if (ini == null) return ThreadParam()
        return ThreadParam(
            userId,
            ini.GetValueInteger(PsFileAccessorIni.NumberOfRetry),
            ini.GetValueInteger(PsFileAccessorIni.SleepTime))
    }

    fun getErrorMessage(result: PsThreadResult) : String {
        var msg = ""
        if (result.result != PalmSecureConstant.JAVA_BioAPI_OK) {
            when {
                (result.pseErrNumber != 0) -> {
                    Log.e("GUIHelper", "PSE Error: pseErrNumber=${result.pseErrNumber}")
                    msg = String.format(
                        "%s: %X",
                        activity.getString(R.string.AplErrorTitle),
                        result.pseErrNumber
                    )
                }
                (result.messageKey != 0) -> {
                    Log.e("GUIHelper", "MessageKey Error: key=${result.messageKey}")
                    msg = activity.getString(result.messageKey)

                }
                (result.errInfo.ErrorLevel.toInt() != 0) -> {
                    Log.e(
                        "GUIHelper",
                        "Lib Error: level=${result.errInfo.ErrorLevel} detail=${result.errInfo.ErrorDetail}"
                    )
                    msg = String.format(
                        "%s: %X",
                        activity.getString(R.string.LibErrorTitle),
                        result.errInfo.ErrorDetail
                    )
                }
                else -> {
                    Log.e("GUIHelper", "Unknown Error: result=${result.result}")
                }
            }
        }
        return msg
    }
}