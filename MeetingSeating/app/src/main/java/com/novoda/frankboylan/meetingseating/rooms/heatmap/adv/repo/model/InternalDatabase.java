package com.novoda.frankboylan.meetingseating.rooms.heatmap.adv.repo.model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {AdvHeatmapMeta.class, AdvHeatmapRoom.class, AdvHeatmapSeat.class}, version = 1)
public abstract class InternalDatabase extends RoomDatabase {
    public abstract AdvHeatmapMetaDao metaDao();

    public abstract AdvHeatmapRoomDao roomDao();

    public abstract AdvHeatmapSeatDao seatDao();
}
