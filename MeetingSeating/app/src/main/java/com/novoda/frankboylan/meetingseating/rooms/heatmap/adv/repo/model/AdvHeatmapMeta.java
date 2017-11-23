package com.novoda.frankboylan.meetingseating.rooms.heatmap.adv.repo.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "metadata")
public class AdvHeatmapMeta {

    @PrimaryKey
    @ColumnInfo(name = "lastUpdated")
    @NonNull
    private String lastUpdated = "0";

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
