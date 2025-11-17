/*
 * PsServiceHelper.java
 *
 *	All Rights Reserved, Copyright(c) FUJITSU FRONTECH LIMITED 2021
 */

package com.fujitsu.frontech.palmsecure_sample.service;

import android.os.Bundle;

import com.fujitsu.frontech.palmsecure.JAVA_BioAPI_DATA;
import com.fujitsu.frontech.palmsecure.JAVA_BioAPI_GUI_BITMAP;
import com.fujitsu.frontech.palmsecure_sample.data.PsThreadResult;

public class PsServiceHelper {

	private static final String PS_SERVICE_HELPER_APPLICATION_KEY = "PsServiceHelper_ApplicationKey";
	private static final String PS_SERVICE_HELPER_DATA_TYPE = "PsServiceHelper_DataType";
	private static final String PS_SERVICE_HELPER_GUIDE_MODE = "PsServiceHelper_GuideMode";
	private static final String PS_SERVICE_HELPER_NUMBER_OF_RETRY = "PsServiceHelper_NumberOfRetry";
	private static final String PS_SERVICE_HELPER_SLEEP_TIME = "PsServiceHelper_SleepTime";
	private static final String PS_SERVICE_HELPER_MAX_RESULTS = "PsServiceHelper_MaxResults";
	private static final String PS_SERVICE_HELPER_USER_ID = "PsServiceHelper_userID";
	private static final String PS_SERVICE_HELPER_GUIDANCE_ERROR = "PsServiceHelper_GuidanceError";
	private static final String PS_SERVICE_HELPER_SENSOR_TYPE = "PsServiceHelper_SensorType";
	private static final String PS_SERVICE_HELPER_SENSOR_EXT_KIND = "PsServiceHelper_SensorExtKind";
	private static final String PS_SERVICE_HELPER_PROCESS_KEY = "PsServiceHelper_ProcessKey";
	private static final String PS_SERVICE_HELPER_COUNT = "PsServiceHelper_Count";
	private static final String PS_SERVICE_HELPER_CAP_NUMBER = "PsServiceHelper_CapNumber";
	private static final String PS_SERVICE_HELPER_GUIDANCE_KEY = "PsServiceHelper_GuidanceKey";
	private static final String PS_SERVICE_HELPER_ENROLL_SCORE = "PsServiceHelper_EnrollScore";

	// PsThreadResult
	private static final String PS_THREAD_RESULT_RESULT = "PsThreadResult_result";
	private static final String PS_THREAD_RESULT_RETRY_CNT = "PsThreadResult_retryCnt";
	private static final String PS_THREAD_RESULT_AUTHENTICATED = "PsThreadResult_authenticated";
	private static final String PS_THREAD_RESULT_USER_ID = "PsThreadResult_userId";
	private static final String PS_THREAD_RESULT_FAR_ACHIEVED = "PsThreadResult_farAchieved";
	private static final String PS_THREAD_RESULT_PSE_ERROR_NUMBER = "PsThreadResult_PSE_ErrNumber";
	private static final String PS_THREAD_RESULT_MESSAGE_KEY = "PsThreadResult_messageKey";
	private static final String PS_THREAD_RESULT_info = "PsThreadResult_info";

	// JAVA_PvAPI_ErrorInfo
	private static final String JAVA_PVAPI_ERROR_INFO_ERROR_LEVEL = "JAVA_PvAPI_ErrorInfo_ErrorLevel";
	private static final String JAVA_PVAPI_ERROR_INFO_ERROR_CODE = "JAVA_PvAPI_ErrorInfo_ErrorCode";
	private static final String JAVA_PVAPI_ERROR_INFO_ERROR_DETAIL = "JAVA_PvAPI_ErrorInfo_ErrorDetail";
	private static final String JAVA_PVAPI_ERROR_INFO_ERROR_MODULE = "JAVA_PvAPI_ErrorInfo_ErrorModule";
	private static final String JAVA_PVAPI_ERROR_INFO_ERROR_OPTIONAL1 = "JAVA_PvAPI_ErrorInfo_ErrorOptional1";
	private static final String JAVA_PVAPI_ERROR_INFO_ERROR_OPTIONAL2 = "JAVA_PvAPI_ErrorInfo_ErrorOptional2";
	private static final String JAVA_PVAPI_ERROR_INFO_API_INFO = "JAVA_PvAPI_ErrorInfo_APIInfo";
	private static final String JAVA_PVAPI_ERROR_INFO_ERROR_INFO1 = "JAVA_PvAPI_ErrorInfo_ErrorInfo1";
	private static final String JAVA_PVAPI_ERROR_INFO_ERROR_INFO2 = "JAVA_PvAPI_ErrorInfo_ErrorInfo2";
	private static final String JAVA_PVAPI_ERROR_INFO_ERROR_INFO3 = "JAVA_PvAPI_ErrorInfo_ErrorInfo3";

