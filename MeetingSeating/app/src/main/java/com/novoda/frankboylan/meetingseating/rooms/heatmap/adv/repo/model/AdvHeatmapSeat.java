package com.novoda.frankboylan.meetingseating.rooms.heatmap.adv.repo.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "seat_data",
        primaryKeys = {"seat_id", "room_id"},
        foreignKeys = @ForeignKey(
                onDelete = CASCADE,
                entity = AdvHeatmapRoom.class,
                parentColumns = "room_id",
                childColumns = "room_id"))
public class AdvHeatmapSeat {
    @ColumnInfo(name = "seat_id")
    private int seatId;

    @ColumnInfo(name = "room_id")
    private int roomId;

    @ColumnInfo(name = "heat_value")
    private int heatValue;

    @ColumnInfo(name = "pos_x")
    private int posX;

    @ColumnInfo(name = "pos_y")
    private int posY;

    int getSeatId() {
        return seatId;
    }

    void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    int getRoomId() {
        return roomId;
    }

    void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    int getHeatValue() {
        return heatValue;
    }

    void setHeatValue(int heatValue) {
        this.heatValue = heatValue;
    }

    int getPosX() {
        return posX;
    }

    void setPosX(int posX) {
        this.posX = posX;
    }

    int getPosY() {
        return posY;
    }

    void setPosY(int posY) {
        this.posY = posY;
    }
}
