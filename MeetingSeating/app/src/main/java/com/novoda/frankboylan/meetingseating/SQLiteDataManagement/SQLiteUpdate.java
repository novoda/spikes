package com.novoda.frankboylan.meetingseating.SQLiteDataManagement;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import static com.novoda.frankboylan.meetingseating.SQLiteDataManagement.SQLiteCreate.*;

public class SQLiteUpdate {
    private SQLiteCreate database;

    public SQLiteUpdate(Context context) {
        database = new SQLiteCreate(context);
    }

    /**
     * Updates the Timestamp in META_TABLE
     */
    public void updateMetaTimestamp(Long timestamp) {
        SQLiteDatabase db = database.getWritableDatabase();
        db.execSQL("REPLACE INTO " + META_TABLE + " (" + META_TIMESTAMP +
                           ") VALUES (" + timestamp + ");");
        db.close();
    }

    public void setMetaCacheToActive() {
        SQLiteDatabase db = database.getWritableDatabase();
        db.execSQL("DELETE FROM " + META_CACHE_TABLE + ";");
        db.execSQL("INSERT INTO " + META_CACHE_TABLE + " (" +
                           META_CACHE_TABLE_EXISTS + ") " +
                           " VALUES (1)");
        db.close();
    }

    public void setMetaCacheToInactive() {
        SQLiteDatabase db = database.getWritableDatabase();
        db.execSQL("DELETE FROM " + META_CACHE_TABLE + ";");
        db.execSQL("INSERT INTO " + META_CACHE_TABLE + " (" +
                           META_CACHE_TABLE_EXISTS + ") " +
                           " VALUES (0)");
        db.close();
    }
}
