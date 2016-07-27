package com.novoda.magicmirror.modules.greetings;

import android.widget.TextView;

import com.novoda.magicmirror.R;
import com.novoda.magicmirror.modules.DashboardModule;

import org.joda.time.DateTime;

public class GreetingsModule implements DashboardModule {

    private final TextView greetingsLabel;

    public GreetingsModule(TextView greetingsLabel) {
        this.greetingsLabel = greetingsLabel;
    }

    @Override
    public void update() {
        DateTime dateTime = DateTime.now();
        int hourOfDay = dateTime.getHourOfDay();
        int messageResId;
        if (hourOfDay <= 12) {
            messageResId = R.string.good_morning;
        } else if (hourOfDay < 17) {
            messageResId = R.string.good_afternoon;
        } else {
            messageResId = R.string.good_evening;
        }

        greetingsLabel.setText(messageResId);
    }

    @Override
    public void stop() {
        // no-op
    }

}
