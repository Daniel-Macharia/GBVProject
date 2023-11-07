package com.example.frats;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;

public class NewMessageCounter {

    private static final String chatKey = "chatKey";
    private static final String number = "numberOfNewMessages";
    private static final String chatNotificationID = "chatNewMessageNotificationId";

    private static final String tableName = "newMessageCountTable";

    private static final String dbName = "NewMessageCountDB";

    private static final int version = 1;

    private final Context thisContext;
    private DbHelper newMessageCountHelper;
    SQLiteDatabase newMessageCountDB;

    private static class DbHelper extends SQLiteOpenHelper
    {
        public DbHelper(Context context)
        {
            super(context, dbName, null, version);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(" CREATE TABLE " + tableName + " ( " +
                    chatKey + " TEXT NOT NULL PRIMARY KEY, " +
                    number + " INTEGER NOT NULL ); ");

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            //do nothing yet
        }
    }

    public NewMessageCounter(Context context)
    {
        thisContext = context;
    }

    public NewMessageCounter open() throws SQLException
    {
        newMessageCountHelper = new DbHelper(thisContext);
        newMessageCountDB = newMessageCountHelper.getWritableDatabase();
        return this;
    }

    public void close()
    {
        newMessageCountHelper.close();
    }

    public long addChat(String chatID)
    {
        ContentValues cv = new ContentValues();
        cv.put(chatKey,chatID);
        cv.put(number, 0);
        Toast.makeText(thisContext, "adding chat " + chatID, Toast.LENGTH_SHORT).show();
        return newMessageCountDB.insert(tableName, null, cv);
    }

    public void setCount( String chatID, int count)
    {
        try{
            ContentValues cv = new ContentValues();
            cv.put(number, count);

            String whereClause = chatKey + " LIKE ('%" + chatID + "%')";

            newMessageCountDB.update(tableName, cv, whereClause, null);
           // Toast.makeText(thisContext, "Setting Count of " + chatID + " to " + count, Toast.LENGTH_SHORT).show();
        }catch ( Exception e )
        {
            Toast.makeText(thisContext, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public int getCount( String chatID )
    {
        int count = -1; //return -1 if chat does not exist

        try {

            String sql = " SELECT " + number +
                    " FROM " + tableName +
                    " WHERE " + chatKey + " LIKE ('%" + chatID + "%');";
            Cursor c = newMessageCountDB.rawQuery(sql, null);

            int countIndex = c.getColumnIndex(number);

            if( c.getCount() > 0)
            {
                c.moveToFirst();
                count = c.getInt( countIndex );
            }

            c.close();

        }catch ( Exception e )
        {
            Toast.makeText(thisContext, e.toString(), Toast.LENGTH_SHORT).show();
        }

        return count;
    }

    public int getTotalNewUserMessage()
    {
        int total = 0;

        try{
            ArrayList<String[]> data = new ArrayList<>(10);
            user u = new user(thisContext);
            u.open();
            data = u.getUsers();
            u.close();

            for( String[] userData : data )
            {
                int count = getCount( userData[1] );
                total += (( count == -1) ? 0 : count );
            }

        }catch( Exception e )
        {
            Toast.makeText(thisContext, "Error getting total: " + e.toString(), Toast.LENGTH_SHORT).show();
        }
        return total;
    }

}
