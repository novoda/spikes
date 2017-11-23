package com.novoda.frankboylan.meetingseating.rooms;

import com.novoda.frankboylan.meetingseating.seats.Seat;
import com.squareup.moshi.Json;

import java.util.ArrayList;
import java.util.List;

public class Room {

    @Json(name = "location")
    private String location;
    @Json(name = "unitName")
    private String unitName;
    @Json(name = "roomName")
    private String roomName;
    @Json(name = "roomId")
    private Integer roomId;
    @Json(name = "seats")
    private List<Seat> seats = new ArrayList<>();

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    public void updateSeatRoomIds() {
        for (Seat seat : seats) {
            seat.setRoomId(roomId);
        }
    }

    @Override
    public String toString() {
        return "Room{" +
                "location='" + location + '\'' +
                ", unitName='" + unitName + '\'' +
                ", roomName='" + roomName + '\'' +
                ", roomId=" + roomId +
                ", seats=" + seats +
                '}';
    }
}
