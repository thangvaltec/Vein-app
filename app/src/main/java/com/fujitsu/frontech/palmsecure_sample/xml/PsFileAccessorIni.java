/*
 * PsFileAccessorIni.java
 *
 *	All Rights Reserved, Copyright(c) FUJITSU FRONTECH LIMITED 2021
 */

package com.fujitsu.frontech.palmsecure_sample.xml;

import java.util.HashMap;

import android.content.Context;
import android.util.Log;

public class PsFileAccessorIni extends PsFileAccessor {

	public static final String FileName = "PalmSecureSample.ini";

	public static final String ApplicationKey = "ApplicationKey";
	public static final String GuideMode = "GuideMode";
	public static final String GExtendedMode = "GExtendedMode";
	public static final String MaxResults = "MaxResults";
	public static final String NumberOfRetry = "NumberOfRetry";
	public static final String LogMode = "LogMode";
	public static final String LogFolderPath = "LogFolderPath";
	public static final String SilhouetteMode = "SilhouetteMode";
	public static final String SleepTime = "SleepTime";
    public static final String MessageLevel = "MessageLevel";
    public static final String AnimationTime = "AnimationTime";

	public static final String DEFAULT_ApplicationKey = "";
	public static final int DEFAULT_GuideMode = 0;
	public static final int DEFAULT_GExtendedMode = 2;
	public static final int DEFAULT_MaxResults = 2;
	public static final int DEFAULT_NumberOfRetry = 2;
	public static final int DEFAULT_LogMode = 1;
	public static final String DEFAULT_LogFolderPath = "Log";
	public static final int DEFAULT_SilhouetteMode = 1;
	public static final int DEFAULT_SleepTime = 2000;
    public static final int DEFAULT_MessageLevel = 0;
    public static final int DEFAULT_AnimationTime = 2000;
	private static final int MAX_Int = 2147483647;

	private static PsFileAccessorIni psFileAcsIni = null;
	private HashMap<String, String> xmlData = null;
	private HashMap<String, Integer> intData = null;

	private boolean Initialize(Context cx) {

		xmlData = new HashMap<String, String>();
		intData = new HashMap<String, Integer>();

		try {
			ReadXML(xmlData, cx.getFileStreamPath(FileName));
		} catch (Exception e) {
			Log.e("PsFileAccessorIni", "ReadXML", e);
			xmlData = null;
			intData = null;
			return false;
		}

		return true;
	}

	public static PsFileAccessorIni GetInstance(Context cx) {

		if (psFileAcsIni == null) {
			psFileAcsIni = new PsFileAccessorIni();
			if (!psFileAcsIni.Initialize(cx)) {
				psFileAcsIni = null;
				return psFileAcsIni;
			}
			if (!psFileAcsIni.CheckAndSetValues()) {
				psFileAcsIni = null;
			}
		}

		return psFileAcsIni;
	}

	public String GetValueString(String key) {

		String value = null;

		try {
			value = xmlData.get(key);
		} catch (Exception e) {
			value = null;
		}

		return value;
	}

	public Integer GetValueInteger(String key) {

		Integer value = null;

		try {
			value = intData.get(key);
		} catch (Exception e) {
			value = null;
		}

		return value;

	}

	private boolean CheckAndSetValues() {

		if (xmlData == null || intData == null) {
			return false;
		}

		try {
			setStrVal(ApplicationKey,	DEFAULT_ApplicationKey);
			setStrVal(LogFolderPath,	DEFAULT_LogFolderPath);
			setIntVal(GuideMode,		DEFAULT_GuideMode,		0,	1);
			setIntVal(GExtendedMode,	DEFAULT_GExtendedMode,	0,	2);
			setIntVal(MaxResults,		DEFAULT_MaxResults,		1,	5);
			setIntVal(NumberOfRetry,	DEFAULT_NumberOfRetry,	0,	MAX_Int);
			setIntVal(LogMode,			DEFAULT_LogMode,		0,	1);
			setIntVal(SilhouetteMode,	DEFAULT_SilhouetteMode,	0,	1);
			setIntVal(SleepTime,		DEFAULT_SleepTime,		0,	MAX_Int);
            setIntVal(MessageLevel,		DEFAULT_MessageLevel,	0,	2);
            setIntVal(AnimationTime,	DEFAULT_AnimationTime,	0,	MAX_Int);

		} catch (Exception e) {
			return false;
		}

		return true;
	}
	
	private void setStrVal(String key, String def) {

		String val = xmlData.get(key);
		if (val == null || (val.compareTo("") == 0)) {
			xmlData.put(key, def);
		}
	}

	private void setIntVal(String key, int def, int min, int max) {

		try {
			int val = Integer.parseInt(xmlData.get(key));
			if (val >= min && val <= max) {
				intData.put(key, val);
				return;
			}

		} catch (Exception e) {}

		intData.put(key, def);
	}

}
