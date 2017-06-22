package com.pip.talk.pip.pc.piptalk.User_Model;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ContactsDatabase extends SQLiteOpenHelper{
    private String KEY_ID="ID";
    private String KEY_USER_1="USER_1";
    private String KEY_USER_2="USER_2";
    private String TABLE_NAME="MAIN_CHAT";
    private static String DB_NAME="chat";
    private static int DB_VERSION=1;
    private SQLiteDatabase dp;

    public ContactsDatabase(Context context) {
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE " +TABLE_NAME+ " ( "+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT , "+KEY_USER_1+" TEXT ,"+
                KEY_USER_2+" TEXT );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
