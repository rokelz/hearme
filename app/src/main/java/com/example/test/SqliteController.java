package com.example.test;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Created by USER on 5/9/2016.
 *
 * @author R.K.Opatha (IT13510572)
 *         Use to handle the database activities.
 * @see SQLiteOpenHelper
 */
public class SqliteController extends SQLiteOpenHelper {

    private final static Logger LOGGER = Logger.getLogger(SingleConversationActivity.class.getName());
    private static final String LOGCAT = null;

    public SqliteController(Context applicationcontext) {
        super(applicationcontext, "androidsqlite.db", null, 1);
        LOGGER.info("Android SQLiteDB is being created");
        Log.d(LOGCAT, "Created the Android SQLiteDB");
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String query;
        query = "CREATE TABLE SoundProfiles ( id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, profileName TEXT, left INTEGER, right INTEGER)";
        database.execSQL(query);
        Log.d(LOGCAT, "SoundProfiles Table Created");
        query = "CREATE TABLE recordings ( id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, recordingName TEXT, path TEXT)";
        database.execSQL(query);
        Log.d(LOGCAT, "recordings Table Created");
        query = "CREATE TABLE SoundProfilesLog ( id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, profileName TEXT,logdate TEXT ,left INTEGER, right INTEGER)";
        database.execSQL(query);
        query = "INSERT INTO SoundProfiles (profileName,left,right) VALUES ('classroom',55,75) ";
        database.execSQL(query);
        Log.d(LOGCAT, "done inserting profile.....");
        query = "INSERT INTO SoundProfilesLog (profileName,logdate,left,right) VALUES ('classroom','2016-05-4',55,75) ";
        database.execSQL(query);
        query = "INSERT INTO SoundProfilesLog (profileName,logdate,left,right) VALUES ('classroom','2016-05-10',65,70) ";
        database.execSQL(query);
        query = "INSERT INTO SoundProfilesLog (profileName,logdate,left,right) VALUES ('classroom','2016-05-11',75,65) ";
        database.execSQL(query);
        query = "INSERT INTO SoundProfilesLog (profileName,logdate,left,right) VALUES ('classroom','2016-05-14',85,60) ";
        database.execSQL(query);
        query = "INSERT INTO SoundProfilesLog (profileName,logdate,left,right) VALUES ('classroom','2016-05-16',95,65) ";
        database.execSQL(query);
        query = "INSERT INTO SoundProfilesLog (profileName,logdate,left,right) VALUES ('classroom','2016-05-17',105,60) ";
        database.execSQL(query);
        query = "INSERT INTO SoundProfilesLog (profileName,logdate,left,right) VALUES ('classroom','2016-05-18',55,60) ";
        database.execSQL(query);
        Log.d(LOGCAT, "done inserting logs.....");


    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version) {
        LOGGER.info("On Upgrade of Android SQLiteDB");
        String query;
        query = "DROP TABLE IF EXISTS SoundProfiles";
        database.execSQL(query);
        onCreate(database);
    }

