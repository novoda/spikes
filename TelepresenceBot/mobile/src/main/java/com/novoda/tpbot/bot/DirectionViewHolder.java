package com.novoda.tpbot.bot;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.novoda.tpbot.Direction;

class DirectionViewHolder extends RecyclerView.ViewHolder {

    private TextView directionTextView;

    static DirectionViewHolder inflate(LayoutInflater layoutInflater, @LayoutRes int layoutResId, ViewGroup container) {
        TextView directionView = (TextView) layoutInflater.inflate(layoutResId, container, false);
        return new DirectionViewHolder(directionView);
    }

    private DirectionViewHolder(TextView itemView) {
        super(itemView);
        this.directionTextView = itemView;
    }

    void bind(Direction direction) {
        directionTextView.setText(direction.visualRepresentation());
    }
}
