package com.novoda.bonfire.chat.view;

import android.content.Context;
import android.util.AttributeSet;
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
    private ImageView picture;
    private TextView body;
    private TextView time;
    private TextView name;

    public MessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.merge_message_item_view, this);
        this.picture = Views.findById(this, R.id.messageAuthorImage);
        this.body = Views.findById(this, R.id.messageBody);
        this.time = Views.findById(this, R.id.messageTime);
        this.name = Views.findById(this, R.id.messageAuthorName);
    }

    public void display(Message message) {
        Context context = getContext();
        Glide.with(context)
                .load(message.getAuthor().getPhotoUrl())
                .error(R.drawable.ic_person)
                .transform(new CircleCropImageTransformation(context))
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
