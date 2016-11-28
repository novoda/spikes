package com.novoda.tpbot.bot;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

public class MoveViewHolder extends RecyclerView.ViewHolder {

    public TextView moveTextView;

    static MoveViewHolder inflate(LayoutInflater layoutInflater, @LayoutRes int layoutResId, ViewGroup container) {
        TextView moveView = (TextView) layoutInflater.inflate(layoutResId, container, false);
        return new MoveViewHolder(moveView);
    }

    public MoveViewHolder(TextView itemView) {
        super(itemView);
        this.moveTextView = itemView;
    }

    public void bind(String move) {
        moveTextView.setText(move);
    }
}
