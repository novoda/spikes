package com.novoda.frankboylan.meetingseating.rooms.heatmap.adv.repo.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "room_data")
public class AdvHeatmapRoom {

    @PrimaryKey
    @ColumnInfo(name = "room_id")
    private int roomId;

    @ColumnInfo(name = "location")
    private String location;

    @ColumnInfo(name = "unit_name")
    private String unitName;

    @ColumnInfo(name = "room_name")
    private String roomName;

    @ColumnInfo(name = "size_x")
    private int sizeX;

    @ColumnInfo(name = "size_y")
    private int sizeY;

    int getRoomId() {
        return roomId;
    }

    void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    String getLocation() {
        return location;
    }

    void setLocation(String location) {
        this.location = location;
    }

    String getUnitName() {
        return unitName;
    }

    void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    String getRoomName() {
        return roomName;
    }

    void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    int getSizeX() {
        return sizeX;
    }

    void setSizeX(int sizeX) {
        this.sizeX = sizeX;
    }

    int getSizeY() {
        return sizeY;
    }

    void setSizeY(int sizeY) {
        this.sizeY = sizeY;
    }
}
