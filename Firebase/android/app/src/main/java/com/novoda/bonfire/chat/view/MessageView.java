package com.novoda.bonfire.chat.view;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.novoda.bonfire.R;
import com.novoda.bonfire.chat.data.model.Message;
import com.novoda.bonfire.view.CircleCropImageTransformation;
import com.novoda.notils.caster.Views;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageView extends LinearLayout {

    private final DateFormat timeFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
    private final Date date = new Date();
    private final ImageView picture;
    private final TextView body;
    private final TextView time;
    private final TextView name;
    private CircleCropImageTransformation circleCropTransformation;

    public MessageView(Context context) {
        super(context);
        configureViewParams();
        View.inflate(context, R.layout.merge_message_item_view, this);
        this.picture = Views.findById(this, R.id.messageAuthorImage);
        this.body = Views.findById(this, R.id.messageBody);
        this.time = Views.findById(this, R.id.messageTime);
        this.name = Views.findById(this, R.id.messageAuthorName);
        circleCropTransformation = new CircleCropImageTransformation(context);
    }

    private void configureViewParams() {
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        int horizontalMargin = getResources().getDimensionPixelSize(R.dimen.list_item_horizontal_margin);
        int verticalMargin = getResources().getDimensionPixelSize(R.dimen.list_item_vertical_margin);
        setPadding(horizontalMargin, verticalMargin, horizontalMargin, verticalMargin);
    }

    public void display(Message message) {
        Context context = getContext();
        Glide.with(context).load(message.getAuthor().getPhotoUrl())
                .transform(circleCropTransformation)
                .into(picture);
        body.setText(message.getBody());
        time.setText(formattedTimeFrom(message.getTimestamp()));
        name.setText(message.getAuthor().getName());
    }

    private String formattedTimeFrom(long timestamp) {
        date.setTime(timestamp);
        return timeFormat.format(date);
    }

}
