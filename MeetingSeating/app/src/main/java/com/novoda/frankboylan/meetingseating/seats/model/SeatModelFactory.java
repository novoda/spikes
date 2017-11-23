package com.novoda.frankboylan.meetingseating.seats.model;

import android.content.Context;

import com.novoda.frankboylan.meetingseating.SQLiteDataManagement.SQLiteDelete;
import com.novoda.frankboylan.meetingseating.SQLiteDataManagement.SQLiteInsert;
import com.novoda.frankboylan.meetingseating.SQLiteDataManagement.SQLiteRead;
import com.novoda.frankboylan.meetingseating.SQLiteDataManagement.SQLiteUpdate;

public final class SeatModelFactory {
    public static SeatModel build(Context context) {
        SQLiteDelete sqliteDelete = new SQLiteDelete(context);
        SQLiteInsert sqliteInsert = new SQLiteInsert(context);
        SQLiteRead sqliteRead = new SQLiteRead(context);
        SQLiteUpdate sqliteUpdate = new SQLiteUpdate(context);
        RoomDatabaseWriter roomDatabaseWriter = new RoomDatabaseWriter(sqliteDelete, sqliteUpdate, sqliteInsert, sqliteRead);

        return new SeatModelImpl(sqliteRead, sqliteDelete, sqliteInsert, sqliteUpdate, roomDatabaseWriter);
    }

    private SeatModelFactory() {
    }
}
