package com.example.frats;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class journalEntry {

    public static final String date = "entryDate";
    public static final String time = "entryTime";
    public static final String entry = "journalEntry";
    public static final String entryNumber = "entryNumber";


    private static final String dbName = "userJournaldb";
    private static final String tableName = "journal";
    private static final int dbversion = 1;

    private DBHelper entryHelper;
    private final Context thisContext;
    private SQLiteDatabase journaldb;

    private class DBHelper extends SQLiteOpenHelper
    {
        public DBHelper(Context context) {
            super(context, dbName, null, dbversion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(
                    "CREATE TABLE " + tableName + " ( " +
                            entryNumber + " INTEGER PRIMARY KEY  AUTOINCREMENT, " +
                            date + " TEXT NOT NULL," +
                            time + " TEXT NOT NULL," +
                            entry + " TEXT NOT NULL ); "
            );
            //
            //entryNumber + "INTEGER NOT NULL  PRIMARY KEY AUTOINCREMENT, " +

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {

            db.execSQL( "DROP TABLE IF EXISTS " + tableName + ";");

            onCreate(db);

        }

    }

    public journalEntry(Context c)
    {
        thisContext = c;
    }

    public journalEntry open() throws SQLException
    {
        entryHelper = new DBHelper(thisContext);
        journaldb = entryHelper.getWritableDatabase();

        return this;
    }

    public void close()
    {
        entryHelper.close();
    }

    public long makeEntry(String dateOfEntry, String timeOfEntry, String entryToMake)
    {
        ContentValues c = new ContentValues();
        c.put(date, dateOfEntry);
        c.put(time, timeOfEntry);
        c.put(entry,entryToMake);

        return journaldb.insert( tableName, null, c);
    }

    public void updateEntry( String dateOfUpdate, String timeOfUpdate, String newEntry, String oldDate, String oldTime, String oldEntry)
    {
        try{
            String getEntryId = " SELECT " + entryNumber + " FROM " + tableName +
                    " WHERE " + date + " = " + oldDate + " AND " +
                    time + " = " + oldTime + " AND " +
                    entry + " = " + oldEntry + ";";

            Cursor c = journaldb.rawQuery( getEntryId, null );

            int IdIndex = c.getColumnIndex(entryNumber);
            int thisEntryNumber = c.getInt( IdIndex );

            String update = dateOfUpdate + " / " + timeOfUpdate +"\n\n\n" + newEntry;
            String sqlUpdateQuery = " UPDATE TABLE " + tableName +
                    " SET " + entry + " = " + update +
                    " WHERE " + entryNumber + " = " + thisEntryNumber + " ; ";
        }catch( Exception e  )
        {
            Toast.makeText(thisContext, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public ArrayList<String[]> getEntries()
    {
        String columns[] = new String[]{date,time,entry};
        Cursor c = journaldb.query(tableName, columns, null, null, null, null, null);

        int dateIndex = c.getColumnIndex(date);
        int timeIndex = c.getColumnIndex(time);
        int entryIndex = c.getColumnIndex(entry);

        ArrayList<String[]> result = new ArrayList<String[]>(10);
        for( c.moveToFirst(); !c.isAfterLast(); c.moveToNext() )
        {
            result.add(new String[]{ c.getString(dateIndex), c.getString(timeIndex), c.getString(entryIndex)});

        }

        return result;

    }

}
