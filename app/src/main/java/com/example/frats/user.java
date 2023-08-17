package com.example.frats;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class user {


    public static final String userName = "user_name";
    public static final String passWord = "user_password";


    private static final String dbName = "userAccountdb";
    private static final String tableName = "account";
    private static final int dbversion = 1;

    private DBHelper helper;
    private final Context thisContext;
    private SQLiteDatabase userdb;

    private static class DBHelper extends SQLiteOpenHelper
    {
        public DBHelper(Context context) {
            super(context, dbName, null, dbversion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL( "CREATE TABLE " + tableName + " ( " +
                    userName + " TEXT NOT NULL PRIMARY KEY , " +
                    passWord + " TEXT NOT NULL ); "

            );

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {

            db.execSQL(
                    "DROP TABLE IF EXISTS " + tableName + " ; "
            );

            onCreate(db);
        }
    }

    public user(Context c)
    {
        thisContext = c;
    }

    public user open() throws SQLException
    {
        helper = new DBHelper(thisContext);
        userdb = helper.getWritableDatabase();
        return this;
    }

    public void close()
    {
        helper.close();
    }

    public long createUser(String name, String password)
    {
        ContentValues cv = new ContentValues();
        cv.put(userName,name);
        cv.put(passWord,password);
        return userdb.insert(tableName,null,cv);
    }
    public String[] readData()
    {
        String userInfo[] = new String[2];
        String []columns = new String[]{userName,passWord};

        Cursor c = userdb.query(tableName,columns,null,null,null,null,null);

        int nameIndex = c.getColumnIndex(userName);
        int passwordIndex = c.getColumnIndex(passWord);

        c.moveToFirst();

        userInfo[0] = c.getString(nameIndex);
        userInfo[1] = c.getString(passwordIndex);

        return userInfo;
    }

}
