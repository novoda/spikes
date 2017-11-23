package com.novoda.frankboylan.meetingseating.SQLiteDataManagement;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.util.Log;

import com.novoda.frankboylan.meetingseating.Timestamp;
import com.novoda.frankboylan.meetingseating.rooms.Room;
import com.novoda.frankboylan.meetingseating.seats.Seat;

import java.util.ArrayList;
import java.util.List;

public class SQLiteRead {
    private SQLiteCreate database;
    private static final String TAG = "SQLiteInsert";

    public SQLiteRead(Context context) {
        database = new SQLiteCreate(context);
    }

    /**
     * Returns last updates timestamp
     */
    public Timestamp getMetaTimestamp() {
        Long timestamp;
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + SQLiteCreate.META_TIMESTAMP + " FROM " + SQLiteCreate.META_TABLE, null);
        if (cursor.moveToFirst()) {
            timestamp = cursor.getLong(0);
            return new Timestamp(timestamp);
        }
        cursor.close();
        db.close();
        return Timestamp.INVALID_TIMESTAMP;
    }

    public List<Room> getAllRooms() {
        List<Room> roomList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + SQLiteCreate.ROOM_TABLE;

        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Room room = new Room();
                room.setRoomId(cursor.getInt(0));
                room.setRoomName(cursor.getString(1));
                room.setLocation(cursor.getString(2));
                room.setUnitName(cursor.getString(3));
                room.setSeats(getSeatsWithMatchingId(cursor.getInt(0)));

                roomList.add(room);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return roomList;
    }

    private List<Seat> getSeatsWithMatchingId(int roomId) {
        List<Seat> seatList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + SQLiteCreate.SEAT_TABLE +
                " WHERE " + SQLiteCreate.SEAT_ROOM_ID + " = " + roomId;

        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Seat seat = new Seat(); // Optimise this cursor.
                seat.setSeatId(cursor.getInt(0));
                seat.setValue(cursor.getInt(1));
                seat.setUnitType(cursor.getString(2));
                seat.setRoomId(cursor.getInt(3));

                seatList.add(seat);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return seatList;
    }

    /**
     * Returns List of all Seat Objects in SEAT_TABLE
     */
    public List<Seat> getAllSeats() {
        List<Seat> seatList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + SQLiteCreate.SEAT_TABLE;

        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Seat seat = new Seat(); // Optimise this cursor.
                seat.setSeatId(cursor.getInt(0));
                seat.setValue(cursor.getInt(1));
                seat.setUnitType(cursor.getString(2));
                seat.setRoomId(cursor.getInt(3));

                seatList.add(seat);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return seatList;
    }

    /**
     * Returns list of All Seat objects from SEAT_CACHE_TABLE
     */
    public List<Seat> getCachedList() {
        List<Seat> cachedSeatList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + SQLiteCreate.SEAT_CACHE_TABLE;

        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Seat seat = new Seat(); // Optimise this cursor.
                seat.setSeatId(cursor.getInt(0));
                seat.setValue(cursor.getInt(1));
                seat.setUnitType(cursor.getString(2));
                seat.setRoomId(cursor.getInt(3));

                cachedSeatList.add(seat);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return cachedSeatList;
    }

    /**
     * Prints currents data from DB. (Seat & Room tables only)
     */
    public void debugLog() {
        List<Seat> seatList = getAllSeats();
        for (int i = 0; i < seatList.size(); i++) {
            Log.d("TABLE_SEAT", seatList.get(i).toString());
        }
        List<Room> roomList = getAllRooms();
        for (int j = 0; j < roomList.size(); j++) {
            Log.d("TABLE_ROOM", roomList.get(j).toString());
        }
    }

    public String getMetaCacheStatus() {
        SQLiteDatabase db = database.getReadableDatabase();
        try {
            return DatabaseUtils.stringForQuery(db, "SELECT * FROM " + SQLiteCreate.META_CACHE_TABLE, null);
        } catch (SQLiteDoneException d) {
            return "";
        }
    }
}
