package com.novoda.frankboylan.meetingseating.rooms.heatmap.adv.repo.model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

@Dao
public interface AdvHeatmapMetaDao {

    @Query("SELECT lastUpdated FROM metadata")
    String getLastUpdated();

    @Insert
    void insertMetadata(AdvHeatmapMeta metadata);

    @Query("DELETE FROM metadata")
    void deleteAllMetadata();
}
