package com.novoda.accessibility.demo.custom_actions;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.novoda.accessibility.demo.R;

public class TweetView extends LinearLayout {

    private TextView tweetTextView;

    public TweetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.merge_tweet, this);
        tweetTextView = (TextView) findViewById(R.id.tweet_text);
    }

    public void display(final String tweet, final Listener listener) {
        tweetTextView.setText(tweet);

        setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onClick(tweet);
                    }
                }
        );
    }

    public interface Listener {

        void onClick(String tweet);
        void onClickReply(String tweet);
        void onClickRetweet(String tweet);

    }

}
