/*
 * PsThreadCancel.java
 *
 *	All Rights Reserved, Copyright(c) FUJITSU FRONTECH LIMITED 2021
 */

package com.fujitsu.frontech.palmsecure_sample.service;

import android.util.Log;

import com.fujitsu.frontech.palmsecure.JAVA_uint32;
import com.fujitsu.frontech.palmsecure.PalmSecureIf;
import com.fujitsu.frontech.palmsecure.util.PalmSecureConstant;
import com.fujitsu.frontech.palmsecure.util.PalmSecureException;
import com.fujitsu.frontech.palmsecure_sample.data.PsThreadResult;
import com.fujitsu.frontech.palmsecure_gui_sample.BuildConfig;

public class PsThreadCancel extends PsThreadBase {

	public PsThreadCancel(PsService service, PalmSecureIf palmsecureIf, JAVA_uint32 moduleHandle) {

		super("PsThreadCancel", service, palmsecureIf, moduleHandle, "", 0, 0, 0);
	}

	public void run() {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "run");
		}

		PsThreadResult stResult = new PsThreadResult();

		//Cancel
		///////////////////////////////////////////////////////////////////////////
		try {
			stResult.result = palmsecureIf.JAVA_PvAPI_Cancel(
					moduleHandle,
					stResult.errInfo);

			//If PalmSecure method failed, get error info
			if (stResult.result != PalmSecureConstant.JAVA_BioAPI_OK) {
				palmsecureIf.JAVA_PvAPI_GetErrorInfo(stResult.errInfo);
			}
		} catch (PalmSecureException e) {
			if (BuildConfig.DEBUG) {
				Log.e(TAG, "Cancel", e);
			}
			stResult.result = PalmSecureConstant.JAVA_BioAPI_ERRCODE_FUNCTION_FAILED;
			stResult.pseErrNumber = e.ErrNumber;
		}
		///////////////////////////////////////////////////////////////////////////

		Ps_Sample_Apl_Java_NotifyResult_Cancel(stResult);

		return;
	}
}
