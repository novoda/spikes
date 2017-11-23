package com.novoda.frankboylan.meetingseating.rooms.heatmap;

import java.util.List;

interface
HeatmapSeatListDisplayer {
    void makeToast(String message);

    void updateAdapter(List<HeatmapSeat> seatList);
}
