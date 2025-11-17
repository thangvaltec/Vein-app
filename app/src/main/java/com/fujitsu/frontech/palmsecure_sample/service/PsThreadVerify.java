/*
 * PsThreadVerify.java
 *
 *	All Rights Reserved, Copyright(c) FUJITSU FRONTECH LIMITED 2021
 */

package com.fujitsu.frontech.palmsecure_sample.service;

import android.util.Log;

import com.fujitsu.frontech.palmsecure.JAVA_BioAPI_INPUT_BIR;
import com.fujitsu.frontech.palmsecure.JAVA_BioAPI_BIR;
import com.fujitsu.frontech.palmsecure.JAVA_PvAPI_TemplateInfoEx;
import com.fujitsu.frontech.palmsecure.JAVA_sint32;
import com.fujitsu.frontech.palmsecure.JAVA_uint32;
import com.fujitsu.frontech.palmsecure.PalmSecureIf;
import com.fujitsu.frontech.palmsecure.util.PalmSecureConstant;
import com.fujitsu.frontech.palmsecure.util.PalmSecureException;
import com.fujitsu.frontech.palmsecure_sample.data.PsDataManager;
import com.fujitsu.frontech.palmsecure_sample.data.PsThreadResult;
import com.fujitsu.frontech.palmsecure_sample.exception.PsAplException;
import com.fujitsu.frontech.palmsecure_gui_sample.BuildConfig;
import com.fujitsu.frontech.palmsecure_gui_sample.R;

public class PsThreadVerify extends PsThreadBase {

	public PsThreadVerify(PsService service, PalmSecureIf palmsecureIf, JAVA_uint32 moduleHandle, String userID,
			int numberOfRetry, int sleepTime) {
		super("PsThreadVerify", service, palmsecureIf, moduleHandle, userID, numberOfRetry, sleepTime, 0);
	}

	public void run() {

		PsThreadResult stResult = new PsThreadResult();

		try {
			PsDataManager dataMng = new PsDataManager(
					this.service.getBaseContext(),
					this.service.getUsingSensorType(),
					this.service.getUsingDataType());

			int waitTime = 0;
			stResult.userId.add(userID);

			//Get a instance of DNET_BioAPI_INPUT_BIR class
			///////////////////////////////////////////////////////////////////////////
			// Fetch all templates for this user and try verify against each
			JAVA_BioAPI_BIR[] templates = null;
			try {
				templates = dataMng.convertDBToBioAPI_Data_AllForId(userID);
			} catch (PalmSecureException e) {
				if (BuildConfig.DEBUG) {
					Log.e(TAG, "Get templates for user", e);
				}
				stResult.result = PalmSecureConstant.JAVA_BioAPI_ERRCODE_FUNCTION_FAILED;
				stResult.pseErrNumber = e.ErrNumber;
				Ps_Sample_Apl_Java_NotifyResult_Verify(stResult);
				return;
			} catch (PsAplException pae) {
				if (BuildConfig.DEBUG) {
					Log.e(TAG, "Get templates for user", pae);
				}
				stResult.result = PalmSecureConstant.JAVA_BioAPI_ERRCODE_FUNCTION_FAILED;
				stResult.messageKey = R.string.AplErrorSystemError;
				Ps_Sample_Apl_Java_NotifyResult_Verify(stResult);
				return;
			}
			///////////////////////////////////////////////////////////////////////////

            //Brief warm-up delay to allow sensor/stream to stabilize. This reduces race conditions
            //where the first verification attempt reads stale/low-quality capture data.
            try {
                Thread.sleep(200);
            } catch (InterruptedException ie) {
                // ignore and continue
            }

            //Repeat numOfRetry times until verification succeed
            int verifyCnt = 0;
            for (verifyCnt = 0; verifyCnt <= this.numberOfRetry; verifyCnt++) {				Ps_Sample_Apl_Java_NotifyWorkMessage(R.string.WorkVerify);

				if (verifyCnt > 0) {

					Ps_Sample_Apl_Java_NotifyGuidance(
							R.string.RetryTransaction,
							false);

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

				stResult.retryCnt = verifyCnt;

				//Verification
				///////////////////////////////////////////////////////////////////////////
				JAVA_sint32 maxFRRRequested = new JAVA_sint32();
				maxFRRRequested.value = PalmSecureConstant.JAVA_BioAPI_FALSE;
				JAVA_uint32 farPrecedence = new JAVA_uint32();
				farPrecedence.value = PalmSecureConstant.JAVA_BioAPI_FALSE;
				JAVA_uint32 result = new JAVA_uint32();
				JAVA_sint32 farAchieved = new JAVA_sint32();
				JAVA_sint32 timeout = new JAVA_sint32();
				boolean matched = false;
				if (templates != null) {
					for (int t = 0; t < templates.length; t++) {
						JAVA_BioAPI_INPUT_BIR storedTemplate = new JAVA_BioAPI_INPUT_BIR();
						storedTemplate.Form = PalmSecureConstant.JAVA_BioAPI_FULLBIR_INPUT;
						storedTemplate.BIR = templates[t];
						try {
							stResult.result = palmsecureIf.JAVA_BioAPI_Verify(
									moduleHandle,
									null,
									maxFRRRequested,
									null,
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
								Log.e(TAG, "Verification", e);
							}
							stResult.result = PalmSecureConstant.JAVA_BioAPI_ERRCODE_FUNCTION_FAILED;
							stResult.pseErrNumber = e.ErrNumber;
							break;
						}
						if (stResult.result == PalmSecureConstant.JAVA_BioAPI_OK && result.value == PalmSecureConstant.JAVA_BioAPI_TRUE) {
							matched = true;
							// set the userId in result for reporting
							stResult.userId.add(userID);
							break;
						}
					}
					if (!matched) {
						                    if (!matched) {
                        // keep stResult.result from last verification attempt
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "Verification attempt did not match for user: " + userID + " (attempt " + verifyCnt + ")");
                        }
                    }
					}
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
							Log.e(TAG, "Verification, get error info", e);
						}
						stResult.pseErrNumber = e.ErrNumber;
					}
					break;
				}

				stResult.info = this.service.silhouette;

				//If result of verification is false, retry verification
				if (result.value != PalmSecureConstant.JAVA_BioAPI_TRUE) {
					continue;
				}

				stResult.authenticated = true;
				break;

			}

			Ps_Sample_Apl_Java_NotifyResult_Verify(stResult);

		} catch (Exception e) {
			if (BuildConfig.DEBUG) {
				Log.e(TAG, "run", e);
			}
		}

		return;

	}

}
