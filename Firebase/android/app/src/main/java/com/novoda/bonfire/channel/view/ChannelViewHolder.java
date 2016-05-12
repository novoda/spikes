package com.novoda.bonfire.channel.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.novoda.bonfire.channel.data.model.Channel;

class ChannelViewHolder extends RecyclerView.ViewHolder {

    private final ChannelView channelView;

    public ChannelViewHolder(ChannelView itemView) {
        super(itemView);
        this.channelView = itemView;
    }

    public void bind(final Channel channel, final ChannelSelectionListener listener) {
        channelView.display(channel);
        channelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onChannelSelected(channel);
            }
        });
    }

    public interface ChannelSelectionListener {
        void onChannelSelected(Channel channel);
    }
}
