package com.novoda.bonfire.channel.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.novoda.bonfire.R;
import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.notils.caster.Views;

public class ChannelView extends LinearLayout {

    private TextView title;

    public ChannelView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.merge_channel_item_view, this);
        title = Views.findById(this, R.id.channelTitle);
    }

    public void display(Channel channel) {
        title.setText(channel.getName());
    }
}