    public ArrayList<HashMap<String, String>> getGraphDetails(String profileId) {
        LOGGER.info("SQLiteDB Graph details retrieval");
        ArrayList<HashMap<String, String>> profileList;
        profileList = new ArrayList<HashMap<String, String>>();

        String selectQuery = "SELECT * FROM SoundProfilesLog where profileName = '" + profileId + "'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("id", Integer.toString(cursor.getInt(0)));
                map.put("profileName", cursor.getString(1));
                map.put("logdate", cursor.getString(2));
                map.put("left", Integer.toString(cursor.getInt(3)));
                map.put("right", Integer.toString(cursor.getInt(4)));
                profileList.add(map);
                System.out.println(profileList);
            } while (cursor.moveToNext());

        }
        return profileList;
    }

    public int getNumberOFProfiles() {
        LOGGER.info("SQLiteDB to get number of profiles");
        String selectQuery = "SELECT * FROM SoundProfiles";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            do {
                count++;
            } while (cursor.moveToNext());

        }
        return count;
    }

    public void retakeProfile(HashMap<String, String> queryValues, String name, String id) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        int leftval = Integer.parseInt(queryValues.get("left"));
        int rightval = Integer.parseInt(queryValues.get("right"));
        String insertQuery = "UPDATE SoundProfiles SET left=" + leftval + ", right=" + rightval + " WHERE profileName='" + name + "' AND id = " + id + "";
        database.execSQL(insertQuery);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());
        Log.d(LOGCAT, "Took time tooo");
        insertQuery = "INSERT INTO SoundProfilesLog (profileName,logdate,left,right) VALUES ('" + name + "','" + formattedDate + "'," + leftval + "," + rightval + ") ";
        database.execSQL(insertQuery);
        Log.d(LOGCAT, "Data updated in SoundProfiles Table Successfully");
        database.close();
    }

    public void insertProfile(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());
        int leftval = Integer.parseInt(queryValues.get("left"));
        int rightval = Integer.parseInt(queryValues.get("right"));
        String insertQuery = "INSERT INTO SoundProfiles (profileName,left,right)\n" +
                "VALUES ('" + queryValues.get("profileName") + "'," + leftval + "," + rightval + ")";
        database.execSQL(insertQuery);
        insertQuery = "INSERT INTO SoundProfilesLog (profileName,logdate,left,right)\n" +
                "VALUES ('" + queryValues.get("profileName") + "'," + formattedDate + "," + leftval + "," + rightval + ")";
        database.execSQL(insertQuery);
        Log.d(LOGCAT, "Data inserted to SoundProfiles Table Successfully");
        database.close();
    }

    public void insertrecording(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
//        int leftval = Integer.parseInt(queryValues.get("left"));
        //int rightval = Integer.parseInt(queryValues.get("right"));
        String insertQuery = "INSERT INTO recordings (recordingName,path)\n" +
                "VALUES ('" + queryValues.get("recordingName") + "','" + queryValues.get("path") + "')";
        database.execSQL(insertQuery);
        Log.d(LOGCAT, "Data inserted to recordings Table Successfully");
        database.close();
    }

    public ArrayList<HashMap<String, String>> getAllProfiles() {
        ArrayList<HashMap<String, String>> profileList;
        profileList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT * FROM SoundProfiles";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("id", Integer.toString(cursor.getInt(0)));
                map.put("profileName", cursor.getString(1));
                map.put("left", Integer.toString(cursor.getInt(2)));
                map.put("right", Integer.toString(cursor.getInt(3)));
                profileList.add(map);
                System.out.println(profileList);
            } while (cursor.moveToNext());

        }
        return profileList;
    }

    public ArrayList<HashMap<String, String>> getAllRecordings() {
        ArrayList<HashMap<String, String>> recordList;
        recordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT * FROM recordings";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("id", Integer.toString(cursor.getInt(0)));
                map.put("recordings", cursor.getString(1));
                map.put("path", cursor.getString(2));
                recordList.add(map);
                System.out.println(recordList);
            } while (cursor.moveToNext());

        }
        return recordList;
    }

    public void deleteProfile(String id) {
        Log.d(LOGCAT, "delete");
        SQLiteDatabase database = this.getWritableDatabase();
        int idArr = Integer.parseInt(id);
        String deleteQuery = "DELETE FROM SoundProfiles where id='" + idArr + "'";
        Log.d("query", deleteQuery);
        database.execSQL(deleteQuery);
    }

    public void editProfile(String id, String newProf) {
        Log.d(LOGCAT, "edit");
        SQLiteDatabase database = this.getWritableDatabase();
        int idArr = Integer.parseInt(id);
        String deleteQuery = "UPDATE SoundProfiles SET profileName='" + newProf + "' WHERE id='" + idArr + "'";
        Log.d("query", deleteQuery);
        database.execSQL(deleteQuery);

    }

    public void editRecordingName(String id, String newProf, String path) {
        Log.d(LOGCAT, "edit");
        SQLiteDatabase database = this.getWritableDatabase();
        int idArr = Integer.parseInt(id);
        String deleteQuery = "UPDATE recordings SET recordingName='" + newProf + "',path='" + path + "' WHERE id=" + idArr + "";
        Log.d("query", deleteQuery);
        database.execSQL(deleteQuery);

    }

    public void deleteRecording(String id) {
        Log.d(LOGCAT, "edit");
        SQLiteDatabase database = this.getWritableDatabase();
        int idArr = Integer.parseInt(id);
        String deleteQuery = "DELETE from recordings WHERE id=" + idArr + "";
        Log.d("query", deleteQuery);
        database.execSQL(deleteQuery);

    }


}