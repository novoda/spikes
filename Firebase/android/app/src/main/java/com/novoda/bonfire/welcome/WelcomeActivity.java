package com.novoda.bonfire.welcome;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.novoda.bonfire.BaseActivity;
import com.novoda.bonfire.navigation.AndroidNavigator;
import com.novoda.notils.logger.simple.Log;

public class WelcomeActivity extends BaseActivity {

    private AndroidNavigator navigator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Got it!");
        TextView textView = new TextView(this);
        setContentView(textView);
        String sender = getIntent().getData().getQueryParameter("sender");
        textView.setText(sender);
        navigator = new AndroidNavigator(this);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigator.toLogin();
            }
        });
    }

    @Override
    public void onBackPressed() {
        navigator.toLogin();
    }

}
