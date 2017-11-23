package com.novoda.frankboylan.meetingseating.rooms;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.novoda.frankboylan.meetingseating.R;
import com.novoda.frankboylan.meetingseating.rooms.heatmap.HeatmapSeatListActivity;

import java.util.List;

class CustomRoomAdapter extends ArrayAdapter<Room> {
    private List<Room> roomList;
    private Context context;

    CustomRoomAdapter(Context context, List<Room> roomList) {
        super(context, -1, roomList);
        this.context = context;
        this.roomList = roomList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.room_list_item, parent, false);
        TextView tvRoomName = rowView.findViewById(R.id.tv_room_name);
        TextView tvLocation = rowView.findViewById(R.id.tv_room_location);
        TextView tvUnitName = rowView.findViewById(R.id.tv_room_unitname);
        TextView tvSeatCount = rowView.findViewById(R.id.tv_room_seatcount);

        Room room = roomList.get(position);
        rowView.setTag(room.getRoomId());
        tvRoomName.setText(room.getRoomName());
        tvLocation.setText(room.getLocation());
        tvUnitName.setText(room.getUnitName());
        int seatCount = room.getSeats().size();
        if (seatCount == 0) {
            tvSeatCount.setText("0");
        } else {
            tvSeatCount.setText(String.valueOf(seatCount));
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, HeatmapSeatListActivity.class);
                    intent.putExtra("roomId", v.getTag().toString());
                    context.startActivity(intent);
                }
            });
        }

        return rowView;
    }
}
