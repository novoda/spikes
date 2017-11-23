package com.novoda.frankboylan.meetingseating.settings;

import android.content.res.AssetManager;

import com.novoda.frankboylan.meetingseating.RoomSeatData;
import com.novoda.frankboylan.meetingseating.seats.model.RoomDatabaseWriter;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.io.InputStream;

import okio.Buffer;

class SettingsModelImpl implements SettingsModel {
    private AssetManager assetManager;
    private final RoomDatabaseWriter roomDatabaseWriter;

    SettingsModelImpl(AssetManager assetManager, RoomDatabaseWriter roomDatabaseWriter) {
        this.assetManager = assetManager;
        this.roomDatabaseWriter = roomDatabaseWriter;
    }

    @Override
    public void replaceWithDataset(int i) {
        switch (i) {
            case 0:
                loadJSONFromFile("europe.txt");
                break;
            case 1:
                loadJSONFromFile("continents.txt");
                break;
        }
    }

    private void loadJSONFromFile(String directory) {
        try {
            InputStream input = assetManager.open(directory);
            Moshi build = new Moshi.Builder().build();
            JsonAdapter<RoomSeatData> adapter = build.adapter(RoomSeatData.class);
            Buffer buffer = new Buffer();
            buffer.readFrom(input);
            roomDatabaseWriter.add(adapter.fromJson(buffer));

        } catch (IOException e) {
            throw new IllegalStateException("Expected a file at " + directory + " but got nothing! FUBAR");
        }
    }
}
