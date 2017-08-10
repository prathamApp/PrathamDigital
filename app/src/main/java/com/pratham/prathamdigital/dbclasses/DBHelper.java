package com.pratham.prathamdigital.dbclasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "PraDiGi.db";
    public SQLiteDatabase database;
    public SQLiteDatabase db;
    public ContentValues contentValues;
    public Context c;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        try {
            c = context;
            contentValues = new ContentValues();
        } catch (Exception e) {

        }

    }


    public SQLiteDatabase GetWriteableDatabaseInstance() {
        database = this.getWritableDatabase();
        return database;
    }

    public SQLiteDatabase GetReadableDatabaseInstance() {
        database = this.getReadableDatabase();
        return database;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try {

            db.execSQL(DatabaseInitialization.GoogleLoginTable);

        } catch (Exception e) {

        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {

            db.execSQL("Drop table if exists GoogleData");

        } catch (Exception e) {

        }
    }

}
