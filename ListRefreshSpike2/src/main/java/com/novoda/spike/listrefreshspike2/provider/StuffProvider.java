package com.novoda.spike.listrefreshspike2.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.SystemClock;

/**
 * Created by Paul on 02/10/2013.
 */
public class StuffProvider extends ContentProvider {

    private static final String SQL_CREATE_STUFF = "CREATE TABLE stuff (" +
            " _id INTEGER PRIMARY KEY, " +
            " STUFF TEXT)";
    private static final String DBNAME = "mydb";
    private StuffDatabaseHelper openHelper;
    private SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        openHelper = new StuffDatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return openHelper.getReadableDatabase().query(
                "stuff", new String[]{"_id", "STUFF"}, null, null, null, null, null);
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        db = openHelper.getWritableDatabase();
        db.beginTransaction();
        db.delete("stuff", null, null);
        for (int n = 0; n < 20; n++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("_id", n);
            contentValues.put("STUFF", SystemClock.elapsedRealtime() + "");
            db.insert("stuff", null, contentValues);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    protected static final class StuffDatabaseHelper extends SQLiteOpenHelper {

        public StuffDatabaseHelper(Context context) {
            super(context, DBNAME, null, 1);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_STUFF);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
