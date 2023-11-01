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
    public static final String lastEdited = "lastEditedOn";


    private static final String dbName = "userJournaldb";
    private static final String tableName = "journal";
    private static final int dbversion = 2;

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
                            entry + " TEXT NOT NULL," +
                            lastEdited + " TEXT ); "
            );
            //
            //entryNumber + "INTEGER NOT NULL  PRIMARY KEY AUTOINCREMENT, " +

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {

           try{
                db.execSQL( "ALTER TABLE " + tableName + " ADD  " + lastEdited +
                        " TEXT ;" );
            }catch( Exception e )
            {
                Toast.makeText(thisContext, e.toString(), Toast.LENGTH_SHORT).show();
            }

            //onCreate(db);

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

    public long makeEntry(String dateOfEntry, String timeOfEntry, String entryToMake, String editDate)
    {
        ContentValues c = new ContentValues();
        c.put(date, dateOfEntry);
        c.put(time, timeOfEntry);
        c.put(entry,entryToMake);
        c.put(lastEdited, editDate);

        return journaldb.insert( tableName, null, c);
    }

    public void updateEntry( int EntryId, String dateOfUpdate, String timeOfUpdate, String newEntry, String oldDate, String oldTime, String oldEntry)
    {
        try{
            /* String getEntryId = " SELECT * FROM " + tableName +
                    " WHERE " + date + " LIKE ('%" + oldDate + "%') AND " +
                    time + " LIKE ('%" + oldTime + "%');" ; */

            String getEntryId = " SELECT * FROM " + tableName +
                    " WHERE " + entryNumber + " = " + EntryId + ";" ;

            Cursor c = journaldb.rawQuery( getEntryId, null );

            int idIndex = c.getColumnIndex(entryNumber);
            String thisEntryNumber = "";
            if( c.getCount() > 0)
            {
                c.moveToFirst();
                thisEntryNumber = String.valueOf( c.getInt( idIndex ) );
                String editDate = "Last Edited on: " + dateOfUpdate + " / " + timeOfUpdate + "\n";
               // String sqlUpdateQuery = "UPDATE " + tableName +
                 //       " SET " + entry + " = '" + update +
                //        "' WHERE " + entryNumber + " = " + thisEntryNumber + " ; ";
               // journaldb.execSQL(sqlUpdateQuery);
                ContentValues cv = new ContentValues();
                cv.put(entry, newEntry);
                cv.put(lastEdited, editDate);
                journaldb.update(tableName,cv, entryNumber + " = " + thisEntryNumber, null);
            }

            Toast.makeText(thisContext, "ID to this entry is " + thisEntryNumber, Toast.LENGTH_SHORT).show();



        }catch( Exception e  )
        {
            Toast.makeText(thisContext, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public ArrayList<String[]> getEntries()
    {
        ArrayList<String[]> result = new ArrayList<String[]>(10);
        try{
            String columns[] = new String[]{date,time,entry,entryNumber, lastEdited};

            Cursor c = journaldb.query(tableName, columns, null, null, null, null, null);

            int idIndex = c.getColumnIndex(entryNumber);
            int dateIndex = c.getColumnIndex(date);
            int timeIndex = c.getColumnIndex(time);
            int entryIndex = c.getColumnIndex(entry);
            int editIndex = c.getColumnIndex(lastEdited);


            String edited;
            for( c.moveToFirst(); !c.isAfterLast(); c.moveToNext() )
            {
                edited = c.getString(editIndex);
                result.add(new String[]{ c.getString(dateIndex), c.getString(timeIndex),
                        c.getString(entryIndex), c.getString(idIndex),
                        edited == null ? "" : new String(edited) });

            }
        }catch( Exception e )
        {
            Toast.makeText( thisContext, e.toString(), Toast.LENGTH_SHORT).show();
        }

        return result;

    }

}
