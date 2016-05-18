package com.novoda.bonfire.channel.view;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.novoda.bonfire.R;
import com.novoda.bonfire.channel.data.model.Channels;
import com.novoda.bonfire.channel.displayer.ChannelsDisplayer;
import com.novoda.notils.caster.Views;

public class ChannelsView extends FrameLayout implements ChannelsDisplayer {

    private final ChannelsAdapter channelsAdapter = new ChannelsAdapter();
    private FloatingActionButton newChannelFab;

    public ChannelsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.merge_channels_view, this);
        RecyclerView channels = Views.findById(this, R.id.channels);
        channels.setLayoutManager(new LinearLayoutManager(getContext()));
        channels.setAdapter(channelsAdapter);
        newChannelFab = Views.findById(this, R.id.newChannelFab);
    }

    @Override
    public void display(Channels channels) {
        channelsAdapter.update(channels);
    }

    @Override
    public void attach(final ChannelsInteractionListener channelsInteractionListener) {
        channelsAdapter.attach(channelsInteractionListener);
        newChannelFab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                channelsInteractionListener.onCreateChannel();
            }
        });
    }

    @Override
    public void detach(ChannelsInteractionListener channelsInteractionListener) {
        channelsAdapter.detach(channelsInteractionListener);
        newChannelFab.setOnClickListener(null);
    }
}
