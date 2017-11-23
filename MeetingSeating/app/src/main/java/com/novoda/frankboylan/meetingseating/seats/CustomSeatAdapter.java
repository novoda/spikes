package com.novoda.frankboylan.meetingseating.seats;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.novoda.frankboylan.meetingseating.R;

import java.util.List;

class CustomSeatAdapter extends ArrayAdapter<Seat> {
    private List<Seat> seatList;
    private Context context;

    CustomSeatAdapter(Context context, List<Seat> seatList) {
        super(context, -1, seatList);
        this.context = context;
        this.seatList = seatList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.seat_list_item, parent, false);

        TextView tvSeatId = rowView.findViewById(R.id.tv_seat_id);
        TextView tvLocation = rowView.findViewById(R.id.tv_location);
        TextView tvWeight = rowView.findViewById(R.id.tv_weight);

        Seat seat = seatList.get(position);
        tvSeatId.setText("#" + seat.getSeatId().toString());
        tvLocation.setText("Room Id: " + seat.getRoomId().toString());
        tvWeight.setText(seat.getValue().toString() + seat.getUnitType().toString());

        return rowView;
    }
}