	// JAVA_BioAPI_GUI_BITMAP
	private static final String JAVA_BIOAPI_GUI_BITMAP_HEIGHT = "JAVA_BioAPI_GUI_BITMAP_Height";
	private static final String JAVA_BIOAPI_GUI_BITMAP_WIDTH = "JAVA_BioAPI_GUI_BITMAP_Width";

	// JAVA_BioAPI_DATA
	private static final String JAVA_BIOAPI_DATA_DATA = "JAVA_BioAPI_DATA_Data";
	private static final String JAVA_BIOAPI_DATA_LENGTH = "JAVA_BioAPI_DATA_Length";

	// Application Key
	public static String getBundleToApplicationKey(Bundle bundle) {

		return bundle.getString(PS_SERVICE_HELPER_APPLICATION_KEY);
	}

	public static void putApplicationKeyToBundle(Bundle bundle, String aplKey) {

		bundle.putString(PS_SERVICE_HELPER_APPLICATION_KEY, aplKey);
	}

	// Data Type
	public static int getBundleToDataType(Bundle bundle) {

		return bundle.getInt(PS_SERVICE_HELPER_DATA_TYPE);
	}

	public static void pubDataTypeToBundle(Bundle bundle, int dataType) {

		bundle.putInt(PS_SERVICE_HELPER_DATA_TYPE, dataType);
	}

	// Guide Mode
	public static int getBundleToGuideMode(Bundle bundle) {

		return bundle.getInt(PS_SERVICE_HELPER_GUIDE_MODE);
	}

	public static void putGuideModeToBundle(Bundle bundle, int guideMode) {

		bundle.putInt(PS_SERVICE_HELPER_GUIDE_MODE, guideMode);
	}

	// Number Of Retry
	public static int getBundleToNumberOfRetry(Bundle bundle) {

		return bundle.getInt(PS_SERVICE_HELPER_NUMBER_OF_RETRY);
	}

	public static void putNumberOfRetryToBundle(Bundle bundle, int mumberOfRetry) {

		bundle.putInt(PS_SERVICE_HELPER_NUMBER_OF_RETRY, mumberOfRetry);
	}

	// Sleep Time
	public static int getBundleToSleepTime(Bundle bundle) {

		return bundle.getInt(PS_SERVICE_HELPER_SLEEP_TIME);
	}

	public static void putSleepTimeToBundle(Bundle bundle, int sleepTime) {

		bundle.putInt(PS_SERVICE_HELPER_SLEEP_TIME, sleepTime);
	}

	// Max Results
	public static long getBundleToMaxResults(Bundle bundle) {

		return bundle.getLong(PS_SERVICE_HELPER_MAX_RESULTS);
	}

	public static void putMaxResultsToBundle(Bundle bundle, long maxResults) {

		bundle.putLong(PS_SERVICE_HELPER_MAX_RESULTS, maxResults);
	}

	// User ID
	public static String getBundleToUserId(Bundle bundle) {

		return bundle.getString(PS_SERVICE_HELPER_USER_ID);
	}

	public static void putUserIdToBundle(Bundle bundle, String userID) {

		bundle.putString(PS_SERVICE_HELPER_USER_ID, userID);
	}

	// Guidance Error
	public static boolean getBundleToGuidanceError(Bundle bundle) {

		return bundle.getBoolean(PS_SERVICE_HELPER_GUIDANCE_ERROR);
	}

	public static void putGuidanceErrorToBundle(Bundle bundle, boolean error) {

		bundle.putBoolean(PS_SERVICE_HELPER_GUIDANCE_ERROR, error);
	}

	// Sensor Type
	public static long getBundleToSensorType(Bundle bundle) {

		return bundle.getLong(PS_SERVICE_HELPER_SENSOR_TYPE);
	}

