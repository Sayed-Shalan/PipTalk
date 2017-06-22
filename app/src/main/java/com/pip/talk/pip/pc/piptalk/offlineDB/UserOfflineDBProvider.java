package com.pip.talk.pip.pc.piptalk.offlineDB;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by pc on 3/17/2017.
 */

public class UserOfflineDBProvider extends ContentProvider {

    // fields for my content provider
    static final String PROVIDER_NAME = "com.pip.talk.pip.pc.piptalk.provider";
    static final String URL = "content://" + PROVIDER_NAME + "/user_data";
   public static final Uri CONTENT_URI = Uri.parse(URL);

    // fields for the database (user data)
   public static final String USER__ID = "user_id";
   public static final String USERNAME = "username";
   public static final String USER_TOKEN = "user_token";
   public static final String NATIVE_LANGUAGE = "native_language";
   public static final String GENDER = "gender";
   public static final String HAS_IMAGE = "has_image";
    public static final String STATUS = "status";
    public static final String PHONE_NUMBER = "phone_number";
    public static final String DOB = "date_of_birth";

    static final int USER = 1;
    static final int USER_ID = 2;
    DBHelper dbHelper;
    // projection map for a query
    private static HashMap<String, String> userMap;

    // maps content URI "patterns" to the integer values that were set above
    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "user_data", USER);
        uriMatcher.addURI(PROVIDER_NAME, "user_data/#", USER_ID);
    }
    // database declarations
    private SQLiteDatabase database;
    static final String TABLE_NAME = "user_data";




    // only for overriding
    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new DBHelper(context);
        // permissions to be writable
        database = dbHelper.getWritableDatabase();

        if(database == null)
            return false;
        else
            return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        // the TABLE_NAME to query on
        queryBuilder.setTables(TABLE_NAME);
        switch (uriMatcher.match(uri))
        {
            // maps all database column names
            case USER:
                queryBuilder.setProjectionMap(userMap);
                break;
            case USER_ID:
                queryBuilder.appendWhere( USER__ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (sortOrder == null || sortOrder == ""){
            // No sorting-> sort on names by default
            sortOrder = USER__ID;
        }
        Cursor cursor = queryBuilder.query(database, projection, selection,
                selectionArgs, null, null, sortOrder);
        /**
         * register to watch a content URI for changes
         */
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            // Get all friend-birthday records
            case USER:
                return "DIR";
            // Get a particular friend
            case USER_ID:
                return "ITEM";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long row = database.insert(TABLE_NAME, "", values);
        // If record is added successfully
        Log.e("ZZZ","ros is :" +row);
        if(row >= 0) {
            Uri newUri = ContentUris.withAppendedId(CONTENT_URI, row);
            getContext().getContentResolver().notifyChange(newUri, null);
            return newUri;
        }else {
            throw new SQLException("Fail to add a new record into " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){
            case USER:
                // delete all the records of the table
                count = database.delete(TABLE_NAME, selection, selectionArgs);
                break;
            case USER_ID:
                String id = uri.getLastPathSegment();	//gets the id
                count = database.delete( TABLE_NAME, USER__ID +  " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                                selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)){
            case USER:
                count = database.update(TABLE_NAME, values, selection, selectionArgs);
                break;
            case USER_ID:
                count = database.update(TABLE_NAME, values, USER__ID +
                        " = " + uri.getLastPathSegment() +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                                selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI " + uri );
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;    }
}
