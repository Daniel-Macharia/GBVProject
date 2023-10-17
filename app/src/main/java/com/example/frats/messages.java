package com.example.frats;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;

public class messages {

    private final String sender = "sender";
    private final String receiver = "receiver";
    private final String content = "content";
    private final String time = "time";
    private final String messageID = "messageID";
    private final String tableName = "message";

    private final String dbName = "messageDB";
    private final int dbVersion = 1;

    DBHelper ourHelper;
    Context thisContext;

    SQLiteDatabase msgDB;

    private class DBHelper extends SQLiteOpenHelper
    {
        DBHelper( Context c)
        {
            super(c, dbName, null, dbVersion );
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL( " CREATE TABLE " + tableName + " ( " +
                    messageID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , " +
                    sender + " TEXT NOT NULL, " +
                    receiver + " TEXT NOT NULL ," +
                    content + " TEXT ," +
                    time + " TEXT NOT NULL ); " );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion )
        {
            db.execSQL( " DROP TABLE IF EXISTS " + tableName + " ; " );
            onCreate(db);
        }
    }

    public messages(Context c)
    {
        thisContext = c;
    }

    public Context getThisContext()
    {
        return thisContext;
    }

    public messages open() throws SQLException
    {
        ourHelper = new DBHelper( getThisContext() );
        msgDB = ourHelper.getWritableDatabase();

        return this;

    }

    public void close()
    {
        ourHelper.close();
    }

    public void deleteMessage(chatMessage message)
    {

    }

    public long addNewMessage( String sender, String receiver, String content, String timeStamp )
    {
        ContentValues cv = new ContentValues();
        cv.put( this.sender, sender );
        cv.put( this.receiver, receiver );
        cv.put( this.content, content );
        cv.put( this.content, content );
        cv.put( this.time, timeStamp );

        //Toast.makeText(thisContext, "inserted " + sender + "\t" + receiver
           //     + content + "\n " + timeStamp, Toast.LENGTH_SHORT).show();

        return msgDB.insert( tableName, null, cv);

    }

    public ArrayList<msg> getMessagesSentTo(String recipient, String senderOfMessage )
    {
        String query = " SELECT * FROM " + tableName +
                " WHERE ( " + receiver + " LIKE ('%" + recipient + "%') " +
                " AND " + sender + " LIKE ('%" + senderOfMessage + "%') ) " +
                " OR ( " + receiver + " LIKE ('%" + senderOfMessage + "%') " +
                " AND " + sender + " LIKE ('%" + recipient + "%') ) ; ";
        Cursor c = msgDB.rawQuery(query, null);
        
        int receiverIndex = c.getColumnIndex( this.receiver );
        int senderIndex = c.getColumnIndex( this.sender );
        int contentIndex = c.getColumnIndex( this.content );
        int timeStampIndex = c.getColumnIndex( this.time );
        ArrayList<msg> result = new ArrayList<>(10);
        for( c.moveToFirst(); !c.isAfterLast(); c.moveToNext() )
        {
            result.add( new msg(
                    new String( c.getString(senderIndex)),
                    new String(c.getString( receiverIndex )),
                    new String(c.getString(contentIndex )),
                    new String(c.getString( timeStampIndex ))
            ) );
        }


        return result;
    }

    public ArrayList<msg> getMessagesSentTo(String recipient )
    {
        String query = " SELECT * FROM " + tableName +
                " WHERE " + receiver + " LIKE ('%" + recipient + "%');";
        Cursor c = msgDB.rawQuery(query, null);

        int receiverIndex = c.getColumnIndex( this.receiver );
        int senderIndex = c.getColumnIndex( this.sender );
        int contentIndex = c.getColumnIndex( this.content );
        int timeStampIndex = c.getColumnIndex( this.time );
        ArrayList<msg> result = new ArrayList<>(10);
        for( c.moveToFirst(); !c.isAfterLast(); c.moveToNext() )
        {
            result.add( new msg(
                    new String( c.getString(senderIndex)),
                    new String(c.getString( receiverIndex )),
                    new String(c.getString(contentIndex )),
                    new String(c.getString( timeStampIndex ))
            ) );
        }


        return result;
    }

}
