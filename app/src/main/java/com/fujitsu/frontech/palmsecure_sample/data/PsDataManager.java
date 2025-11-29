/*
 * PsDataManager.java
 *
 *	All Rights Reserved, Copyright(c) FUJITSU FRONTECH LIMITED 2021
 */

package com.fujitsu.frontech.palmsecure_sample.data;

import java.io.IOException;
import java.util.ArrayList;
import android.util.Base64;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.fujitsu.frontech.palmsecure.JAVA_BioAPI_BIR;
import com.fujitsu.frontech.palmsecure.JAVA_BioAPI_BIR_ARRAY_POPULATION;
import com.fujitsu.frontech.palmsecure.JAVA_BioAPI_IDENTIFY_POPULATION;
import com.fujitsu.frontech.palmsecure.util.PalmSecureConstant;
import com.fujitsu.frontech.palmsecure.util.PalmSecureException;
import com.fujitsu.frontech.palmsecure.util.PalmSecureHelper;
import com.fujitsu.frontech.palmsecure_sample.exception.PsAplException;
import com.fujitsu.frontech.palmsecure_gui_sample.BuildConfig;
import com.fujitsu.frontech.palmsecure_gui_sample.R;
import com.google.firebase.firestore.*;

public class PsDataManager {

	private static final String TAG = "PsDataManager";
	private static final String VEIN_DB_NAME = "PsVeinData.sqlite3";
	// Bump DB version to apply schema migration for multiple templates per user
	private static final int VERSION = 2;
	private final String mSensorType;
	private final String mDataType;
	private final PsDbHelper mDbHelper;
    private final FirebaseFirestore dbFirestore = FirebaseFirestore.getInstance();

	public PsDataManager(Context cx, long sensorType, long dataType) {
		if (BuildConfig.DEBUG) {
			Log.i(TAG, "new PsDataManager");
		}

		mDbHelper = new PsDbHelper(cx, VEIN_DB_NAME);
		mSensorType = Long.toString(sensorType);
		mDataType = Long.toString(dataType);
	}

	private static class PsDbHelper extends SQLiteOpenHelper {

		public PsDbHelper(Context context, String name) {
			super(context, name, null, VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// Use an autoincrement primary key to allow multiple templates per id
			db.execSQL(
					"create table veindata_table(" +
						" recid INTEGER PRIMARY KEY AUTOINCREMENT," +
						" sensortype long," +
						" datatype long," +
						" id text," +
						" veindata blob" +
						");"
			);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// Migrate from version 1 schema (primary key on id,sensortype,datatype)
			// to version 2 schema (autoincrement recid) while preserving existing data.
			if (oldVersion < 2) {
				db.beginTransaction();
				try {
					// Create new table with desired schema
					db.execSQL("create table veindata_table_new( recid INTEGER PRIMARY KEY AUTOINCREMENT, sensortype long, datatype long, id text, veindata blob );");
					// Copy data from old table to new table if exists
					try {
                        db.execSQL("INSERT INTO veindata_table_new(sensortype, datatype, id, veindata) SELECT sensortype, datatype, id, veindata FROM veindata_table;");
					} catch (Exception ignore) {
						// If old table doesn't exist or insert fails, ignore and continue
					}
					// Drop old table if exists and rename new table
					try { db.execSQL("DROP TABLE IF EXISTS veindata_table;"); } catch (Exception ignore) {}
					db.execSQL("ALTER TABLE veindata_table_new RENAME TO veindata_table;");
					db.setTransactionSuccessful();
				} finally {
					db.endTransaction();
				}
			}
		}
	}

