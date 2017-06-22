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

import java.util.HashMap;



public class ChatOfflineDBProvider extends ContentProvider {
    // fields for my content provider
    static final String PROVIDER_NAME = "com.pip.talk.pip.pc.piptalk.chatter";
    static final String URL = "content://" + PROVIDER_NAME + "/chat_data";
    public static final Uri CONTENT_URI = Uri.parse(URL);

    // fields for the database (user data)
    public static final String MESSAGE_ID = "message_id";
    public static final String SENDER_ID = "sender_id";
    public static final String SENDER_NAME = "sender_name";
    public static final String SENDER_HAS_IMAGE = "sender_has_image";
    public static final String RECEIVER_ID = "receiver_id";
    public static final String RECEIVER_NAME = "receiver_name";
    public static final String RECEIVER_HAS_IMAGE = "receiver_has_image";
    public static final String MESSAGE_BODY = "message_body";
    public static final String MESSAGE_LANG_CODE = "msg_lang_code";
    public static final String TIME = "time";
    public static final String DATE = "date";
    public static final String M_SENT = "m_sent";
    public static final String M_RECEIVED = "m_received";

    static final int CHAT = 1;
    static final int CHAT_ID = 2;
    DBHelper dbHelper;
    // projection map for a query
    private static HashMap<String, String> chatMap;
    // maps content URI "patterns" to the integer values that were set above
    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "chat_data", CHAT);
        uriMatcher.addURI(PROVIDER_NAME, "chat_data/#", CHAT_ID);
    }
    // database declarations
    private SQLiteDatabase database;
    static final String TABLE_NAME = "chat_data";

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
            case CHAT:
                queryBuilder.setProjectionMap(chatMap);
                break;
            case CHAT_ID:
                queryBuilder.appendWhere( MESSAGE_ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (sortOrder == null || sortOrder == ""){
            // No sorting-> sort on names by default
            sortOrder = TIME;
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
            case CHAT:
                return "DIR";
            // Get a particular friend
            case CHAT_ID:
                return "ITEM";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        long row = database.insert(TABLE_NAME, "", values);
        // If record is added successfully
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
            case CHAT:
                // delete all the records of the table
                count = database.delete(TABLE_NAME, selection, selectionArgs);
                break;
            case CHAT_ID:
                String id = uri.getLastPathSegment();	//gets the id
                count = database.delete( TABLE_NAME, MESSAGE_ID +  " = " + id +
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
            case CHAT:
                count = database.update(TABLE_NAME, values, selection, selectionArgs);
                break;
            case CHAT_ID:
                count = database.update(TABLE_NAME, values, MESSAGE_ID +
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
