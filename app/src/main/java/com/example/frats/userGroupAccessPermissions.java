package com.example.frats;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class userGroupAccessPermissions {

    private static final String groupKey = "group_key";
    private static final String isAllowedAccess = "access_rights";

    private static final String tableName = "group_access_permissions";

    private static final String dbName = "groupAccessPermissionsDB";

    private static final int dbVersion = 1;

    private ourDBHelper dbHelper;

    private SQLiteDatabase db;

    private Context thisContext;

    class ourDBHelper extends SQLiteOpenHelper
    {
        public ourDBHelper(Context context)
        {
            super(context, dbName, null, dbVersion );
        }

        @Override
        public void onCreate( SQLiteDatabase db)
        {
            db.execSQL(" CREATE TABLE " + tableName + " ( " +
                    groupKey + " TEXT NOT NULL PRIMARY KEY , " +
                    isAllowedAccess + " TEXT NOT NULL " +
                    " );" );
        }

        @Override
        public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion )
        {
            db.execSQL(" DROP TABLE IF EXISTS " + tableName + " ; " );

            onCreate(db);
        }

    }

    userGroupAccessPermissions( Context c )
    {
        thisContext = c;
    }

    public userGroupAccessPermissions open() throws SQLException
    {
        dbHelper = new ourDBHelper(thisContext);

        db = dbHelper.getWritableDatabase();

        return this;
    }

    public void close()
    {
        dbHelper.close();
    }

    public long insertPermission(String key, String allowedAccess )
    {
        ContentValues cv = new ContentValues();
        cv.put(groupKey, key);
        cv.put(isAllowedAccess, allowedAccess );

        return db.insert(tableName, null, cv);
    }

    public boolean isAllowedAccessTo(String key)
    {
        boolean access = false;
        String permission = "allowed";
        String[] columns = {groupKey, isAllowedAccess };
        Cursor c = db.query( tableName, columns, null, null, null, null, null);

        int groupKeyIndex = c.getColumnIndex(groupKey);
        int isAllowedAccessIndex = c.getColumnIndex(isAllowedAccess);

        for( c.moveToFirst(); !c.isAfterLast(); c.moveToNext() )
        {
            if( key.equals( c.getString(groupKeyIndex) ) )
            {
                if( permission.equals(c.getString( isAllowedAccessIndex )) )
                    access = true;
            }
        }

        return access;
    }

}
