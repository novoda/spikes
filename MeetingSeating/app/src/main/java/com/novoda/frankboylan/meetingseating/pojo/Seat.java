package com.novoda.frankboylan.meetingseating.pojo;

import com.squareup.moshi.Json;

public class Seat {

    @Json(name = "seatId")
    private Integer seatId;
    @Json(name = "heat")
    private Integer heat;
    @Json(name = "posX")
    private Integer posX;
    @Json(name = "posY")
    private Integer posY;

    public Integer getSeatId() {
        return seatId;
    }

    public void setSeatId(Integer seatId) {
        this.seatId = seatId;
    }

    public Integer getHeat() {
        return heat;
    }

    public void setHeat(Integer heat) {
        this.heat = heat;
    }

    public Integer getPosX() {
        return posX;
    }

    public void setPosX(Integer posX) {
        this.posX = posX;
    }

    public Integer getPosY() {
        return posY;
    }

    public void setPosY(Integer posY) {
        this.posY = posY;
    }

}
