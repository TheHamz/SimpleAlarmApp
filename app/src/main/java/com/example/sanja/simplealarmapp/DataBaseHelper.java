package com.example.sanja.simplealarmapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Hamz on 10.1.2016..
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    private final static int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "AlarmDB";

    private static final String TABLE_NAME = "scheduledAlarm";
    private static final String KEY_ID = "id";
    private static final String KEY_HOUR = "hour";
    private static final String KEY_MINUTE = "minute";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MEMBER_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_HOUR + " INTEGER," + KEY_MINUTE + ", INTEGER)";
        String INSERT_DEFAULT_ROW = "INSERT INTO " + TABLE_NAME + "(" + KEY_ID + "," + KEY_HOUR + "," + KEY_MINUTE + ")" + " VALUES(1, 10, 10)";
        db.execSQL(CREATE_MEMBER_TABLE);
        db.execSQL(INSERT_DEFAULT_ROW);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public Alarm getAlarm() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{KEY_ID, KEY_HOUR, KEY_MINUTE}, KEY_ID + "=1", null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Alarm alarm = new Alarm(cursor.getInt(1), cursor.getInt(2));
        cursor.close();
        db.close();
        return alarm;
    }

    public void updateAlarm(Alarm alarm) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_HOUR, alarm.getHour());
        values.put(KEY_MINUTE, alarm.getMinute());
        String selection = KEY_ID + " = ?";
        String[] selectionArgs = {String.valueOf(1)};
        db.update(TABLE_NAME, values, selection, selectionArgs);
        db.close();
    }

}