	public static void putSensorTypeToBundle(Bundle bundle, long sensorType) {

		bundle.putLong(PS_SERVICE_HELPER_SENSOR_TYPE, sensorType);
	}

	// Sensor Ext Kind
	public static long getBundleToSensorExtKind(Bundle bundle) {

		return bundle.getLong(PS_SERVICE_HELPER_SENSOR_EXT_KIND);
	}

	public static void putSensorExtKindToBundle(Bundle bundle, long sensorExtKind) {

		bundle.putLong(PS_SERVICE_HELPER_SENSOR_EXT_KIND, sensorExtKind);
	}

	// Process Key
	public static int getBundleToProcessKey(Bundle bundle) {

		return bundle.getInt(PS_SERVICE_HELPER_PROCESS_KEY);
	}

	public static void putProcessKeyToBundle(Bundle bundle, int processKey) {

		bundle.putInt(PS_SERVICE_HELPER_PROCESS_KEY, processKey);
	}

	// Count (Callback)
	public static int getBundleToCount(Bundle bundle) {

		return bundle.getInt(PS_SERVICE_HELPER_COUNT);
	}

	public static void putCountToBundle(Bundle bundle, int count) {

		bundle.putInt(PS_SERVICE_HELPER_COUNT, count);
	}

	// Number Of Capture Times (Callback)
	public static int getBundleToCapNumber(Bundle bundle) {

		return bundle.getInt(PS_SERVICE_HELPER_CAP_NUMBER);
	}

	public static void putCapNumberToBundle(Bundle bundle, int number) {

		bundle.putInt(PS_SERVICE_HELPER_CAP_NUMBER, number);
	}

	// Guidance Key
	public static int getBundleToGuidanceKey(Bundle bundle) {

		return bundle.getInt(PS_SERVICE_HELPER_GUIDANCE_KEY);
	}

	public static void putGuidanceKeyToBundle(Bundle bundle, int guidanceKey) {

		bundle.putInt(PS_SERVICE_HELPER_GUIDANCE_KEY, guidanceKey);
	}

	// Enroll Score
	public static int getBundleToEnrollScore(Bundle bundle) {

		return bundle.getInt(PS_SERVICE_HELPER_ENROLL_SCORE);
	}

	public static void putEnrollScoreToBundle(Bundle bundle, int enrollScore) {

		bundle.putInt(PS_SERVICE_HELPER_ENROLL_SCORE, enrollScore);
	}

	// PsThreadResult
	public static PsThreadResult getBundleToPsThreadResult(Bundle bundle) {

		PsThreadResult result = new PsThreadResult();

		result.result = bundle.getLong(PS_THREAD_RESULT_RESULT);
		result.retryCnt = bundle.getInt(PS_THREAD_RESULT_RETRY_CNT);
		result.authenticated = bundle.getBoolean(PS_THREAD_RESULT_AUTHENTICATED);
		result.userId = bundle.getStringArrayList(PS_THREAD_RESULT_USER_ID);
		result.farAchieved = bundle.getIntegerArrayList(PS_THREAD_RESULT_FAR_ACHIEVED);
		result.pseErrNumber = bundle.getInt(PS_THREAD_RESULT_PSE_ERROR_NUMBER);
		result.messageKey = bundle.getInt(PS_THREAD_RESULT_MESSAGE_KEY);
		result.info	= bundle.getByteArray(PS_THREAD_RESULT_info);

		result.errInfo.ErrorLevel = bundle.getLong(JAVA_PVAPI_ERROR_INFO_ERROR_LEVEL);
		result.errInfo.ErrorCode = bundle.getLong(JAVA_PVAPI_ERROR_INFO_ERROR_CODE);
		result.errInfo.ErrorDetail = bundle.getLong(JAVA_PVAPI_ERROR_INFO_ERROR_DETAIL);
		result.errInfo.ErrorModule = bundle.getLong(JAVA_PVAPI_ERROR_INFO_ERROR_MODULE);
		result.errInfo.ErrorOptional1 = bundle.getLong(JAVA_PVAPI_ERROR_INFO_ERROR_OPTIONAL1);
		result.errInfo.ErrorOptional2 = bundle.getLong(JAVA_PVAPI_ERROR_INFO_ERROR_OPTIONAL2);
		result.errInfo.APIInfo = bundle.getLongArray(JAVA_PVAPI_ERROR_INFO_API_INFO);
		result.errInfo.ErrorInfo1 = bundle.getLong(JAVA_PVAPI_ERROR_INFO_ERROR_INFO1);
		result.errInfo.ErrorInfo2 = bundle.getLong(JAVA_PVAPI_ERROR_INFO_ERROR_INFO2);
		result.errInfo.ErrorInfo3 = bundle.getLongArray(JAVA_PVAPI_ERROR_INFO_ERROR_INFO3);

		return result;
	}

