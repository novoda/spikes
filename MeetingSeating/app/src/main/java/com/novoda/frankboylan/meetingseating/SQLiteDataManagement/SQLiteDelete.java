package com.novoda.frankboylan.meetingseating.SQLiteDataManagement;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class SQLiteDelete extends SQLiteCreate {

    public SQLiteDelete(Context context) {
        super(context);
    }

    /**
     * Deletes both ROOM_TABLE & SEAT_TABLE
     */
    public void clearRoomSeatData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + ROOM_TABLE);
        db.execSQL("DELETE FROM " + SEAT_TABLE);
        db.execSQL("DELETE FROM " + META_TABLE);
        db.close();
    }

    /**
     * Deletes SEAT_CACHE_TABLE
     */
    public void clearSeatCache() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + SQLiteCreate.SEAT_CACHE_TABLE);
        db.close();
    }

}
