/*
 * PsService.java
 *
 *	All Rights Reserved, Copyright(c) FUJITSU FRONTECH LIMITED 2021
 */

package com.fujitsu.frontech.palmsecure_sample.service;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.fujitsu.frontech.palmsecure_gui_sample.GUIHelper;
import com.fujitsu.frontech.palmsecure_gui_sample.GUISampleListener;
import com.fujitsu.frontech.palmsecure_gui_sample.GUIStateCallback;
import com.fujitsu.frontech.palmsecure_gui_sample.GUIStreamingCallback;
import com.fujitsu.frontech.palmsecure_gui_sample.InitParam;
import com.fujitsu.frontech.palmsecure_gui_sample.ThreadParam;
import com.fujitsu.frontech.palmsecure_gui_sample.Offset;
import com.fujitsu.frontech.palmsecure_gui_sample.Result;
import com.fujitsu.frontech.palmsecure_gui_sample.R;
import com.fujitsu.frontech.palmsecure_gui_sample.BuildConfig;
import com.fujitsu.frontech.palmsecure.JAVA_PvAPI_LBINFO;
import com.fujitsu.frontech.palmsecure.JAVA_uint32;
import com.fujitsu.frontech.palmsecure.PalmSecureIf;
import com.fujitsu.frontech.palmsecure.util.PalmSecureConstant;
import com.fujitsu.frontech.palmsecure.util.PalmSecureException;
import com.fujitsu.frontech.palmsecure_sample.data.PsDataManager;
import com.fujitsu.frontech.palmsecure_sample.data.PsThreadResult;
import com.fujitsu.frontech.palmsecure_sample.exception.PsAplException;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class PsService {

	private static final String TAG = "PsService";

	public static final String RESPONSE = "Response";
	public static final int MSG_RESPONSE_INIT_LIBRARY = 1010;
	public static final int MSG_RESPONSE_ATTACH = 1020;
	public static final int MSG_RESPONSE_TERM_LIBRARY = 1030;
	public static final int MSG_RESPONSE_ENROLL = 1040;
	public static final int MSG_RESPONSE_VERIFY = 1050;
	public static final int MSG_RESPONSE_IDENTIFY = 1060;
	public static final int MSG_RESPONSE_CANCEL = 1070;

	public static final int MSG_RESPONSE_MESSAGE = 2010;
	public static final int MSG_RESPONSE_MESSAGE_COUNT = 2011;
	public static final int MSG_RESPONSE_GUIDANCE = 2020;
	public static final int MSG_RESPONSE_SILHOUETTE = 2030;
	public static final int MSG_RESPONSE_OFFSET = 2040;

	public boolean cancelFlg = false;
	public boolean enrollFlg = false;
	public int notifiedScore = 0;
	private int mUsingDataType = 0;
	public int mUsingGuideMode = 0;
	private long mUsingSensorType = 0;
	public long mUsingSensorTypeReal = 0;
	private long mUsingSensorExtKind = 0;

	public byte[] silhouette = null;
	
	/**
	 * Get the current data type being used.
	 */
	public int getUsingDataType() {
		return mUsingDataType;
	}
	
	/**
	 * Get the current sensor type being used.
	 */
	public long getUsingSensorType() {
		return mUsingSensorType;
	}

	private Activity mActivity = null;
	private GUIStateCallback stateCB = null;
	private GUIStreamingCallback streamingCB = null;
	private PalmSecureIf palmsecureIf = null;
	private JAVA_uint32 moduleHandle = null;
	private GUISampleListener guiListener = null;

	/**
	 * Get the activity context. Used by threads to access resources and DB.
	 */
	public Activity getActivity() {
		return mActivity;
	}

	private static final byte[] ModuleGuid = new byte[] {
			(byte) 0xe1, (byte) 0x9a, (byte) 0x69, (byte) 0x01,
			(byte) 0xb8, (byte) 0xc2, (byte) 0x49, (byte) 0x80,
			(byte) 0x87, (byte) 0x7e, (byte) 0x11, (byte) 0xd4,
			(byte) 0xd8, (byte) 0xf1, (byte) 0xbe, (byte) 0x79
	};

	private ArrayList<String> idList = null;
	public static final String USER = "0000";

	/**
	 * Public getter for idList. Returns a shallow copy of the list to avoid
	 * exposing the internal mutable list directly to callers.
	 */
	public java.util.List<String> getIdList() {
		if (idList == null) return new ArrayList<String>();
		return new ArrayList<String>(idList);
	}

	public PsService(Activity activity, GUISampleListener listener) {
		mActivity = activity;
		guiListener = listener;
		stateCB = new GUIStateCallback(listener, this);
		streamingCB = new GUIStreamingCallback(listener);
	}


	public PsThreadResult Ps_Sample_Apl_Java_Request_InitLibrary(InitParam param) {
		if (BuildConfig.DEBUG)
			Log.d(TAG, "Ps_Sample_Apl_Java_Request_InitLibrary");

        return Ps_Sample_Apl_Java_InitLibrary(
				param.getAppKey(),
				param.getGuideMode(),
				param.getDataType(),
				stateCB, streamingCB);
	}

	public void Ps_Sample_Apl_Java_Request_TermLibrary() {
		if (BuildConfig.DEBUG)
			Log.d(TAG, "Ps_Sample_Apl_Java_Request_TermLibrary");

		Ps_Sample_Apl_Java_TermLibrary();
	}

	public void Ps_Sample_Apl_Java_Request_Enroll(ThreadParam param) {
		if (BuildConfig.DEBUG)
			Log.d(TAG, "Ps_Sample_Apl_Java_Request_Enroll");

		PsThreadEnroll threadEnroll = new PsThreadEnroll(
				this,
				palmsecureIf,
				moduleHandle,
				param.getUserId(),
				param.getNumberOfRetry(),
				param.getSleepTime());
		threadEnroll.start();
	}

	public void Ps_Sample_Apl_Java_Request_Verify(ThreadParam param) {
		if (BuildConfig.DEBUG)
			Log.d(TAG, "Ps_Sample_Apl_Java_Request_Verify");

		PsThreadVerify threadVerify = new PsThreadVerify(
				this,
				palmsecureIf,
				moduleHandle,
				param.getUserId(),
				param.getNumberOfRetry(),
				param.getSleepTime());
		threadVerify.start();
	}

	public void Ps_Sample_Apl_Java_Request_Identify(ThreadParam param) {
		if (BuildConfig.DEBUG)
			Log.d(TAG, "Ps_Sample_Apl_Java_Request_Identify");

		PsThreadIdentify threadIdentify = new PsThreadIdentify(
				this,
				palmsecureIf,
				moduleHandle,
				param.getNumberOfRetry(),
				param.getSleepTime());
		threadIdentify.start();
	}

	public void Ps_Sample_Apl_Java_Request_Cancel() {
		if (BuildConfig.DEBUG)
			Log.d(TAG, "Ps_Sample_Apl_Java_Request_Cancel");
		cancelFlg = true;

		PsThreadCancel threadCancel = new PsThreadCancel(
				this,
				palmsecureIf,
				moduleHandle);
		threadCancel.start();
	}

	public void handleNotify(Bundle response) {
		PsThreadResult result = null;
		int count = 0;
		String s = "";

		switch (response.getInt(RESPONSE)) {
		case MSG_RESPONSE_MESSAGE:
			s = mActivity.getResources().getString(PsServiceHelper.getBundleToProcessKey(response));
			guiListener.guiSampleNotifyProgressMessage(s);
			break;
		case MSG_RESPONSE_MESSAGE_COUNT:
			count = PsServiceHelper.getBundleToCount(response);
			s = String.format(
					mActivity.getResources().getString(PsServiceHelper.getBundleToProcessKey(response)),
					count);
			guiListener.guiSampleNotifyProgressMessage(s);
			if (PsServiceHelper.getBundleToProcessKey(response) == R.string.WorkEnrollTest) {
				guiListener.guiSampleNotifyCount(count+2);
			}
			break;
		case MSG_RESPONSE_GUIDANCE:
			s = mActivity.getResources().getString(PsServiceHelper.getBundleToGuidanceKey(response));
			guiListener.guiSampleNotifyGuidance(s);
			break;
		case MSG_RESPONSE_OFFSET:
			guiListener.guiSampleNotifyOffset(
					response.getBoolean("OffsetEnroll") ? Offset.ENROLL : Offset.VERIFY);
			break;

		case MSG_RESPONSE_ENROLL:
			result = PsServiceHelper.getBundleToPsThreadResult(response);
			if (cancelFlg) {
				s = mActivity.getResources().getString(R.string.EnrollCancel);
				guiListener.guiSampleNotifyGuidance(s);
				guiListener.guiSampleNotifyResult(Result.CANCELED);
				cancelFlg = false;
			}
			else if (result.result != PalmSecureConstant.JAVA_BioAPI_OK) {
				GUIHelper helper = new GUIHelper(mActivity);
				s = helper.getErrorMessage(result);
				guiListener.guiSampleNotifyGuidance(s);
				guiListener.guiSampleNotifyResult(Result.ERROR);
			}
			else if (!result.authenticated) {
				s = mActivity.getResources().getString(R.string.EnrollNg);
				guiListener.guiSampleNotifyGuidance(s);
				guiListener.guiSampleNotifyResult(Result.FAILED);
			}
			else {
				// Get verified user ID from result
				String verifiedUserId = result.userId != null && !result.userId.isEmpty() ? 
					result.userId.get(0) : "Unknown";
				s = String.format("%s - User ID: %s",
					mActivity.getResources().getString(R.string.VerifyOk),
					verifiedUserId);
				guiListener.guiSampleNotifyGuidance(s);
				guiListener.guiSampleNotifyResult(Result.SUCCESSFUL);
				idList.add(USER);
			}
			break;
		case MSG_RESPONSE_VERIFY:
			result = PsServiceHelper.getBundleToPsThreadResult(response);
			// Extract requested/target user ID used for verification (if present)
			String requestedUserId = "Unknown";
			if (result != null && result.userId != null && !result.userId.isEmpty()) {
				requestedUserId = result.userId.get(0);
			}
			if (cancelFlg) {
				s = mActivity.getResources().getString(R.string.VerifyCancel);
				guiListener.guiSampleNotifyGuidance(s);
				guiListener.guiSampleNotifyResult(Result.CANCELED);
				cancelFlg = false;
			}
			else if (result.result != PalmSecureConstant.JAVA_BioAPI_OK) {
				GUIHelper helper = new GUIHelper(mActivity);
				s = helper.getErrorMessage(result);
				guiListener.guiSampleNotifyGuidance(s);
				guiListener.guiSampleNotifyResult(Result.ERROR);
			}
			else if (!result.authenticated) {
				s = mActivity.getResources().getString(R.string.VerifyNg, requestedUserId);
				guiListener.guiSampleNotifyGuidance(s);
				guiListener.guiSampleNotifyResult(Result.FAILED);
			}
			else {
				// Extract verified user ID from PsThreadResult (sent as String ArrayList)
				String verifiedUserId = "Unknown";
				if (result != null && result.userId != null && !result.userId.isEmpty()) {
					verifiedUserId = result.userId.get(0);
				}
				s = String.format("%s - User ID: %s",
					mActivity.getResources().getString(R.string.VerifyOk),
					verifiedUserId);
				guiListener.guiSampleNotifyGuidance(s);
				guiListener.guiSampleNotifyResult(Result.SUCCESSFUL);
			}
			break;
			
		case MSG_RESPONSE_IDENTIFY:
			result = PsServiceHelper.getBundleToPsThreadResult(response);
			if (cancelFlg) {
				s = mActivity.getResources().getString(R.string.IdentifyCancel);
				guiListener.guiSampleNotifyGuidance(s);
				guiListener.guiSampleNotifyResult(Result.CANCELED);
				cancelFlg = false;
			}
			else if (result.result != PalmSecureConstant.JAVA_BioAPI_OK) {
				GUIHelper helper = new GUIHelper(mActivity);
				s = helper.getErrorMessage(result);
				guiListener.guiSampleNotifyGuidance(s);
				guiListener.guiSampleNotifyResult(Result.ERROR);
			}
			else if (!result.authenticated) {
				s = mActivity.getResources().getString(R.string.IdentifyNg);
				guiListener.guiSampleNotifyGuidance(s);
				guiListener.guiSampleNotifyResult(Result.FAILED);
			}
			else {
				// Get matched user ID from result
				String matchedUserId = "Unknown";
				if (result != null && result.userId != null && !result.userId.isEmpty()) {
					matchedUserId = result.userId.get(0);
				}
				s = String.format(mActivity.getResources().getString(R.string.IdentifyOk), matchedUserId);
				guiListener.guiSampleNotifyGuidance(s);
                guiListener.guiSampleGetUserId(matchedUserId);
				guiListener.guiSampleNotifyResult(Result.SUCCESSFUL);
			}
			break;
		case MSG_RESPONSE_CANCEL:
			break;
		default:
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "handleNotify : default.");
			}
			break;
		}

	}

	public void Ps_Sample_Apl_Java_InitAuthDataFile() {
		if (BuildConfig.DEBUG)
			Log.d(TAG, "Ps_Sample_Apl_Java_InitAuthDataFile");
		FileOutputStream os = null;
		ZipInputStream zis = null;
        try {
			byte[] buf = new byte[1024];
			int size;
			zis = new ZipInputStream(mActivity.getResources().openRawResource(
					R.raw.authdatafile));
			ZipEntry ze;
			while ((ze = zis.getNextEntry()) != null) {
                try {
                    os = mActivity.openFileOutput(ze.getName(), Context.MODE_PRIVATE);
                    while ((size = zis.read(buf, 0, buf.length)) > 0) {
                        os.write(buf, 0, size);
                    }
                } finally {
                    if (os != null) {
                        try { os.close(); } catch (Exception ignore) {}
                        os = null;
                    }
                }
			}
            // Ensure PalmSecureSample.ini exists even if not present in ZIP
            File ini = new File(mActivity.getFilesDir(), "PalmSecureSample.ini");
            if (!ini.exists()) {
                try {
                    InputStream is = mActivity.getResources().openRawResource(R.raw.palmsecuresample);
                    os = mActivity.openFileOutput("PalmSecureSample.ini", Context.MODE_PRIVATE);
                    while ((size = is.read(buf, 0, buf.length)) > 0) {
                        os.write(buf, 0, size);
                    }
                    is.close();
                } catch (Exception ignore) {
                    // Fallback best-effort; if resource missing, ignore here
                }
            }
            // Final fallback: create default minimal INI if still missing or empty
            if (!ini.exists() || ini.length() == 0) {
                String defaultIni = "<?xml version=\"1.0\"?>\n" +
                        "<SettingData xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n" +
                        "  <ApplicationKey></ApplicationKey>\n" +
                        "  <MessageLevel>0</MessageLevel>\n" +
                        "</SettingData>\n";
                os = mActivity.openFileOutput("PalmSecureSample.ini", Context.MODE_PRIVATE);
                os.write(defaultIni.getBytes(StandardCharsets.UTF_8));
                os.close();
            }
		} catch (Exception e) {
            if (BuildConfig.DEBUG)
                Log.e(TAG, "InitAuthDataFile error", e);
		} finally {
			try {
				if (zis != null)
					zis.close();
                if (os != null)
                    os.close();
			} catch (Exception e) {
                if (BuildConfig.DEBUG)
                    Log.e(TAG, "InitAuthDataFile close error", e);
			}
		}
	}

	/**
	 * Initialize the Authentication library.
	 */
	private PsThreadResult Ps_Sample_Apl_Java_InitLibrary(String aplKey, int guideMode, int dataType,
														 GUIStateCallback stateCB,
														 GUIStreamingCallback streamingCB) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Ps_Sample_Apl_Java_InitLibrary: aplKey=" + aplKey
				+ " guideMode=" + guideMode + " dataType=" + dataType);
		}

		mUsingDataType = dataType;

		PsThreadResult stResult = new PsThreadResult();
		JAVA_PvAPI_LBINFO lbInfo = new JAVA_PvAPI_LBINFO();

		//Create a instance of PalmSecureIf class
		///////////////////////////////////////////////////////////////////////////
		try {
			palmsecureIf = new PalmSecureIf(getBaseContext());
		} catch (PalmSecureException e) {
			if (BuildConfig.DEBUG) {
				Log.e(TAG, "Create a instance of PalmSecureIf class", e);
			}
			stResult.result = PalmSecureConstant.JAVA_BioAPI_ERRCODE_FUNCTION_FAILED;
			stResult.pseErrNumber = e.ErrNumber;
			return stResult;
		}
		///////////////////////////////////////////////////////////////////////////

		//Authenticate application by key
		///////////////////////////////////////////////////////////////////////////
		try {
			stResult.result = palmsecureIf.JAVA_PvAPI_ApAuthenticate(aplKey);
			if (stResult.result != PalmSecureConstant.JAVA_BioAPI_OK) {
				palmsecureIf.JAVA_PvAPI_GetErrorInfo(stResult.errInfo);
				if (BuildConfig.DEBUG) {
					Log.e(TAG, "Authenticate application by key, PalmSecure method failed");
				}
				return stResult;
			}
		} catch (PalmSecureException e) {
			if (BuildConfig.DEBUG) {
				Log.e(TAG, "Authenticate application by key", e);
			}
			stResult.result = PalmSecureConstant.JAVA_BioAPI_ERRCODE_FUNCTION_FAILED;
			stResult.pseErrNumber = e.ErrNumber;
			return stResult;
		}
		if (BuildConfig.DEBUG)
			Log.d(TAG, "PvAPI_ApAuthenticate Done!");
		///////////////////////////////////////////////////////////////////////////

		//Load module
		///////////////////////////////////////////////////////////////////////////
		try {
			stResult.result = palmsecureIf.JAVA_BioAPI_ModuleLoad(ModuleGuid, null, null, null);
			if (stResult.result != PalmSecureConstant.JAVA_BioAPI_OK) {
				palmsecureIf.JAVA_PvAPI_GetErrorInfo(stResult.errInfo);
				if (BuildConfig.DEBUG) {
					Log.e(TAG, "Load module, PalmSecure method failed");
				}
				return stResult;
			}
		} catch (PalmSecureException e) {
			if (BuildConfig.DEBUG) {
				Log.e(TAG, "Load module", e);
			}
			stResult.result = PalmSecureConstant.JAVA_BioAPI_ERRCODE_FUNCTION_FAILED;
			stResult.pseErrNumber = e.ErrNumber;
			return stResult;
		}
		if (BuildConfig.DEBUG)
			Log.d(TAG, "BioAPI_ModuleLoad Done!");
		///////////////////////////////////////////////////////////////////////////

		//Set GExtended mode
		///////////////////////////////////////////////////////////////////////////
		JAVA_uint32 uiFlag = new JAVA_uint32();
		uiFlag.value = PalmSecureConstant.JAVA_PvAPI_PRE_PROFILE_G_EXTENDED_MODE;
		JAVA_uint32 lpvParamData = new JAVA_uint32();
		if ( mUsingDataType == 0 || mUsingDataType == 1 ) {
			lpvParamData.value = (int)PalmSecureConstant.JAVA_PvAPI_PRE_PROFILE_G_EXTENDED_MODE_OFF;
		} else if ( mUsingDataType == 2 ){
			lpvParamData.value = (int)PalmSecureConstant.JAVA_PvAPI_PRE_PROFILE_G_EXTENDED_MODE_1;
		} else {
			lpvParamData.value = (int)PalmSecureConstant.JAVA_PvAPI_PRE_PROFILE_G_EXTENDED_MODE_2;
		}
		
		try {
			stResult.result = palmsecureIf.JAVA_PvAPI_PreSetProfile(
					uiFlag,
					lpvParamData,
					null,
					null);
			if (stResult.result != PalmSecureConstant.JAVA_BioAPI_OK) {
				palmsecureIf.JAVA_PvAPI_GetErrorInfo(stResult.errInfo);
				if (BuildConfig.DEBUG) {
					Log.e(TAG, "PreSetProfile, PalmSecure method failed");
				}
				return stResult;
			}
		} catch(PalmSecureException e) {
			if (BuildConfig.DEBUG) {
				Log.e(TAG, "PreSetProfile", e);
			}
			stResult.result = PalmSecureConstant.JAVA_BioAPI_ERRCODE_FUNCTION_FAILED;
			stResult.pseErrNumber = e.ErrNumber;
			return stResult;
		}
		if (BuildConfig.DEBUG)
			Log.d(TAG, "BioAPI_PreSetProfile Done!");
		///////////////////////////////////////////////////////////////////////////

		//Attatch to module
		///////////////////////////////////////////////////////////////////////////
		try {
			moduleHandle = new JAVA_uint32();
			stResult.result = palmsecureIf.JAVA_BioAPI_ModuleAttach(
					ModuleGuid,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					moduleHandle);
			if (stResult.result != PalmSecureConstant.JAVA_BioAPI_OK) {
				palmsecureIf.JAVA_PvAPI_GetErrorInfo(stResult.errInfo);
				if (BuildConfig.DEBUG) {
					Log.e(TAG, "Attatch to module, PalmSecure method failed PalmSecureセンサーに接続できませんでした。");
				}
				return stResult;
			}
		} catch (PalmSecureException e) {
			if (BuildConfig.DEBUG) {
				Log.e(TAG, "Attatch to module", e);
			}
			stResult.result = PalmSecureConstant.JAVA_BioAPI_ERRCODE_FUNCTION_FAILED;
			stResult.pseErrNumber = e.ErrNumber;
			return stResult;
		}
		if (BuildConfig.DEBUG)
			Log.d(TAG, "BioAPI_ModuleAttach Done!");
		///////////////////////////////////////////////////////////////////////////

		//Set action listener
		///////////////////////////////////////////////////////////////////////////
		try {
			stResult.result = palmsecureIf.JAVA_BioAPI_SetGUICallbacks(
					moduleHandle,
					streamingCB,
					this,
					stateCB,
					this);
			if (stResult.result != PalmSecureConstant.JAVA_BioAPI_OK) {
				palmsecureIf.JAVA_PvAPI_GetErrorInfo(stResult.errInfo);
				if (BuildConfig.DEBUG) {
					Log.e(TAG, "Set action listener, PalmSecure method failed");
				}
				return stResult;
			}
		} catch (PalmSecureException e) {
			if (BuildConfig.DEBUG) {
				Log.e(TAG, "Set action listener", e);
			}
			stResult.result = PalmSecureConstant.JAVA_BioAPI_ERRCODE_FUNCTION_FAILED;
			stResult.pseErrNumber = e.ErrNumber;
			return stResult;
		}
		if (BuildConfig.DEBUG)
			Log.d(TAG, "BioAPI_SetGUICallbacks Done!");
		///////////////////////////////////////////////////////////////////////////

		//Get library information
		///////////////////////////////////////////////////////////////////////////
		try {
			stResult.result = palmsecureIf.JAVA_PvAPI_GetLibraryInfo(lbInfo);
			if (stResult.result != PalmSecureConstant.JAVA_BioAPI_OK) {
				palmsecureIf.JAVA_PvAPI_GetErrorInfo(stResult.errInfo);
				if (BuildConfig.DEBUG) {
					Log.e(TAG, "Get library information, PalmSecure method failed");
				}
				return stResult;
			}
		} catch (PalmSecureException e) {
			if (BuildConfig.DEBUG) {
				Log.e(TAG, "Get library information", e);
			}
			stResult.result = PalmSecureConstant.JAVA_BioAPI_ERRCODE_FUNCTION_FAILED;
			stResult.pseErrNumber = e.ErrNumber;
			return stResult;
		}
		if (BuildConfig.DEBUG)
			Log.d(TAG, "PvAPI_GetLibraryInfo Done!");
		///////////////////////////////////////////////////////////////////////////

		mUsingSensorType = (int) lbInfo.uiSensorKind;
		mUsingSensorTypeReal = mUsingSensorType;
		mUsingSensorExtKind = lbInfo.uiSensorExtKind;
		switch ((int )mUsingSensorType) {
			case (int) PalmSecureConstant.JAVA_PvAPI_INFO_SENSOR_TYPE_2:
				break;
			case (int) PalmSecureConstant.JAVA_PvAPI_INFO_SENSOR_TYPE_9:
				mUsingSensorType = PalmSecureConstant.JAVA_PvAPI_INFO_SENSOR_TYPE_2;
				break;
			case (int) PalmSecureConstant.JAVA_PvAPI_INFO_SENSOR_TYPE_B:
				break;
			case (int) PalmSecureConstant.JAVA_PvAPI_INFO_SENSOR_TYPE_D:
				break;
		}

		mUsingGuideMode = guideMode;

		//Set guide mode
		///////////////////////////////////////////////////////////////////////////
		try {
			JAVA_uint32 dwFlag = new JAVA_uint32();
			dwFlag.value = PalmSecureConstant.JAVA_PvAPI_PROFILE_GUIDE_MODE;
			JAVA_uint32 dwParam1 = new JAVA_uint32();
			if (mUsingGuideMode == 0) {
				dwParam1.value = (int) PalmSecureConstant.JAVA_PvAPI_PROFILE_GUIDE_MODE_NO_GUIDE;
			} else {
				dwParam1.value = (int) PalmSecureConstant.JAVA_PvAPI_PROFILE_GUIDE_MODE_GUIDE;
			}
			stResult.result = palmsecureIf.JAVA_PvAPI_SetProfile(
					moduleHandle,
					dwFlag,
					dwParam1,
					null,
					null);
			if (stResult.result != PalmSecureConstant.JAVA_BioAPI_OK) {
				palmsecureIf.JAVA_PvAPI_GetErrorInfo(stResult.errInfo);
				if (BuildConfig.DEBUG) {
					Log.e(TAG, "Set guide mode, PalmSecure method failed");
				}
				return stResult;
			}
		} catch (PalmSecureException e) {
			if (BuildConfig.DEBUG) {
				Log.e(TAG, "Set guide mode", e);
			}
			stResult.result = PalmSecureConstant.JAVA_BioAPI_ERRCODE_FUNCTION_FAILED;
			stResult.pseErrNumber = e.ErrNumber;
			return stResult;
		}
		if (BuildConfig.DEBUG)
			Log.d(TAG, "PvAPI_SetProfile(set guide mode) Done!");
		///////////////////////////////////////////////////////////////////////////

		return stResult;
	}

	/**
	 * Terminate the Authentication library.
	 */
	private void Ps_Sample_Apl_Java_TermLibrary() {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Ps_Sample_Apl_Java_TermLibrary");
		}

		//Detach module
		///////////////////////////////////////////////////////////////////////////
		if (moduleHandle != null) {
			try {
				palmsecureIf.JAVA_BioAPI_ModuleDetach(
						moduleHandle);
			} catch (PalmSecureException e) {
				if (BuildConfig.DEBUG) {
					Log.e(TAG, "Detach module", e);
				}
			}
			if (BuildConfig.DEBUG)
				Log.d(TAG, "BioAPI_ModuleDetach Done!");
		}
		///////////////////////////////////////////////////////////////////////////

		//Unload module
		///////////////////////////////////////////////////////////////////////////
		try {
			palmsecureIf.JAVA_BioAPI_ModuleUnload(
					ModuleGuid,
					null,
					null);
		} catch (PalmSecureException e) {
			if (BuildConfig.DEBUG) {
				Log.e(TAG, "Unload module", e);
			}
		}
		if (BuildConfig.DEBUG)
			Log.d(TAG, "BioAPI_ModuleUnload Done!");
		///////////////////////////////////////////////////////////////////////////

	}

	public void Ps_Sample_Apl_Java_InitIdList() {
		if (BuildConfig.DEBUG)
			Log.d(TAG, "Ps_Sample_Apl_Java_InitIdList");

		PsDataManager dataMng = new PsDataManager(mActivity,
				mUsingSensorType, mUsingDataType);
		idList = new ArrayList<String>();

		try {
			dataMng.convertDBToBioAPI_Data_All(idList);
		} catch (PsAplException | PalmSecureException e) {
			if (BuildConfig.DEBUG) {
				Log.e(TAG, "Init List", e);
			}
		}
	}
	
	public void Ps_Sample_Apl_Java_RefreshIdList() {
		if (BuildConfig.DEBUG)
			Log.d(TAG, "Ps_Sample_Apl_Java_RefreshIdList");

		PsDataManager dataMng = new PsDataManager(mActivity,
				mUsingSensorType, mUsingDataType);
		if (idList == null) {
			idList = new ArrayList<String>();
		} else {
			idList.clear();
		}

		try {
			dataMng.convertDBToBioAPI_Data_All(idList);
		} catch (PsAplException | PalmSecureException e) {
			if (BuildConfig.DEBUG) {
				Log.e(TAG, "Refresh List", e);
			}
		}
	}

	public void Ps_Sample_Apl_Java_DeleteId(String id) {
		if (BuildConfig.DEBUG)
			Log.d(TAG, "Ps_Sample_Apl_Java_DeleteId");

		PsDataManager dataMng = new PsDataManager(mActivity,
				mUsingSensorType, mUsingDataType);

		try {
			dataMng.deleteDBToBioAPI_Data(id);
			if (idList != null) idList.remove(id);
		} catch (PsAplException e) {
			if (BuildConfig.DEBUG) {
				Log.e(TAG, "Delete List", e);
			}
		}
	}

	public boolean Ps_Sample_Apl_Java_RegisteredId(String id) {
		return (idList != null && idList.contains(id));
	}

	public Context getBaseContext() {
		return mActivity.getBaseContext();
	}

	/**
	 * Get PalmSecureIf instance for capture operations
	 */
	public PalmSecureIf getPalmSecureIf() {
		return palmsecureIf;
	}

	/**
	 * Get module handle for capture operations
	 */
	public JAVA_uint32 getModuleHandle() {
		return moduleHandle;
	}
}
