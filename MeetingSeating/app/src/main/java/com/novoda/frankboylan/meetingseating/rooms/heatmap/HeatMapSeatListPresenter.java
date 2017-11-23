package com.novoda.frankboylan.meetingseating.rooms.heatmap;

import java.util.List;

interface HeatMapSeatListPresenter {
    void bind(HeatmapSeatListDisplayer displayer);

    void unbind();

    void startPresenting();

    void getData(String roomId);

    void updateList(List<HeatmapSeat> seatList);
}
