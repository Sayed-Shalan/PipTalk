package com.pip.talk.pip.pc.piptalk.offlineDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by pc on 3/17/2017.
 */

 class DBHelper extends SQLiteOpenHelper {

   private static final String DATABASE_NAME = "piptalkdb";
   private static final int DATABASE_VERSION = 15;

    static final String USER = "user_data";
    static final String CREATE_USER_TABLE =
            " CREATE TABLE " + USER +
                    " (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " user_id INTEGER NOT NULL," +
                    " username TEXT NOT NULL," +
                    " user_token TEXT NOT NULL," +
                    " native_language TEXT NOT NULL," +
                    " gender TEXT NOT NULL," +
                    " has_image TEXT NOT NULL," +
                    " status TEXT NOT NULL," +
                    " phone_number TEXT NOT NULL," +
                    " date_of_birth TEXT NOT NULL);" ;

    static final String CHAT = "chat_data";
    static final String CREATE_CHAT_TABLE =
            " CREATE TABLE " + CHAT +
                    " (message_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " sender_id INTEGER NOT NULL," +
                    " sender_name TEXT NOT NULL," +
                    " sender_has_image TEXT NOT NULL," +
                    " receiver_id INTEGER NOT NULL," +
                    " receiver_name TEXT NOT NULL," +
                    " receiver_has_image TEXT NOT NULL," +
                    " message_body TEXT NOT NULL," +
                    " msg_lang_code TEXT NOT NULL," +
                    " time TEXT NOT NULL," +
                    " date TEXT NOT NULL," +
                    " m_sent TEXT NOT NULL," +
                    " m_received TEXT NOT NULL);" ;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_CHAT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER);
        db.execSQL("DROP TABLE IF EXISTS "+ CHAT);
        onCreate(db);
    }
}
