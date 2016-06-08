package com.novoda.bonfire.channel.view;

import android.content.Context;
import android.content.res.Configuration;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.novoda.bonfire.R;
import com.novoda.bonfire.channel.data.model.Channels;
import com.novoda.bonfire.channel.displayer.ChannelsDisplayer;
import com.novoda.notils.caster.Views;

public class ChannelsView extends LinearLayout implements ChannelsDisplayer {

    private final ChannelsAdapter channelsAdapter;
    private FloatingActionButton newChannelFab;
    private Toolbar toolbar;
    private ChannelsInteractionListener channelsInteractionListener;

    public ChannelsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        channelsAdapter = new ChannelsAdapter(LayoutInflater.from(context));
        setOrientation(VERTICAL);
    }
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.merge_channels_view, this);
        RecyclerView channels = Views.findById(this, R.id.channels);
        channels.setAdapter(channelsAdapter);
        newChannelFab = Views.findById(this, R.id.newChannelFab);
        toolbar = Views.findById(this, R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.inflateMenu(R.menu.channels_menu);
    }

    private int getSpanCount() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? 2 : 4;
    }

    @Override
    public void display(Channels channels) {
        channelsAdapter.update(channels);
    }

    @Override
    public void attach(ChannelsInteractionListener channelsInteractionListener) {
        this.channelsInteractionListener = channelsInteractionListener;
        channelsAdapter.attach(channelsInteractionListener);
        newChannelFab.setOnClickListener(fabClickListener);
        toolbar.setOnMenuItemClickListener(menuItemClickListener);
    }

    @Override
    public void detach(ChannelsInteractionListener channelsInteractionListener) {
        channelsAdapter.detach(channelsInteractionListener);
        newChannelFab.setOnClickListener(null);
        toolbar.setOnMenuItemClickListener(null);
        this.channelsInteractionListener = null;
    }

    private Toolbar.OnMenuItemClickListener menuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (item.getItemId() == R.id.actionInvite) {
                channelsInteractionListener.onInviteUsersClicked();
                return true;
            }
            return false;
        }
    };

    private final OnClickListener fabClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            channelsInteractionListener.onAddNewChannel();
        }
    };
}