	public static void putPsThreadResultToBundle(Bundle bundle, PsThreadResult result) {

		bundle.putLong(PS_THREAD_RESULT_RESULT, result.result);
		bundle.putInt(PS_THREAD_RESULT_RETRY_CNT, result.retryCnt);
		bundle.putBoolean(PS_THREAD_RESULT_AUTHENTICATED, result.authenticated);
		bundle.putStringArrayList(PS_THREAD_RESULT_USER_ID, result.userId);
		bundle.putIntegerArrayList(PS_THREAD_RESULT_FAR_ACHIEVED, result.farAchieved);
		bundle.putInt(PS_THREAD_RESULT_PSE_ERROR_NUMBER, result.pseErrNumber);
		bundle.putInt(PS_THREAD_RESULT_MESSAGE_KEY, result.messageKey);
		bundle.putByteArray(PS_THREAD_RESULT_info, result.info);

		bundle.putLong(JAVA_PVAPI_ERROR_INFO_ERROR_LEVEL, result.errInfo.ErrorLevel);
		bundle.putLong(JAVA_PVAPI_ERROR_INFO_ERROR_CODE, result.errInfo.ErrorCode);
		bundle.putLong(JAVA_PVAPI_ERROR_INFO_ERROR_DETAIL, result.errInfo.ErrorDetail);
		bundle.putLong(JAVA_PVAPI_ERROR_INFO_ERROR_MODULE, result.errInfo.ErrorModule);
		bundle.putLong(JAVA_PVAPI_ERROR_INFO_ERROR_OPTIONAL1, result.errInfo.ErrorOptional1);
		bundle.putLong(JAVA_PVAPI_ERROR_INFO_ERROR_OPTIONAL2, result.errInfo.ErrorOptional2);
		bundle.putLongArray(JAVA_PVAPI_ERROR_INFO_API_INFO, result.errInfo.APIInfo);
		bundle.putLong(JAVA_PVAPI_ERROR_INFO_ERROR_INFO1, result.errInfo.ErrorInfo1);
		bundle.putLong(JAVA_PVAPI_ERROR_INFO_ERROR_INFO2, result.errInfo.ErrorInfo2);
		bundle.putLongArray(JAVA_PVAPI_ERROR_INFO_ERROR_INFO3, result.errInfo.ErrorInfo3);
	}

	// Silhouette Image (Callback)
	public static JAVA_BioAPI_GUI_BITMAP getBundleToSilhouette(Bundle bundle) {

		JAVA_BioAPI_GUI_BITMAP silhouette = new JAVA_BioAPI_GUI_BITMAP();
		JAVA_BioAPI_DATA bitmap = new JAVA_BioAPI_DATA();
		silhouette.Bitmap = bitmap;

		silhouette.Height = bundle.getLong(JAVA_BIOAPI_GUI_BITMAP_HEIGHT);
		silhouette.Width = bundle.getLong(JAVA_BIOAPI_GUI_BITMAP_WIDTH);
		silhouette.Bitmap.Data = bundle.getByteArray(JAVA_BIOAPI_DATA_DATA);
		silhouette.Bitmap.Length = bundle.getLong(JAVA_BIOAPI_DATA_LENGTH);

		return silhouette;
	}

	public static void putSilhouetteToBundle(Bundle bundle, JAVA_BioAPI_GUI_BITMAP silhouette) {

		bundle.putLong(JAVA_BIOAPI_GUI_BITMAP_HEIGHT, silhouette.Height);
		bundle.putLong(JAVA_BIOAPI_GUI_BITMAP_WIDTH, silhouette.Width);
		bundle.putByteArray(JAVA_BIOAPI_DATA_DATA, silhouette.Bitmap.Data);
		bundle.putLong(JAVA_BIOAPI_DATA_LENGTH, silhouette.Bitmap.Length);
	}
	
}
