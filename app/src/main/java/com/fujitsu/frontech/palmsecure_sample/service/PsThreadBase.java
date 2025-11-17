/*
 * PsThreadBase.java
 *
 *	All Rights Reserved, Copyright(c) FUJITSU FRONTECH LIMITED 2021
 */

package com.fujitsu.frontech.palmsecure_sample.service;

import android.os.Bundle;
import android.util.Log;

import com.fujitsu.frontech.palmsecure.JAVA_uint32;
import com.fujitsu.frontech.palmsecure.PalmSecureIf;
import com.fujitsu.frontech.palmsecure_gui_sample.BuildConfig;
import com.fujitsu.frontech.palmsecure_sample.data.PsThreadResult;

public abstract class PsThreadBase extends Thread {

	protected String TAG = "";
	protected PalmSecureIf palmsecureIf = null;
	protected JAVA_uint32 moduleHandle = null;
	protected String userID = null;
	protected PsService service = null;
	protected int numberOfRetry = 0;
	protected int sleepTime = 0;
	protected long maxResults = 0;
	protected PsThreadResult result = null;

	public PsThreadResult getResult() {
		return result;
	}

	protected void setResult(PsThreadResult result) {
		this.result = result;
	}

	protected PsThreadBase(String tag, PsService service, PalmSecureIf palmsecureIf, JAVA_uint32 moduleHandle,
			String userID, int numberOfRetry, int sleepTime, long maxResults) {

		this.TAG = tag;
		this.palmsecureIf = palmsecureIf;
		this.service = service;
		this.moduleHandle = moduleHandle;
		this.userID = userID;
		this.numberOfRetry = numberOfRetry;
		this.sleepTime = sleepTime;
		this.maxResults = maxResults;
	}

	protected PsThreadBase(PalmSecureIf palmsecureIf, JAVA_uint32 moduleHandle) {
		this.TAG = getClass().getSimpleName();
		this.palmsecureIf = palmsecureIf;
		this.moduleHandle = moduleHandle;
	}

	protected void Ps_Sample_Apl_Java_NotifyWorkMessage(int processKey) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Ps_Sample_Apl_Java_NotifyWorkMessage(1)");
		}

		Bundle b = new Bundle();
		b.putInt(PsService.RESPONSE, PsService.MSG_RESPONSE_MESSAGE);
		PsServiceHelper.putProcessKeyToBundle(b, processKey);
		service.handleNotify(b);
	}

	protected void Ps_Sample_Apl_Java_NotifyWorkMessage(int processKey, int count) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Ps_Sample_Apl_Java_NotifyWorkMessage(2)");
		}

		Bundle b = new Bundle();
		b.putInt(PsService.RESPONSE, PsService.MSG_RESPONSE_MESSAGE_COUNT);
		PsServiceHelper.putProcessKeyToBundle(b, processKey);
		PsServiceHelper.putCountToBundle(b, count);
		service.handleNotify(b);
	}

	protected void Ps_Sample_Apl_Java_NotifyGuidance(int guidanceKey, boolean error) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Ps_Sample_Apl_Java_NotifyGuidance");
		}

		Bundle b = new Bundle();
		b.putInt(PsService.RESPONSE, PsService.MSG_RESPONSE_GUIDANCE);
		PsServiceHelper.putGuidanceKeyToBundle(b, guidanceKey);
		PsServiceHelper.putGuidanceErrorToBundle(b, error);
		service.handleNotify(b);
	}

	protected void Ps_Sample_Apl_Java_NotifyResult(int msg, PsThreadResult stResult) {

		Bundle b = new Bundle();
		b.putInt(PsService.RESPONSE, msg);
		PsServiceHelper.putPsThreadResultToBundle(b, stResult);
		service.handleNotify(b);
	}

	protected void Ps_Sample_Apl_Java_NotifyResult_Enroll(PsThreadResult stResult, int enrollscore) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Ps_Sample_Apl_Java_NotifyResult_Enroll");
		}

		Ps_Sample_Apl_Java_NotifyResult(PsService.MSG_RESPONSE_ENROLL, stResult);
	}

	protected void Ps_Sample_Apl_Java_NotifyResult_Verify(PsThreadResult stResult) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Ps_Sample_Apl_Java_NotifyResult_Verify");
		}

		Ps_Sample_Apl_Java_NotifyResult(PsService.MSG_RESPONSE_VERIFY, stResult);
	}

	protected void Ps_Sample_Apl_Java_NotifyResult_Identify(PsThreadResult stResult) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Ps_Sample_Apl_Java_NotifyResult_Identify");
		}

		Ps_Sample_Apl_Java_NotifyResult(PsService.MSG_RESPONSE_IDENTIFY, stResult);
	}

	protected void Ps_Sample_Apl_Java_NotifyResult_Cancel(PsThreadResult stResult) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Ps_Sample_Apl_Java_NotifyResult_Cancel");
		}

		Ps_Sample_Apl_Java_NotifyResult(PsService.MSG_RESPONSE_CANCEL, stResult);
	}

	protected void Ps_Sample_Apl_Java_NotifyOffset(boolean enroll) {

		Bundle b = new Bundle();
		b.putInt(PsService.RESPONSE, PsService.MSG_RESPONSE_OFFSET);
		b.putBoolean("OffsetEnroll", enroll);
		service.handleNotify(b);
	}
}
