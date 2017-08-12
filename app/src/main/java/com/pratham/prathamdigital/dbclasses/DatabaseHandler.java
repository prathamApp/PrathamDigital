package com.pratham.prathamdigital.dbclasses;

import java.lang.reflect.Type;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Modal_DownloadContent;

/**
 * Created by HP on 12-08-2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "PrathamDB";

    // Contacts table name
    private static final String TABLE_CONTENTS = "table_contents";
    private static final String TABLE_DOWNLOADED = "table_downloaded";

    // Contacts Table Columns names
    private static final String KEY_NODELIST = "nodelist";
    private static final String KEY_FOLDER_NAME = "foldername";
    private static final String KEY_DOWNLOADURL = "downloadUrl";
    private static final String KEY_RESOURCEID = "resourceid";
    private static final String KEY_NODEID = "nodeid";
    private static final String KEY_NODETITLE = "nodetitle";

    private ArrayList<Modal_DownloadContent> contents = new ArrayList<>();

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTENTS + "("
                + KEY_FOLDER_NAME + " TEXT,"
                + KEY_NODELIST + " TEXT,"
                + KEY_DOWNLOADURL + " TEXT" + ")";
        String CREATE_DOWNLOADED_TABLE = "CREATE TABLE " + TABLE_DOWNLOADED + "("
                + KEY_RESOURCEID + " TEXT,"
                + KEY_NODEID + " TEXT,"
                + KEY_NODETITLE + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_DOWNLOADED_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTENTS);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new contact
    public void Add_Content(Modal_DownloadContent content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_FOLDER_NAME, content.getFoldername());
        Gson gson = new Gson();
        String nodeArray = gson.toJson(content.getNodelist());
        values.put(KEY_NODELIST, nodeArray);
        values.put(KEY_DOWNLOADURL, content.getDownloadurl());
        // Inserting Row
        db.insert(TABLE_CONTENTS, null, values);
        db.close(); // Closing database connection
    }

    // Adding new downloaded file detail
    public void Add_DOownloadedFileDetail(Modal_ContentDetail content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_RESOURCEID, content.getResourceid());
        values.put(KEY_NODEID, content.getNodeid());
        values.put(KEY_NODETITLE, content.getNodetitle());
        // Inserting Row
        db.insert(TABLE_DOWNLOADED, null, values);
        db.close(); // Closing database connection
    }

    // Getting All Contents
    public ArrayList<Modal_DownloadContent> Get_Contents() {
        try {
            contents.clear();

            // Select All Query
            String selectQuery = "SELECT  * FROM " + TABLE_CONTENTS;

            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    Modal_DownloadContent downloadContent = new Modal_DownloadContent();
                    downloadContent.setFoldername(cursor.getString(0));
                    Gson gson = new Gson();
                    Type type = new TypeToken<ArrayList<Modal_ContentDetail>>() {
                    }.getType();
                    Log.d("retrieve_gson::", cursor.getString(0));
                    Log.d("retrieve_gson::", cursor.getString(1));
                    Log.d("retrieve_gson::", cursor.getString(2));
                    ArrayList<Modal_ContentDetail> detailArrayList = gson.fromJson(cursor.getString(1), type);
                    downloadContent.setNodelist(detailArrayList);
                    downloadContent.setDownloadurl(cursor.getString(2));
                    // Adding contact to list
                    contents.add(downloadContent);
                } while (cursor.moveToNext());
            }

            // return contact list
            cursor.close();
            db.close();
            return contents;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        return contents;
    }

    // Getting All Downloaded Contents Resource IDS
    public ArrayList<String> getDownloadContentID() {
        ArrayList<String> ids=new ArrayList<>();
        try {
            // Select All Query
            String selectQuery = "SELECT  * FROM " + TABLE_DOWNLOADED;

            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    // Adding contact to list
                    ids.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }

            // return contact list
            cursor.close();
            db.close();
            return ids;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        return ids;
    }

    // Updating single contact
//    public int Update_Contact(Contact contact) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(KEY_NAME, contact.getName());
//        values.put(KEY_PH_NO, contact.getPhoneNumber());
//        values.put(KEY_EMAIL, contact.getEmail());
//
//        // updating row
//        return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
//                new String[]{String.valueOf(contact.getID())});
//    }

    // Deleting single contact
//    public void Delete_Contact(int id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
//                new String[]{String.valueOf(id)});
//        db.close();
//    }

    // Getting contacts Count
    public int Get_Total_Contacts() {
        String countQuery = "SELECT  * FROM " + TABLE_CONTENTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

}