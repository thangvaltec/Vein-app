/*
 * PsThreadIdentify.java
 *
 *    All Rights Reserved, Copyright(c) FUJITSU FRONTECH LIMITED 2021
 */

package com.fujitsu.frontech.palmsecure_sample.service;

import android.util.Log;

import com.fujitsu.frontech.palmsecure.JAVA_BioAPI_BIR_ARRAY_POPULATION;
import com.fujitsu.frontech.palmsecure.JAVA_BioAPI_CANDIDATE;
import com.fujitsu.frontech.palmsecure.JAVA_BioAPI_IDENTIFY_POPULATION;
import com.fujitsu.frontech.palmsecure.JAVA_sint32;
import com.fujitsu.frontech.palmsecure.JAVA_uint32;
import com.fujitsu.frontech.palmsecure.PalmSecureIf;
import com.fujitsu.frontech.palmsecure.util.PalmSecureConstant;
import com.fujitsu.frontech.palmsecure.util.PalmSecureException;
import com.fujitsu.frontech.palmsecure_sample.data.PsDataManager;
import com.fujitsu.frontech.palmsecure_sample.data.PsThreadResult;
import com.fujitsu.frontech.palmsecure_sample.exception.PsAplException;
import com.fujitsu.frontech.palmsecure_gui_sample.BuildConfig;

/**
 * Thread that performs identification (scan-first) against the stored population.
 */
public class PsThreadIdentify extends PsThreadBase {

    public PsThreadIdentify(PsService service,
                            PalmSecureIf palmsecureIf,
                            JAVA_uint32 moduleHandle,
                            int numberOfRetry,
                            int sleepTime) {
        super("PsThreadIdentify", service, palmsecureIf, moduleHandle, null, numberOfRetry, sleepTime, 0);
    }

    @Override
    public void run() {
        PsThreadResult stResult = new PsThreadResult();
        stResult.result = PalmSecureConstant.JAVA_BioAPI_ERRCODE_FUNCTION_FAILED;

        try {
            // Create data manager with proper sensor type and data type from service
            PsDataManager dm = new PsDataManager(service.getActivity(),
                    service.getUsingSensorType(),
                    service.getUsingDataType());
                    
            java.util.ArrayList<String> names = new java.util.ArrayList<String>(service.getIdList());
            if (names.size() == 0) {
                stResult.result = PalmSecureConstant.JAVA_BioAPI_ERRCODE_FUNCTION_FAILED;
                Ps_Sample_Apl_Java_NotifyResult_Identify(stResult);
                return;
            }

            JAVA_BioAPI_IDENTIFY_POPULATION population = dm.convertDBToBioAPI_Data_All(names);

            // Set up identify parameters
            JAVA_sint32 maxFRRRequested = new JAVA_sint32();
            maxFRRRequested.value = PalmSecureConstant.JAVA_PvAPI_MATCHING_LEVEL_NORMAL;
            
            JAVA_sint32 farRequested = new JAVA_sint32();
            farRequested.value = PalmSecureConstant.JAVA_BioAPI_FALSE;
            
            JAVA_uint32 farPrecedence = new JAVA_uint32();
            farPrecedence.value = PalmSecureConstant.JAVA_BioAPI_FALSE;
            
            // Get total number of templates from the names list since that matches our population
            JAVA_uint32 totalNumberOfTemplates = new JAVA_uint32();
            totalNumberOfTemplates.value = names.size();
            
            JAVA_uint32 maxNumberOfResults = new JAVA_uint32();
            maxNumberOfResults.value = 1; // We only want the best match
            
            JAVA_uint32 numberReturned = new JAVA_uint32();
            
            JAVA_BioAPI_CANDIDATE[] candidates = new JAVA_BioAPI_CANDIDATE[1];
            candidates[0] = new JAVA_BioAPI_CANDIDATE();
            
            JAVA_sint32 timeout = new JAVA_sint32();
            timeout.value = 0;
            
            JAVA_sint32 templateUpdate = new JAVA_sint32();
            templateUpdate.value = PalmSecureConstant.JAVA_BioAPI_FALSE;

            // Call identify API with all required parameters
            try {
                stResult.result = palmsecureIf.JAVA_BioAPI_Identify(
                    moduleHandle,           // JAVA_uint32 
                    maxFRRRequested,        // JAVA_sint32
                    farRequested,           // JAVA_sint32
                    farPrecedence,          // JAVA_uint32
                    population,             // JAVA_BioAPI_IDENTIFY_POPULATION
                    totalNumberOfTemplates, // JAVA_uint32
                    maxNumberOfResults,     // JAVA_uint32
                    numberReturned,         // JAVA_uint32 
                    candidates,             // JAVA_BioAPI_CANDIDATE[]
                    timeout,                // JAVA_sint32
                    templateUpdate          // JAVA_sint32
                );

                // If identification succeeds, get the matched index from candidates
                if (stResult.result == PalmSecureConstant.JAVA_BioAPI_OK && 
                    numberReturned.value > 0 && candidates[0] != null) {
                    // Get the index from the candidate using reflection
                    int matchedIndex = -1;
                    try {
                        java.lang.reflect.Field[] fields = candidates[0].getClass().getFields();
                        for (java.lang.reflect.Field field : fields) {
                            String fieldName = field.getName().toLowerCase();
                            if (fieldName.contains("index") || fieldName.contains("bir")) {
                                Object value = field.get(candidates[0]);
                                if (value instanceof Number) {
                                    matchedIndex = ((Number)value).intValue();
                                    if (BuildConfig.DEBUG) {
                                        Log.d(TAG, "Found index field: " + field.getName() + 
                                              " with value: " + matchedIndex);
                                    }
                                    break;
                                }
                            }
                        }
                    } catch (Exception e) {
                        if (BuildConfig.DEBUG) {
                            Log.e(TAG, "Error getting index from candidate", e);
                        }
                    }

                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "Identify result - MatchedIndex: " + matchedIndex + 
                              ", Names size: " + names.size() +
                              ", Available IDs: " + String.join(", ", names));
                    }

                    // Map index back to names list to get user ID
                    if (matchedIndex >= 0 && matchedIndex < names.size()) {
                        String matchedId = names.get(matchedIndex);
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "Match found - Index: " + matchedIndex + 
                                  ", Matched ID: " + matchedId);
                        }
                        stResult.authenticated = true;
                        stResult.userId = new java.util.ArrayList<String>();
                        stResult.userId.add(matchedId);
                    }
                }
            } catch (PalmSecureException e) {
                if (BuildConfig.DEBUG) Log.e(TAG, "Identify failed", e);
                stResult.result = PalmSecureConstant.JAVA_BioAPI_ERRCODE_FUNCTION_FAILED;
                stResult.pseErrNumber = e.ErrNumber;
            }

            if (stResult.result != PalmSecureConstant.JAVA_BioAPI_OK || !stResult.authenticated) {
                // If we haven't already authenticated in the previous block, mark as failed
                stResult.authenticated = false;
            }

        } catch (PsAplException | PalmSecureException e) {
            if (BuildConfig.DEBUG) Log.e(TAG, "Identify failed", e);
            stResult.result = PalmSecureConstant.JAVA_BioAPI_ERRCODE_FUNCTION_FAILED;
            stResult.authenticated = false;
        }

        Ps_Sample_Apl_Java_NotifyResult_Identify(stResult);
    }
}
