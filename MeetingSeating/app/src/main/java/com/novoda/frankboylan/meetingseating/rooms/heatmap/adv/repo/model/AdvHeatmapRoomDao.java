package com.novoda.frankboylan.meetingseating.rooms.heatmap.adv.repo.model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface AdvHeatmapRoomDao {

    @Insert
    void insertRoom(AdvHeatmapRoom room);

    @Insert
    void insertRooms(List<AdvHeatmapRoom> roomList);

    @Query("DELETE FROM room_data")
    void deleteAllRooms();

    @Query("SELECT * FROM room_data WHERE room_id LIKE :roomId")
    List<AdvHeatmapRoom> findByRoomId(String roomId);

    @Query("SELECT * FROM room_data")
    List<AdvHeatmapRoom> getAllRooms();
}
