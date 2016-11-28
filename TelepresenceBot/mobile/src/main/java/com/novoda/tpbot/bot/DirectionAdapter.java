package com.novoda.tpbot.bot;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.novoda.tpbot.Direction;
import com.novoda.tpbot.R;

import java.util.List;

class DirectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater layoutInflater;
    private List<Direction> directions;

    public DirectionAdapter(LayoutInflater layoutInflater, List<Direction> directions) {
        this.layoutInflater = layoutInflater;
        this.directions = directions;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return DirectionViewHolder.inflate(layoutInflater, R.layout.view_direction_item, parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Direction direction = directions.get(position);
        ((DirectionViewHolder) holder).bind(direction);
    }

    @Override
    public int getItemCount() {
        return directions.size();
    }

    public void update(List<Direction> directions) {
        this.directions = directions;
        notifyDataSetChanged();
    }
}
