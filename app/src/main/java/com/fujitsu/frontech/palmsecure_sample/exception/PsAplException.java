/*
 *	PsException.java
 *
 *	All Rights Reserved, Copyright(c) FUJITSU FRONTECH LIMITED 2021
 */

package com.fujitsu.frontech.palmsecure_sample.exception;

import com.fujitsu.frontech.palmsecure.JAVA_PvAPI_ErrorInfo;

public class PsAplException extends Exception {

	private static final long serialVersionUID = 1L;
	private int errorMsgKey = 0;
	private JAVA_PvAPI_ErrorInfo errorInfo = null;

	public PsAplException() {
		setErrorMsgKey(0);
	}

	public PsAplException(int errorMsgKey) {
		setErrorMsgKey(errorMsgKey);
	}

	public PsAplException(JAVA_PvAPI_ErrorInfo errorInfo) {
		setErrorInfo(errorInfo);
	}

	public void setErrorMsgKey(int errorMsgKey) {
		this.errorMsgKey = errorMsgKey;
	}

	public int getErrorMsgKey() {
		return errorMsgKey;
	}

	public JAVA_PvAPI_ErrorInfo getErrorInfo() {
		return errorInfo;
	}

	public void setErrorInfo(JAVA_PvAPI_ErrorInfo errorInfo) {
		this.errorInfo = errorInfo;
	}
}
