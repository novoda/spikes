package com.novoda.frankboylan.meetingseating.settings;

import android.content.Context;
import android.content.res.AssetManager;

import com.novoda.frankboylan.meetingseating.SQLiteDataManagement.SQLiteDelete;
import com.novoda.frankboylan.meetingseating.SQLiteDataManagement.SQLiteInsert;
import com.novoda.frankboylan.meetingseating.SQLiteDataManagement.SQLiteRead;
import com.novoda.frankboylan.meetingseating.SQLiteDataManagement.SQLiteUpdate;
import com.novoda.frankboylan.meetingseating.seats.model.RoomDatabaseWriter;

public final class SettingsModelFactory {
    public static SettingsModel build(Context context) {
        SQLiteDelete sqliteDelete = new SQLiteDelete(context);
        SQLiteInsert sqliteInsert = new SQLiteInsert(context);
        SQLiteUpdate sqliteUpdate = new SQLiteUpdate(context);
        SQLiteRead sqliteRead = new SQLiteRead(context);
        AssetManager assetManager = context.getAssets();
        RoomDatabaseWriter roomDatabaseWriter = new RoomDatabaseWriter(sqliteDelete, sqliteUpdate, sqliteInsert, sqliteRead);

        return new SettingsModelImpl(assetManager, roomDatabaseWriter);
    }
}
