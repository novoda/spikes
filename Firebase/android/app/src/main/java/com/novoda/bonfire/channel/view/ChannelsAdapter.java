package com.novoda.bonfire.channel.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.novoda.bonfire.R;
import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.channel.data.model.Channels;
import com.novoda.bonfire.channel.displayer.ChannelsDisplayer;

import java.util.ArrayList;

class ChannelsAdapter extends RecyclerView.Adapter<ChannelViewHolder>  {

    private Channels channels = new Channels(new ArrayList<Channel>());
    private ChannelsDisplayer.ChannelsInteractionListener channelsInteractionListener;
    private final LayoutInflater inflater;

    ChannelsAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    public void update(Channels channels){
        this.channels = channels;
        notifyDataSetChanged();
    }

    @Override
    public ChannelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChannelViewHolder((ChannelView) inflater.inflate(R.layout.channel_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(ChannelViewHolder holder, int position) {
        holder.bind(channels.getChannelAt(position), clickListener);
    }

    @Override
    public int getItemCount() {
        return channels.size();
    }

    @Override
    public long getItemId(int position) {
        return channels.getChannelAt(position).hashCode();
    }

    public void attach(ChannelsDisplayer.ChannelsInteractionListener channelsInteractionListener) {
        this.channelsInteractionListener = channelsInteractionListener;
    }

    public void detach(ChannelsDisplayer.ChannelsInteractionListener channelsInteractionListener) {
        this.channelsInteractionListener = null;
    }

    private final ChannelViewHolder.ChannelSelectionListener clickListener = new ChannelViewHolder.ChannelSelectionListener() {
        @Override
        public void onChannelSelected(Channel channel) {
            ChannelsAdapter.this.channelsInteractionListener.onChannelSelected(channel);
        }
    };
}
