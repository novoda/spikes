package com.novoda.tpbot.bot;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.novoda.tpbot.R;

import java.util.List;

class MoveAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater layoutInflater;
    private List<String> moves;

    public MoveAdapter(LayoutInflater layoutInflater, List<String> moves) {
        this.layoutInflater = layoutInflater;
        this.moves = moves;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return MoveViewHolder.inflate(layoutInflater, R.layout.view_move_item, parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String move = moves.get(position);
        ((MoveViewHolder) holder).bind(move);
    }

    @Override
    public int getItemCount() {
        return moves.size();
    }

    public void update(List<String> moves) {
        this.moves = moves;
        notifyDataSetChanged();
    }
}
