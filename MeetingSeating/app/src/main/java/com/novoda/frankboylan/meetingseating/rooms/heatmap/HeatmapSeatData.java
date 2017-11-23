package com.novoda.frankboylan.meetingseating.rooms.heatmap;

import com.squareup.moshi.Json;

import java.util.ArrayList;
import java.util.List;

public class HeatmapSeatData {
    @Json(name = "roomId")
    private String roomId;
    @Json(name = "heat-unit")
    private String heatUnit;
    @Json(name = "seats")
    private List<HeatmapSeat> seats = new ArrayList<>();

    public String getRoomId() {
        return roomId;
    }

    public String getHeatUnit() {
        return heatUnit;
    }

    public List<HeatmapSeat> getSeats() {
        return seats;
    }
}
