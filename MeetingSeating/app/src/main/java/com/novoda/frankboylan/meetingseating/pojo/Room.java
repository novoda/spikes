package com.novoda.frankboylan.meetingseating.pojo;

import com.novoda.frankboylan.meetingseating.seats.Seat;
import com.squareup.moshi.Json;

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
    @Json(name = "sizeX")
    private Integer sizeX;
    @Json(name = "sizeY")
    private Integer sizeY;
    @Json(name = "seats")
    private List<Seat> seats = null;

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

    public Integer getSizeX() {
        return sizeX;
    }

    public void setSizeX(Integer sizeX) {
        this.sizeX = sizeX;
    }

    public Integer getSizeY() {
        return sizeY;
    }

    public void setSizeY(Integer sizeY) {
        this.sizeY = sizeY;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

}
