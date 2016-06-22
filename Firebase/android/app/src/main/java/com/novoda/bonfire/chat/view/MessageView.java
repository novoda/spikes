package com.novoda.bonfire.chat.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.novoda.bonfire.R;
import com.novoda.bonfire.chat.data.model.Message;
import com.novoda.bonfire.view.CircleCropImageTransformation;
import com.novoda.bonfire.view.MessageBubbleDrawable;
import com.novoda.notils.caster.Views;
import com.novoda.notils.exception.DeveloperError;

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

    private int layoutResId;

    public MessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
        super.setOrientation(VERTICAL);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            int[] attrsArray = {
                    android.R.attr.layout
            };
            TypedArray array = context.obtainStyledAttributes(attrs, attrsArray);
            layoutResId = array.getResourceId(0, R.layout.merge_message_item_view);
            array.recycle();
        }
    }

    public void setTextBackground(MessageBubbleDrawable bubbleDrawable) {
        body.setBackground(bubbleDrawable);
    }

    @Override
    public void setOrientation(int orientation) {
        throw new DeveloperError("This view only supports vertical orientation");
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), layoutResId, this);
        this.picture = Views.findById(this, R.id.message_author_image);
        this.body = Views.findById(this, R.id.message_body);
        this.time = Views.findById(this, R.id.message_time);
        this.name = Views.findById(this, R.id.message_author_name);
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
