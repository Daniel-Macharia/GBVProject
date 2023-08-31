package com.example.frats;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class user {


    public static final String userName = "user_name";
    public static final String passWord = "user_password";
    public static final String phoneNumber = "phone_number";
    public static final String userOrAssistant = "user_or_assistant";


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
                    userName + " TEXT NOT NULL PRIMARY KEY, " +
                    passWord + " TEXT NOT NULL, " +
                    phoneNumber + " TEXT NOT NULL, " +
                    userOrAssistant + " TEXT NOT NULL );"

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

    public long createUser(String name, String password, String phone, String user_or_assistant )
    {
        ContentValues cv = new ContentValues();
        cv.put(userName,name);
        cv.put(passWord,password);
        cv.put(phoneNumber, phone);

        //if( user_or_assistant == null)
         //   user_or_assistant = "users";
        cv.put(userOrAssistant, user_or_assistant);
        long l = userdb.insert(tableName,null,cv);

       // Toast.makeText(thisContext,"User or Assistant: " + cv.get(userOrAssistant), Toast.LENGTH_SHORT).show();

        return l;
    }
    public String[] readData()
    {
        String userInfo[] = new String[4];
        String []columns = new String[]{userName,passWord,phoneNumber,userOrAssistant};

        Cursor c = userdb.query(tableName,columns,null,null,null,null,null);

        int nameIndex = c.getColumnIndex(userName);
        int passwordIndex = c.getColumnIndex(passWord);
        int phoneIndex = c.getColumnIndex(phoneNumber);
        int userAssistantIndex = c.getColumnIndex(userOrAssistant);

        c.moveToFirst();

        userInfo[0] = c.getString(nameIndex);
        userInfo[1] = c.getString(passwordIndex);
        userInfo[2] = c.getString(phoneIndex);
        userInfo[3] = c.getString(userAssistantIndex);
        //Toast.makeText(thisContext,"Phone: " + c.getString(phoneIndex), Toast.LENGTH_SHORT).show();


        return new String[]{userInfo[0],userInfo[1],userInfo[2],userInfo[3]} ;
    }

}
