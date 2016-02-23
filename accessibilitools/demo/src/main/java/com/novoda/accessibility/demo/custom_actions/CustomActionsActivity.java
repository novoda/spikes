package com.novoda.accessibility.demo.custom_actions;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.novoda.accessibility.demo.R;

public class CustomActionsActivity extends AppCompatActivity {

    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_actions);

        TweetView tweetView = (TweetView) findViewById(R.id.tweet);
        tweetView.display(
                "This is a tweet lol",
                new TweetView.Listener() {

                    @Override
                    public void onClick(String tweet) {
                        toast("click: " + tweet);
                    }

                    @Override
                    public void onClickReply(String tweet) {
                        toast("reply: " + tweet);
                    }

                    @Override
                    public void onClickRetweet(String tweet) {
                        toast("rt: " + tweet);
                    }

                }
        );
    }

    private void toast(String message) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

}
