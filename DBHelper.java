package com.example.sqlitersa;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper  extends SQLiteOpenHelper { // DBHelper own class inherited from SQLiteOpenHelper

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "oflameronDB"; // Database name
    public static final String TABLE_CONTACTS = "NTable"; // Table name

    public static final String KEY_ID = "_id"; // This name is used in Android to work with cursors
    public static final String KEY_NAME = "name";
    public static final String KEY_MAIL = "mail";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_NOTE = "note";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) { // Required abstract method. Called when the database is first created
        db.execSQL("create table " + TABLE_CONTACTS + "(" + KEY_ID
                + " integer primary key," + KEY_NAME + " text," + KEY_MAIL + " text," + KEY_PHONE + " text," + KEY_NOTE + " text" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { // Required abstract method. Called when the database is modified
        db.execSQL("drop table if exists " + TABLE_CONTACTS);

        onCreate(db);
    }

}
