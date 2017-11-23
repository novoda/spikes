package com.novoda.frankboylan.meetingseating.SQLiteDataManagement;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteCreate extends SQLiteOpenHelper {
    private static int DATABASE_VERSION = 1;

    // Database Strings
    private static final String DATABASE_NAME = "meeting_seating_db";

    static final String ROOM_TABLE = "rooms";
    static final String ROOM_ID = "room_id";
    static final String ROOM_NAME = "room_name";
    static final String ROOM_LOCATIONNAME = "room_locationname";
    static final String ROOM_UNITNAME = "room_unitname";

    static final String SEAT_TABLE = "seats";
    static final String SEAT_ID = "seat_id";
    static final String SEAT_VALUE = "seat_value";
    static final String SEAT_UNITTYPE = "seat_unittype";
    static final String SEAT_ROOM_ID = "seat_roomid";

    static final String SEAT_CACHE_TABLE = "seats_cache";
    static final String SEAT_CACHE_ID = "seats_cache_id";
    static final String SEAT_CACHE_VALUE = "seats_cache_value";
    static final String SEAT_CACHE_UNITTYPE = "seats_cache_unittype";
    static final String SEAT_CACHE_ROOM_ID = "seats_cache_room_id";

    static final String META_TABLE = "metadata";
    static final String META_TIMESTAMP = "meta_timestamp";

    static final String META_CACHE_TABLE = "metadata_cache";
    static final String META_CACHE_TABLE_EXISTS = "metacache_exists";

    SQLiteCreate(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_ROOM = "CREATE TABLE " + ROOM_TABLE + "(" +
                ROOM_ID + " INTEGER PRIMARY KEY, " +
                ROOM_NAME + " VARCHAR(50), " +
                ROOM_LOCATIONNAME + " VARCHAR(50), " +
                ROOM_UNITNAME + " VARCHAR(50)" +
                ");";
        db.execSQL(CREATE_TABLE_ROOM);

        String CREATE_TABLE_SEAT = "CREATE TABLE " + SEAT_TABLE + "(" +
                SEAT_ID + " INTEGER, " +
                SEAT_VALUE + " DECIMAL(5,2), " +
                SEAT_UNITTYPE + " VARCHAR(5), " +
                SEAT_ROOM_ID + " INTEGER, " +
                " FOREIGN KEY (" + SEAT_ROOM_ID + ") REFERENCES " + ROOM_TABLE + "(" + ROOM_ID + ")" +
                "PRIMARY KEY (" + SEAT_ID + ", " + SEAT_ROOM_ID + ")" +
                ");";
        db.execSQL(CREATE_TABLE_SEAT);

        String CREATE_TABLE_SEAT_CACHE = "CREATE TABLE " + SEAT_CACHE_TABLE + "(" +
                SEAT_CACHE_ID + " INTEGER, " +
                SEAT_CACHE_VALUE + " DECIMAL(5,2), " +
                SEAT_CACHE_UNITTYPE + " VARCHAR(5), " +
                SEAT_CACHE_ROOM_ID + " INTEGER, " +
                " FOREIGN KEY (" + SEAT_CACHE_ROOM_ID + ") REFERENCES " + SEAT_TABLE + "(" + SEAT_ROOM_ID + ")" +
                "PRIMARY KEY (" + SEAT_CACHE_ID + ", " + SEAT_CACHE_ROOM_ID + ")" +
                ");";
        db.execSQL(CREATE_TABLE_SEAT_CACHE);

        String CREATE_META_TABLE = "CREATE TABLE " + META_TABLE + "(" +
                META_TIMESTAMP + " INTEGER PRIMARY KEY" +
                ");";
        db.execSQL(CREATE_META_TABLE);

        String CREATE_META_CACHE_TABLE = "CREATE TABLE " + META_CACHE_TABLE + "(" +
                META_CACHE_TABLE_EXISTS + " INTEGER DEFAULT 0" +
                ");";
        db.execSQL(CREATE_META_CACHE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ROOM_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SEAT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + META_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SEAT_CACHE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + META_CACHE_TABLE);
        onCreate(db);
    }
}
