/*
 * PsThreadCapture.java
 *
 * All Rights Reserved, Copyright(c) FUJITSU FRONTECH LIMITED 2021
 */

package com.fujitsu.frontech.palmsecure_sample.service;

import com.fujitsu.frontech.palmsecure_sample.data.PsThreadResult;

/**
 * Lightweight stub for capture thread.
 * The project uses the streaming silhouette callback to receive images.
 * Keep a simple stub so any residual code referencing this class compiles.
 */
public class PsThreadCapture extends PsThreadBase {

    public PsThreadCapture() {
        super((com.fujitsu.frontech.palmsecure.PalmSecureIf) null, (com.fujitsu.frontech.palmsecure.JAVA_uint32) null);
    }

    // Backwards-compatible constructor
    public PsThreadCapture(com.fujitsu.frontech.palmsecure.PalmSecureIf palmSecureIf,
                           com.fujitsu.frontech.palmsecure.JAVA_uint32 moduleHandle) {
        super(palmSecureIf, moduleHandle);
    }

    @Override
    public void run() {
        PsThreadResult stResult = new PsThreadResult();
        // Indicate success but no captured image (streaming silhouette should be used)
        stResult.result = 0L; // JAVA_BioAPI_OK
        setResult(stResult);
    }

    public android.graphics.Bitmap getCapturedImage() {
        return null;
    }
}