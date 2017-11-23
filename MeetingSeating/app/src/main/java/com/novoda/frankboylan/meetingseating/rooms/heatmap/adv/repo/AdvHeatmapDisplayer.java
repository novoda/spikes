package com.novoda.frankboylan.meetingseating.rooms.heatmap.adv.repo;

import com.novoda.frankboylan.meetingseating.rooms.heatmap.adv.repo.model.AdvHeatmapRoom;
import com.novoda.frankboylan.meetingseating.rooms.heatmap.adv.repo.model.AdvHeatmapSeat;

import java.util.List;

public interface AdvHeatmapDisplayer {
    void updateRoomList(List<AdvHeatmapRoom> roomList);

    void updateSeatList(List<AdvHeatmapSeat> seatList);

    void updateMetaTimestamp(String latestTimestamp);
}
