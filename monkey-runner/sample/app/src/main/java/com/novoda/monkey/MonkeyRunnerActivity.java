package com.novoda.monkey;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MonkeyRunnerActivity extends AppCompatActivity {

    private boolean showingAlternativeBackgroundColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monkey_runner);
    }

    public void toggleBackgroundColor(View view) {
        if (showingAlternativeBackgroundColor) {
            showDefaultBackgroundColor();
        } else {
            showAlternativeBackgroundColor();
        }
        showingAlternativeBackgroundColor = !showingAlternativeBackgroundColor;
    }

    private void showDefaultBackgroundColor() {
        getWindow().setBackgroundDrawableResource(android.R.color.holo_green_light);
    }

    private void showAlternativeBackgroundColor() {
        getWindow().setBackgroundDrawableResource(android.R.color.holo_blue_light);
    }
}
