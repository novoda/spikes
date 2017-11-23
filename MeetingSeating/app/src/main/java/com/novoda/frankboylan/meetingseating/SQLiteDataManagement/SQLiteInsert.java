package com.novoda.frankboylan.meetingseating.SQLiteDataManagement;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.novoda.frankboylan.meetingseating.rooms.Room;
import com.novoda.frankboylan.meetingseating.seats.Seat;

import static com.novoda.frankboylan.meetingseating.SQLiteDataManagement.SQLiteCreate.*;

public class SQLiteInsert {
    private SQLiteCreate database;

    public SQLiteInsert(Context context) {
        database = new SQLiteCreate(context);
    }

    /**
     * Insert a room row into the database, for back-end use only.
     */
    public void addRoom(Room room) {
        SQLiteDatabase db = database.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ROOM_ID, room.getRoomId());
        values.put(ROOM_NAME, room.getRoomName());
        values.put(ROOM_LOCATIONNAME, room.getLocation());
        values.put(ROOM_UNITNAME, room.getUnitName());
        for (Seat seat : room.getSeats()) {
            addSeat(seat);
        }

        db.insert(ROOM_TABLE, null, values);
        db.close();
    }

    /**
     * Insert a new seat row into the database, for back-end use only.
     */
    private void addSeat(Seat seat) {
        SQLiteDatabase db = database.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SEAT_ID, seat.getSeatId());
        values.put(SEAT_VALUE, seat.getValue());
        values.put(SEAT_UNITTYPE, seat.getUnitType());
        values.put(SEAT_ROOM_ID, seat.getRoomId());

        db.insert(SEAT_TABLE, null, values);
    }

    /**
     * Insert a seat object into the cache, used for storing Filtered Lists
     */
    public void addSeatToCache(Seat seat) {
        SQLiteDatabase db = database.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SEAT_CACHE_ID, seat.getSeatId());
        values.put(SEAT_CACHE_VALUE, seat.getValue());
        values.put(SEAT_CACHE_UNITTYPE, seat.getUnitType());
        values.put(SEAT_CACHE_ROOM_ID, seat.getRoomId());

        db.insert(SEAT_CACHE_TABLE, null, values);

        db.close();
    }
}
