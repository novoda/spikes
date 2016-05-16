package com.novoda.bonfire.channel.view;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.novoda.bonfire.R;
import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.notils.caster.Views;

public class ChannelView extends LinearLayout {

    private final TextView title;

    public ChannelView(Context context) {
        super(context);
        configureViewParams();
        View view = inflate(context, R.layout.merge_channel_item_view, this);
        title = Views.findById(view, R.id.channelTitle);
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        setBackgroundResource(outValue.resourceId);
    }

    private void configureViewParams() {
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        int horizontalMargin = getResources().getDimensionPixelSize(R.dimen.list_item_horizontal_margin);
        int verticalMargin = getResources().getDimensionPixelSize(R.dimen.list_item_vertical_margin);
        setPadding(horizontalMargin, verticalMargin, horizontalMargin, verticalMargin);
    }

    public void display(Channel channel) {
        title.setText(channel.getName());
    }
}