	public boolean convertBioAPI_DataToDB(JAVA_BioAPI_BIR Data, String Name) throws PsAplException, PalmSecureException {

		byte[] veinData = null;
		SQLiteDatabase db = null;
		boolean return_value = false;
		long db_result = 0;

		if (Data == null || Name == null)
		{
			PsAplException pae = new PsAplException(R.string.AplErrorSystemError);
			throw pae;
		}

		//Create a byte array of vein data
		///////////////////////////////////////////////////////////////////////////
		try {
			veinData = PalmSecureHelper.convertBIRToByte(Data);
		} catch (IOException e) {
			if (BuildConfig.DEBUG) {
				Log.e(TAG, "PalmSecureHelper.convertBIRToByte", e);
			}
			PsAplException pae = new PsAplException(R.string.AplErrorSystemError);
			throw pae;
		} catch (PalmSecureException pse) {
			if (BuildConfig.DEBUG) {
				Log.e(TAG, "PalmSecureHelper.convertBIRToByte", pse);
			}
			throw pse;
		}
		///////////////////////////////////////////////////////////////////////////

		try {
			db = mDbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("id", Name);
			values.put("sensortype", Long.valueOf(mSensorType));
			values.put("datatype", Long.valueOf(mDataType));
			values.put("veindata", veinData);

			db_result = db.insert("veindata_table", null, values);

            // トランザクションを使用して安全に連番を取得
            byte[] finalVeinData = veinData;
            dbFirestore.runTransaction((Transaction.Function<Long>) transaction -> {
                DocumentReference counterRef = dbFirestore.collection("numberings").document("veinId");
                DocumentSnapshot snapshot = transaction.get(counterRef);

                long current = snapshot.getLong("seq"); // 現在の番号を取得
                long next = current + 1;                    // 次の番号を計算

                // Firestoreに次の番号を保存
                transaction.update(counterRef, "seq", next);

                return next; // これがトランザクション結果として返される
            }).addOnSuccessListener(nextNumber -> {
                // nextNumberが次の連番
                Log.d("Firestore", "取得した連番: " + nextNumber);

                // 静脈データをエンコード
                String encodedData = Base64.encodeToString(finalVeinData, Base64.DEFAULT);

                // ここで他のコレクションやドキュメントに利用可能
                Map<String, Object> data = new HashMap<>();
                data.put("recid", nextNumber);
                data.put("id", Name);
                data.put("sensortype", Long.valueOf(mSensorType));
                data.put("datatype", Long.valueOf(mDataType));
                data.put("veindata", encodedData);

                dbFirestore.collection("veindata").document(Name) // 例: ordersコレクション
                        .set(data)
                        .addOnSuccessListener(aVoid -> Log.d("Firestore", "連番を保存成功"))
                        .addOnFailureListener(e -> Log.w("Firestore", "保存失敗", e));

            }).addOnFailureListener(e -> {
                Log.w("Firestore", "連番取得失敗", e);
            });

			if (db_result > 0) {
				return_value = true;
			}
		} catch (SQLiteException e) {
			if (BuildConfig.DEBUG) {
				Log.e(TAG, "convertBioAPI_DataToDB", e);
			}
			PsAplException pae = new PsAplException(R.string.AplErrorSystemError);
			throw pae;
		} finally {
			if (db != null) {
				db.close();
			}
		}

		return return_value;
	}

	public JAVA_BioAPI_BIR convertDBToBioAPI_Data(String Name) throws PsAplException, PalmSecureException {

		SQLiteDatabase db = null;
		Cursor c = null;
		// Return the most recent template for the given id (supports multiple templates per id)
		String sql = "select id, veindata from veindata_table where id = ? and sensortype = ? and datatype = ? order by recid desc limit 1;";
		byte[] veindata = null;

		if (Name == null)
		{
			PsAplException pae = new PsAplException(R.string.AplErrorSystemError);
			throw pae;
		}

		try {
			db = mDbHelper.getReadableDatabase();
			c = db.rawQuery(sql, new String[] { Name, mSensorType, mDataType });

			if (c != null && c.moveToFirst()) {
				veindata = c.getBlob(c.getColumnIndex("veindata"));
			}
		} catch (SQLiteException e) {
			if (BuildConfig.DEBUG) {
				Log.e(TAG, "convertDBToBioAPI_Data", e);
			}
			PsAplException pae = new PsAplException(R.string.AplErrorSystemError);
			throw pae;
		} finally {
			if (c != null) {
				c.close();
			}
			if (db != null) {
				db.close();
			}
		}

		JAVA_BioAPI_BIR bir = null;

		try {
			bir = PalmSecureHelper.convertByteToBIR(veindata);
		} catch (IOException e) {
			if (BuildConfig.DEBUG) {
				Log.e(TAG, "PalmSecureHelper.convertByteToBIR", e);
			}
			PsAplException pae = new PsAplException(R.string.AplErrorSystemError);
			throw pae;
		} catch (PalmSecureException pse) {
			if (BuildConfig.DEBUG) {
				Log.e(TAG, "PalmSecureHelper.convertByteToBIR", pse);
			}
			throw pse;
		}

		return bir;
	}

