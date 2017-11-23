package com.novoda.frankboylan.meetingseating.rooms.heatmap;

import com.squareup.moshi.Json;

class HeatmapSeat {
    @Json(name = "seatId")
    private Integer seatId;
    @Json(name = "heat")
    private Integer heatValue;

    public Integer getSeatId() {
        return seatId;
    }

    public void setSeatId(Integer seatId) {
        this.seatId = seatId;
    }

    public Integer getHeatValue() {
        return heatValue;
    }

    public void setHeatValue(Integer value) {
        this.heatValue = value;
    }

    @Override
    public String toString() {
        return "HeatmapSeat{" +
                "seatId=" + seatId +
                ", heatValue=" + heatValue +
                '}';
    }
}
