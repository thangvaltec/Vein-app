/*
 * PsThreadResult.java
 *
 *	All Rights Reserved, Copyright(c) FUJITSU FRONTECH LIMITED 2021
 */

package com.fujitsu.frontech.palmsecure_sample.data;

import java.util.ArrayList;

import com.fujitsu.frontech.palmsecure.JAVA_PvAPI_ErrorInfo;

public class PsThreadResult {

	public long result;
	public int retryCnt;
	public boolean authenticated;
	public ArrayList<String> userId;
	public ArrayList<Integer> farAchieved;
	public byte[] info;
	public JAVA_PvAPI_ErrorInfo errInfo;
	public int pseErrNumber;
	public int messageKey;

	public PsThreadResult() {
		result = 0;
		retryCnt = 0;
		authenticated = false;
		userId = new ArrayList<String>();
		farAchieved = new ArrayList<Integer>();
		info = null;
		errInfo = new JAVA_PvAPI_ErrorInfo();
		pseErrNumber = 0;
		messageKey = 0;
	}
}
