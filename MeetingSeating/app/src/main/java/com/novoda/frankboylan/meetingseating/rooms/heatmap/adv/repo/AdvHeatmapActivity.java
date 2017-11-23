package com.novoda.frankboylan.meetingseating.rooms.heatmap.adv.repo;

import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.novoda.frankboylan.meetingseating.R;
import com.novoda.frankboylan.meetingseating.rooms.heatmap.adv.repo.model.AdvHeatmapModelImpl;
import com.novoda.frankboylan.meetingseating.rooms.heatmap.adv.repo.model.AdvHeatmapPresenterImpl;
import com.novoda.frankboylan.meetingseating.rooms.heatmap.adv.repo.model.AdvHeatmapRoom;
import com.novoda.frankboylan.meetingseating.rooms.heatmap.adv.repo.model.AdvHeatmapSeat;
import com.novoda.frankboylan.meetingseating.rooms.heatmap.adv.repo.model.InternalDatabase;

import java.util.List;

public class AdvHeatmapActivity extends AppCompatActivity implements AdvHeatmapDisplayer {
    private static final String DATABASE_NAME = "AdvDatabase";
    AdvHeatmapPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adv_heatmap);

        final InternalDatabase database = Room.databaseBuilder(
                getApplicationContext(),
                InternalDatabase.class,
                DATABASE_NAME
        ).build();

        presenter = new AdvHeatmapPresenterImpl(database, this.getAssets(), new AdvHeatmapModelImpl());
        presenter.bind(this);
        presenter.startPresenting();
    }

    @Override
    public void updateRoomList(List<AdvHeatmapRoom> roomList) {
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, roomList);
        ListView roomListView = findViewById(R.id.lv_room_list);
        roomListView.setAdapter(adapter);
    }

    @Override
    public void updateSeatList(List<AdvHeatmapSeat> seatList) {
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, seatList);
        ListView seatListView = findViewById(R.id.lv_seat_list);
        seatListView.setAdapter(adapter);
    }

    @Override
    public void updateMetaTimestamp(String latestTimestamp) {
        TextView timestampTextView = findViewById(R.id.tv_timestamp);
        timestampTextView.setText(latestTimestamp);
    }

    @Override
    protected void onDestroy() {
        presenter.unbind();
        super.onDestroy();
    }
}