	public JAVA_BioAPI_IDENTIFY_POPULATION convertDBToBioAPI_Data_All(ArrayList<String> Name)
			throws PsAplException, PalmSecureException {

		JAVA_BioAPI_IDENTIFY_POPULATION population = new JAVA_BioAPI_IDENTIFY_POPULATION();

		SQLiteDatabase db = null;
		Cursor c = null;
		// 最新のテンプレートを取得するために recid desc で並べ替え
		String sql = "select id, veindata from veindata_table where sensortype = ? and datatype = ? " +
					"group by id having recid = max(recid) order by id;";

		if (Name == null)
		{
			PsAplException pae = new PsAplException(R.string.AplErrorSystemError);
			throw pae;
		}

		try {
			db = mDbHelper.getReadableDatabase();
			c = db.rawQuery(sql, new String[] { mSensorType, mDataType });
		} catch (SQLiteException e) {
			if (BuildConfig.DEBUG) {
				Log.e(TAG, "convertDBToBioAPI_Data_All", e);
			}
			if (c != null) {
				c.close();
			}
			if (db != null) {
				db.close();
			}

			PsAplException pae = new PsAplException(R.string.AplErrorSystemError);
			throw pae;
		}

		int memberNum = c.getCount();

		population.Type = PalmSecureConstant.JAVA_BioAPI_ARRAY_TYPE;
		population.BIRArray = new JAVA_BioAPI_BIR_ARRAY_POPULATION();
		population.BIRArray.NumberOfMembers = memberNum;

		JAVA_BioAPI_BIR[] members = new JAVA_BioAPI_BIR[memberNum];

		try {
			if (c != null && c.moveToFirst()) {
				int membersIndex = 0;
				int columnId = c.getColumnIndex("id");
				int columnVeindata = c.getColumnIndex("veindata");
				Name.clear();
				do {
					String id = c.getString(columnId);
					Name.add(id);
					if (BuildConfig.DEBUG) {
						Log.d(TAG, "Loading template for ID: " + id + " at index: " + membersIndex);
					}
					members[membersIndex] = PalmSecureHelper.convertByteToBIR(c.getBlob(columnVeindata));
					membersIndex++;
				} while (c.moveToNext());
			}
		} catch (IOException e) {
			if (BuildConfig.DEBUG) {
				Log.e(TAG, "PalmSecureHelper.convertByteToBIR", e);
			}
			PsAplException pae = new PsAplException(R.string.AplErrorSystemError);
			throw pae;
		} catch (PalmSecureException pse) {
			if (BuildConfig.DEBUG) {
				Log.e(TAG, "PalmSecureHelper.convertByteToBIR", pse);
			}
			throw pse;
		} finally {
			if (c != null) {
				c.close();
			}
			if (db != null) {
				db.close();
			}
		}

		population.BIRArray.Members = members;

		return population;
	}

	// Return all templates (BIR) for a specific user id
	public JAVA_BioAPI_BIR[] convertDBToBioAPI_Data_AllForId(String Name) throws PsAplException, PalmSecureException {

		if (Name == null) {
			PsAplException pae = new PsAplException(R.string.AplErrorSystemError);
			throw pae;
		}

		SQLiteDatabase db = null;
		Cursor c = null;
		String sql = "select veindata from veindata_table where id = ? and sensortype = ? and datatype = ? order by recid desc;";

		try {
			db = mDbHelper.getReadableDatabase();
			c = db.rawQuery(sql, new String[] { Name, mSensorType, mDataType });
			int memberNum = 0;
			if (c != null) memberNum = c.getCount();
			JAVA_BioAPI_BIR[] members = new JAVA_BioAPI_BIR[memberNum];
			int idx = 0;
			if (c != null && c.moveToFirst()) {
				int columnVeindata = c.getColumnIndex("veindata");
				do {
					members[idx] = PalmSecureHelper.convertByteToBIR(c.getBlob(columnVeindata));
					idx++;
				} while (c.moveToNext());
			}
			return members;
		} catch (IOException e) {
			if (BuildConfig.DEBUG) {
				Log.e(TAG, "PalmSecureHelper.convertByteToBIR", e);
			}
			PsAplException pae = new PsAplException(R.string.AplErrorSystemError);
			throw pae;
		} catch (PalmSecureException pse) {
			if (BuildConfig.DEBUG) {
				Log.e(TAG, "PalmSecureHelper.convertByteToBIR", pse);
			}
			throw pse;
		} finally {
			if (c != null) c.close();
			if (db != null) db.close();
		}
	}

	public void deleteDBToBioAPI_Data(String Name) throws PsAplException {

		SQLiteDatabase db = null;
		long db_result = 0;

		if (Name == null)
		{
			PsAplException pae = new PsAplException(R.string.AplErrorSystemError);
			throw pae;
		}

		try {
			db = mDbHelper.getWritableDatabase();
			// Delete all templates for the given id (keeps backward compatibility)
			db_result = db.delete(
					"veindata_table",
					"id = ? and sensortype = ? and datatype = ?",
					new String[] { Name, mSensorType, mDataType });

            dbFirestore.collection("veindata").document(Name)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firestore", "Field successfully deleted!");
                    })
                    .addOnFailureListener(e -> {
                        Log.w("Firestore", "Error deleting field", e);
                    });

			if (db_result <= 0) {
				PsAplException pae = new PsAplException(R.string.AplErrorFileDelete);
				throw pae;
			}
		} catch (SQLiteException e) {
			if (BuildConfig.DEBUG) {
				Log.e(TAG, "deleteBioAPI_DataToDB Name = " + Name);
			}
			PsAplException pae = new PsAplException(R.string.AplErrorSystemError);
			throw pae;
		} finally {
			if (db != null) {
				db.close();
			}
		}

		return;
	}
}
