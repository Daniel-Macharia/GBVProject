package com.example.frats;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;

public class user {


    public static final String userName = "user_name";
    public static final String passWord = "user_password";
    public static final String phoneNumber = "phone_number";
    public static final String userOrAssistant = "user_or_assistant";
    private static final String email = "email_address";
    private static final String thisUser = "this_user";

    private static final String userID = "user_id";


    private static final String dbName = "userAccountdb";
    private static final String tableName = "account";
    private static final int dbversion = 3;

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
                    userID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    userName + " TEXT NOT NULL , " +
                    passWord + " TEXT NOT NULL, " +
                    phoneNumber + " TEXT NOT NULL, " +
                    userOrAssistant + " TEXT NOT NULL, " +
                    email + " TEXT NOT NULL, " +
                    thisUser + " INTEGER NOT NULL );"

            );

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {

            db.execSQL(
                    " DROP TABLE IF EXISTS " + tableName + " ; "
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

    public long createUser(String name, String password, String phone, String emailAddress, String user_or_assistant, int isThisUser )
    {
        ContentValues cv = new ContentValues();
        cv.put(userName,name);
        cv.put(passWord,password);
        cv.put(phoneNumber, phone);

        //if( user_or_assistant == null)
         //   user_or_assistant = "users";
        cv.put(userOrAssistant, user_or_assistant);
        cv.put(email, emailAddress);
        cv.put( thisUser, isThisUser);
        long l = userdb.insert(tableName,null,cv);

       // Toast.makeText(thisContext,"User or Assistant: " + cv.get(userOrAssistant), Toast.LENGTH_SHORT).show();

        return l;
    }

    public ArrayList<String[]> getUsers()
    {
        String query = " SELECT * FROM " + tableName +
                " WHERE " + thisUser + " = 0 ; ";
        Cursor c = userdb.rawQuery( query, null );

        int nameIndex = c.getColumnIndex( userName);
        int phoneIndex = c.getColumnIndex( phoneNumber );
        //int userIds = c.getColumnIndex( userID );
        int imageURIIndex = c.getColumnIndex( email );

        ArrayList<String[]> result = new ArrayList<>(10);
        //String ids = "";

        for( c.moveToFirst(); !c.isAfterLast(); c.moveToNext() )
        {
            result.add( new String[]{ new String( c.getString(nameIndex)),
                    new String(c.getString(phoneIndex))} );
            //ids += "\n" + c.getInt( userIds );
            //Toast.makeText( thisContext, "read user: " + c.getString( nameIndex ) +
             //       " ~ " + c.getString( phoneIndex ), Toast.LENGTH_SHORT ).show();
        }

        //Toast.makeText(thisContext, "IDs are :\n" + ids, Toast.LENGTH_SHORT).show();
        return result;
    }

    public void updateUserName(String name)
    {
        try{
            ContentValues cv = new ContentValues();
            String whereClause = thisUser + " = " + 1;

            cv.put(userName, name);

            userdb.update( tableName, cv, whereClause, null);
        }catch( Exception e )
        {
            Toast.makeText( thisContext, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
    public void updateUserPhone(String contact)
    {
       try{
           ContentValues cv = new ContentValues();
           String whereClause = thisUser + " = " + 1;

           cv.put(phoneNumber, contact);

           userdb.update( tableName, cv, whereClause, null);
       }catch( Exception e )
       {
           Toast.makeText(thisContext, e.toString(), Toast.LENGTH_SHORT).show();
       }
    }
    public void updateUserPassword(String pass)
    {
       try{
           ContentValues cv = new ContentValues();
           String whereClause = thisUser + " = " + 1;

           cv.put(passWord, pass);

           userdb.update( tableName, cv, whereClause, null);
       }catch( Exception e )
       {
           Toast.makeText( thisContext, e.toString(), Toast.LENGTH_SHORT).show();
       }
    }

    public String[] readData()
    {
        String userInfo[] = new String[4];
        String query = " SELECT * FROM " + tableName +
                " WHERE " + thisUser + " = 1 ; ";

        Cursor c = userdb.rawQuery(query, null);
        int nameIndex = c.getColumnIndex(userName);
        int passwordIndex = c.getColumnIndex(passWord);
        int phoneIndex = c.getColumnIndex(phoneNumber);
        int userAssistantIndex = c.getColumnIndex(userOrAssistant);

        if( c.getCount() > 0 )
        {
           // Toast.makeText(thisContext, "Cursor has values", Toast.LENGTH_SHORT).show();
            c.moveToFirst();

            userInfo[0] = c.getString(nameIndex);
            userInfo[1] = c.getString(passwordIndex);
            userInfo[2] = c.getString(phoneIndex);
            userInfo[3] = c.getString(userAssistantIndex);

            return new String[]{userInfo[0],userInfo[1],userInfo[2],userInfo[3]} ;
        }
        else {
           // Toast.makeText(thisContext, "Cursor has no values", Toast.LENGTH_SHORT).show();

            return new String[]{ new String(""), new String(""), new String(""), new String("")};
        }
        //Toast.makeText(thisContext,"Phone: " + c.getString(phoneIndex), Toast.LENGTH_SHORT).show();

    }

}
