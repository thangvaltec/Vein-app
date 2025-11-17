/*
 * PsThreadEnroll.java
 *
 *	All Rights Reserved, Copyright(c) FUJITSU FRONTECH LIMITED 2021
 */

 package com.fujitsu.frontech.palmsecure_sample.service;

 import java.util.ArrayList;
 
 import android.util.Log;
 
 import com.fujitsu.frontech.palmsecure.JAVA_BioAPI_BIR;
 import com.fujitsu.frontech.palmsecure.JAVA_BioAPI_INPUT_BIR;
 import com.fujitsu.frontech.palmsecure.JAVA_sint32;
 import com.fujitsu.frontech.palmsecure.JAVA_uint32;
 import com.fujitsu.frontech.palmsecure.JAVA_uint8;
 import com.fujitsu.frontech.palmsecure.PalmSecureIf;
 import com.fujitsu.frontech.palmsecure.util.PalmSecureConstant;
 import com.fujitsu.frontech.palmsecure.util.PalmSecureException;
 import com.fujitsu.frontech.palmsecure_sample.data.PsDataManager;
 import com.fujitsu.frontech.palmsecure_sample.data.PsThreadResult;
 import com.fujitsu.frontech.palmsecure_sample.exception.PsAplException;
 import com.fujitsu.frontech.palmsecure_gui_sample.BuildConfig;
 import com.fujitsu.frontech.palmsecure_gui_sample.R;
 
 public class PsThreadEnroll extends PsThreadBase {
 
	 public PsThreadEnroll(PsService service, PalmSecureIf palmsecureIf, JAVA_uint32 moduleHandle, String userID,
			 int numberOfRetry, int sleepTime) {
		 super("PsThreadEnroll", service, palmsecureIf, moduleHandle, userID, numberOfRetry, sleepTime, 0);
	 }
 
	 public void run() {
 
		 PsThreadResult stResult = new PsThreadResult();
 
		 try {
 
			 int enrollCnt = 0;
			 int enrollScore = 0;
			 int waitTime = 0;
 
			 ArrayList<Integer> scoreList = new ArrayList<Integer>();
			 for (enrollCnt = 0; enrollCnt <= this.numberOfRetry; enrollCnt++) {
 
				 if (enrollCnt > 0) {
					 Ps_Sample_Apl_Java_NotifyGuidance(R.string.RetryTransaction, false);
 
					 waitTime = 0;
					 do {
						 //End transaction in case of cancel
						 if (this.service.cancelFlg == true) {
							 break;
						 }
 
						 if (waitTime < this.sleepTime) {
							 Thread.sleep(100);
							 waitTime = waitTime + 100;
						 } else {
							 break;
						 }
 
					 } while (true);
				 }
 
				 //End transaction in case of cancel
				 if (this.service.cancelFlg == true) {
					 break;
				 }
 
				 this.service.enrollFlg = true;
				 stResult.retryCnt = enrollCnt;
 
				 //Enrollment
				 ///////////////////////////////////////////////////////////////////////////
				 JAVA_uint8 purpose = new JAVA_uint8();
				 purpose.value = PalmSecureConstant.JAVA_BioAPI_PURPOSE_VERIFY;
				 JAVA_sint32 birHandle = new JAVA_sint32();
				 JAVA_sint32 timeout = new JAVA_sint32();
				 try {
					 stResult.result = palmsecureIf.JAVA_BioAPI_Enroll(
							 moduleHandle,
							 purpose,
							 null,
							 birHandle,
							 null,
							 timeout,
							 null);
				 } catch (PalmSecureException e) {
					 if (BuildConfig.DEBUG) {
						 Log.e(TAG, "Enrollment", e);
					 }
					 stResult.result = PalmSecureConstant.JAVA_BioAPI_ERRCODE_FUNCTION_FAILED;
					 stResult.pseErrNumber = e.ErrNumber;
					 this.service.enrollFlg = false;
					 break;
				 }
				 ///////////////////////////////////////////////////////////////////////////
 
				 this.service.enrollFlg = false;
 
				 //End transaction in case of cancel
				 if (this.service.cancelFlg == true) {
					 break;
				 }
 
				 //If PalmSecure method failed, get error info
				 if (stResult.result != PalmSecureConstant.JAVA_BioAPI_OK) {
					 try {
						 palmsecureIf.JAVA_PvAPI_GetErrorInfo(stResult.errInfo);
					 } catch (PalmSecureException e) {
						 if (BuildConfig.DEBUG) {
							 Log.e(TAG, "Enrollment, get error info", e);
						 }
						 stResult.pseErrNumber = e.ErrNumber;
					 }
					 break;
				 }
 
				 enrollScore = this.service.notifiedScore;
				 stResult.info = this.service.silhouette;
 
				 //Get BIR data ( vein data )
				 ///////////////////////////////////////////////////////////////////////////
				 JAVA_BioAPI_BIR BIR = new JAVA_BioAPI_BIR();
				 try {
					 stResult.result = palmsecureIf.JAVA_BioAPI_GetBIRFromHandle(
							 moduleHandle,
							 birHandle,
							 BIR);
				 } catch (PalmSecureException e) {
					 if (BuildConfig.DEBUG) {
						 Log.e(TAG, "Get BIR data ( vein data )", e);
					 }
					 stResult.result = PalmSecureConstant.JAVA_BioAPI_ERRCODE_FUNCTION_FAILED;
					 stResult.pseErrNumber = e.ErrNumber;
					 break;
				 }
				 ///////////////////////////////////////////////////////////////////////////
 
				 //If PalmSecure method failed, get error info
				 if (stResult.result != PalmSecureConstant.JAVA_BioAPI_OK) {
					 try {
						 palmsecureIf.JAVA_PvAPI_GetErrorInfo(stResult.errInfo);
					 } catch (PalmSecureException e) {
						 if (BuildConfig.DEBUG) {
							 Log.e(TAG, "Get BIR data ( vein data ), get error info", e);
						 }
						 stResult.pseErrNumber = e.ErrNumber;
					 }
					 break;
				 }
 
				 //Free BIR handle
				 ///////////////////////////////////////////////////////////////////////////
				 try {
					 stResult.result = palmsecureIf.JAVA_BioAPI_FreeBIRHandle(
							 moduleHandle,
							 birHandle);
				 } catch (PalmSecureException e) {
					 if (BuildConfig.DEBUG) {
						 Log.e(TAG, "Free BIR handle", e);
					 }
					 stResult.result = PalmSecureConstant.JAVA_BioAPI_ERRCODE_FUNCTION_FAILED;
					 stResult.pseErrNumber = e.ErrNumber;
					 break;
				 }
				 ///////////////////////////////////////////////////////////////////////////
 
				 //End transaction in case of cancel
				 if (this.service.cancelFlg == true) {
					 break;
				 }
 
				 //If PalmSecure method failed, get error info
				 if (stResult.result != PalmSecureConstant.JAVA_BioAPI_OK) {
					 try {
						 palmsecureIf.JAVA_PvAPI_GetErrorInfo(stResult.errInfo);
					 } catch (PalmSecureException e) {
						 if (BuildConfig.DEBUG) {
							 Log.e(TAG, "Free BIR handle, get error info", e);
						 }
						 stResult.pseErrNumber = e.ErrNumber;
					 }
					 break;
				 }
 
				 Ps_Sample_Apl_Java_NotifyOffset(false);
 
				 //Repeat 2 times until enrollment test failed
				 boolean retryFlg = false;
				 scoreList.clear();
				 for (int cnt = 0; cnt < 1; cnt++) {
					 if (cnt == 0) {
						 Ps_Sample_Apl_Java_NotifyGuidance(
								 R.string.EnrollmentTest,
								 false);
					 } else {
						 Ps_Sample_Apl_Java_NotifyGuidance(
								 R.string.RetryTransaction,
								 false);
					 }
 
					 Ps_Sample_Apl_Java_NotifyWorkMessage(
							 R.string.WorkEnrollTest,
							 cnt + 1);
					 waitTime = 0;
					 do {
						 //End transaction in case of cancel
						 if (this.service.cancelFlg == true) {
							 break;
						 }
						 if (waitTime < this.sleepTime) {
							 Thread.sleep(100);
							 waitTime = waitTime + 100;
						 } else {
							 break;
						 }
					 } while (true);
 
					 //End transaction in case of cancel
					 if (this.service.cancelFlg == true) {
						 break;
					 }
 
					 //Set mode to get authentication score
					 ///////////////////////////////////////////////////////////////////////////
					 JAVA_uint32 dwFlag = new JAVA_uint32();
					 dwFlag.value = PalmSecureConstant.JAVA_PvAPI_PROFILE_SCORE_NOTIFICATIONS;
					 JAVA_uint32 dwParam1 = new JAVA_uint32();
					 dwParam1.value = PalmSecureConstant.JAVA_PvAPI_PROFILE_SCORE_NOTIFICATIONS_ON;
					 try {
						 stResult.result = palmsecureIf.JAVA_PvAPI_SetProfile(
								 moduleHandle,
								 dwFlag,
								 dwParam1,
								 null,
								 null);
					 } catch (PalmSecureException e) {
						 if (BuildConfig.DEBUG) {
							 Log.e(TAG, "Set mode to get authentication score", e);
						 }
						 stResult.result = PalmSecureConstant.JAVA_BioAPI_ERRCODE_FUNCTION_FAILED;
						 stResult.pseErrNumber = e.ErrNumber;
						 break;
					 }
					 ///////////////////////////////////////////////////////////////////////////
 
					 //If PalmSecure method failed, get error info.
					 if (stResult.result != PalmSecureConstant.JAVA_BioAPI_OK) {
						 try {
							 palmsecureIf.JAVA_PvAPI_GetErrorInfo(stResult.errInfo);
						 } catch (PalmSecureException e) {
							 if (BuildConfig.DEBUG) {
								 Log.e(TAG, "Set mode to get authentication score, get error info.", e);
							 }
							 stResult.pseErrNumber = e.ErrNumber;
						 }
						 break;
					 }
 
					 //Set mode to capture inv sense
					 ///////////////////////////////////////////////////////////////////////////
					 dwFlag.value = PalmSecureConstant.JAVA_PvAPI_PROFILE_CAPTURE_INV_SENSE;
					 dwParam1.value = PalmSecureConstant.JAVA_PvAPI_PROFILE_CAPTURE_INV_SENSE_ON;
					 try {
						 stResult.result = palmsecureIf.JAVA_PvAPI_SetProfile(
								 moduleHandle,
								 dwFlag,
								 dwParam1,
								 null,
								 null);
					 } catch(PalmSecureException e) {
						 if (BuildConfig.DEBUG) {
							 Log.e(TAG, "Set mode to capture inv sense", e);
						 }
						 stResult.result = PalmSecureConstant.JAVA_BioAPI_ERRCODE_FUNCTION_FAILED;
						 stResult.pseErrNumber = e.ErrNumber;
						 break;
					 }
					 ///////////////////////////////////////////////////////////////////////////
 
					 //If PalmSecure method failed, get error info.
					 if (stResult.result != PalmSecureConstant.JAVA_BioAPI_OK) {
						 try {
							 palmsecureIf.JAVA_PvAPI_GetErrorInfo(stResult.errInfo);
						 } catch (PalmSecureException e) {
							 if (BuildConfig.DEBUG) {
								 Log.e(TAG, "Set mode to capture inv sense, get error info.", e);
							 }
							 stResult.pseErrNumber = e.ErrNumber;
						 }
					 }
 
					 //Verification to check template quality
					 ///////////////////////////////////////////////////////////////////////////
					 JAVA_sint32 maxFRRRequested = new JAVA_sint32();
					 maxFRRRequested.value = PalmSecureConstant.JAVA_PvAPI_MATCHING_LEVEL_NORMAL;
					 JAVA_uint32 farPrecedence = new JAVA_uint32();
					 farPrecedence.value = PalmSecureConstant.JAVA_BioAPI_FALSE;
					 JAVA_BioAPI_INPUT_BIR storedTemplate = new JAVA_BioAPI_INPUT_BIR();
					 storedTemplate.Form = PalmSecureConstant.JAVA_BioAPI_FULLBIR_INPUT;
					 storedTemplate.BIR = BIR;
					 JAVA_uint32 result = new JAVA_uint32();
					 JAVA_sint32 farAchieved = new JAVA_sint32();
					 timeout.value = 0;
					 try {
						 stResult.result = palmsecureIf.JAVA_BioAPI_Verify(
								 moduleHandle,
								 null,
								 maxFRRRequested,
								 farPrecedence,
								 storedTemplate,
								 null,
								 result,
								 farAchieved,
								 null,
								 null,
								 timeout,
								 null);
					 } catch (PalmSecureException e) {
						 if (BuildConfig.DEBUG) {
							 Log.e(TAG, "Verification to check template quality", e);
						 }
						 stResult.result = PalmSecureConstant.JAVA_BioAPI_ERRCODE_FUNCTION_FAILED;
						 stResult.pseErrNumber = e.ErrNumber;
						 break;
					 }
					 ///////////////////////////////////////////////////////////////////////////
 
					 //If PalmSecure method failed, get error info
					 if ((stResult.result != PalmSecureConstant.JAVA_BioAPI_OK)
							 && this.service.cancelFlg != true) {
						 try {
							 palmsecureIf.JAVA_PvAPI_GetErrorInfo(stResult.errInfo);
						 } catch (PalmSecureException e) {
							 if (BuildConfig.DEBUG) {
								 Log.e(TAG, "Verification to check template quality, get error info", e);
							 }
							 stResult.pseErrNumber = e.ErrNumber;
						 }
						 break;
					 }
 
					 //Set mode not to get authentication score
					 ///////////////////////////////////////////////////////////////////////////
					 dwFlag.value = PalmSecureConstant.JAVA_PvAPI_PROFILE_SCORE_NOTIFICATIONS;
					 dwParam1.value = PalmSecureConstant.JAVA_PvAPI_PROFILE_SCORE_NOTIFICATIONS_OFF;
					 try {
						 stResult.result = palmsecureIf.JAVA_PvAPI_SetProfile(
								 moduleHandle,
								 dwFlag,
								 dwParam1,
								 null,
								 null);
					 } catch (PalmSecureException e) {
						 if (BuildConfig.DEBUG) {
							 Log.e(TAG, "Set mode not to get authentication score", e);
						 }
						 stResult.result = PalmSecureConstant.JAVA_BioAPI_ERRCODE_FUNCTION_FAILED;
						 stResult.pseErrNumber = e.ErrNumber;
						 break;
					 }
					 ///////////////////////////////////////////////////////////////////////////
 
					 //If PalmSecure method failed, get error info
					 if (stResult.result != PalmSecureConstant.JAVA_BioAPI_OK) {
						 try {
							 palmsecureIf.JAVA_PvAPI_GetErrorInfo(stResult.errInfo);
						 } catch (PalmSecureException e) {
							 if (BuildConfig.DEBUG) {
								 Log.e(TAG, "Set mode not to get authentication score, get error info", e);
							 }
							 stResult.pseErrNumber = e.ErrNumber;
						 }
						 break;
					 }
 
					 //Set mode not to capture inv sense
					 ///////////////////////////////////////////////////////////////////////////
					 dwFlag.value = PalmSecureConstant.JAVA_PvAPI_PROFILE_CAPTURE_INV_SENSE;
					 dwParam1.value = PalmSecureConstant.JAVA_PvAPI_PROFILE_CAPTURE_INV_SENSE_OFF;
					 try {
						 stResult.result = palmsecureIf.JAVA_PvAPI_SetProfile(
								 moduleHandle,
								 dwFlag,
								 dwParam1,
								 null,
								 null);
					 } catch(PalmSecureException e) {
						 if (BuildConfig.DEBUG) {
							 Log.e(TAG, "Set mode not to capture inv sense", e);
						 }
						 stResult.result = PalmSecureConstant.JAVA_BioAPI_ERRCODE_FUNCTION_FAILED;
						 stResult.pseErrNumber = e.ErrNumber;
						 break;
					 }
					 ///////////////////////////////////////////////////////////////////////////
 
					 //If PalmSecure method failed, get error info.
					 if (stResult.result != PalmSecureConstant.JAVA_BioAPI_OK) {
						 try {
							 palmsecureIf.JAVA_PvAPI_GetErrorInfo(stResult.errInfo);
						 } catch (PalmSecureException e) {
							 if (BuildConfig.DEBUG) {
								 Log.e(TAG, "Set mode not to capture inv sense, get error info.", e);
							 }
							 stResult.pseErrNumber = e.ErrNumber;
						 }
					 }
 
					 //End transaction in case of cancel
					 if (this.service.cancelFlg == true) {
						 break;
					 }
 
					 //If result of verification is false, retry enrollment test
					 if (result.value != PalmSecureConstant.JAVA_BioAPI_TRUE) {
						 stResult.authenticated = false;
						 retryFlg = true;
						 break;
					 }
 
					 stResult.authenticated = true;
					 scoreList.add(farAchieved.value);
 
				 }
 
				 if (this.service.cancelFlg == true) {
					 break;
				 }
				 if (stResult.result != PalmSecureConstant.JAVA_BioAPI_OK) {
					 break;
				 }
				 if (retryFlg == true) {
					 continue;
				 }
 
				 //Get an average score
				 int veriScore = 0;
				 for (int i = 0; i < scoreList.size(); i++) {
					 veriScore = veriScore + scoreList.get(i);
				 }
				 if (scoreList.size() > 0) {
					 veriScore = veriScore / scoreList.size();
				 }
				 stResult.farAchieved.add(veriScore);
 
				 //Create a byte array of vein data and output vein data to DB
				 ///////////////////////////////////////////////////////////////////////////
				 try {
					 PsDataManager dataMng = new PsDataManager(
							 this.service.getBaseContext(),
							 this.service.getUsingSensorType(),
							 this.service.getUsingDataType());
					 ArrayList<String> idList = new ArrayList<String>();
					 // Do not delete existing templates: allow multiple templates per user id
					 dataMng.convertDBToBioAPI_Data_All(idList);
					 if (!dataMng.convertBioAPI_DataToDB(BIR, userID)) {
						 stResult.result = PalmSecureConstant.JAVA_BioAPI_ERRCODE_FUNCTION_FAILED;
						 stResult.messageKey = R.string.AplErrorFileWrite;
					 }
				 } catch (PalmSecureException e) {
					 if (BuildConfig.DEBUG) {
						 Log.e(TAG, "Create a byte array of vein data and output vein data to DB", e);
					 }
					 stResult.result = PalmSecureConstant.JAVA_BioAPI_ERRCODE_FUNCTION_FAILED;
					 stResult.pseErrNumber = e.ErrNumber;
				 } catch (PsAplException pae) {
					 if (BuildConfig.DEBUG) {
						 Log.e(TAG, "Create a byte array of vein data and output vein data to DB", pae);
					 }
					 stResult.result = PalmSecureConstant.JAVA_BioAPI_ERRCODE_FUNCTION_FAILED;
					 stResult.messageKey = R.string.AplErrorSystemError;
				 }
				 ///////////////////////////////////////////////////////////////////////////
 
				 break;
 
			 }
 
			 // Thêm userId vào result để có thể hiển thị trong thông báo thành công
			 // Add userId to result so it can be displayed in the success message
			 if (stResult.userId == null) {
				 stResult.userId = new ArrayList<String>();
			 }
			 stResult.userId.add(userID);
 
			 Ps_Sample_Apl_Java_NotifyResult_Enroll (stResult, enrollScore);
 
		 } catch (Exception e) {
			 if (BuildConfig.DEBUG) {
				 Log.e(TAG, "run", e);
			 }
		 }
 
		 return;
	 }
 }
 