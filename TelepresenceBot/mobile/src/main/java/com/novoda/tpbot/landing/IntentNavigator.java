package com.novoda.tpbot.landing;

import android.app.Activity;
import android.content.Intent;

import com.novoda.tpbot.bot.BotActivity;
import com.novoda.tpbot.human.HumanActivity;

public class IntentNavigator implements Navigator {

    private Activity activity;

    IntentNavigator(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void toBot() {
        Intent intent = new Intent(activity.getApplicationContext(), BotActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public void toHuman() {
        Intent intent = new Intent(activity.getApplicationContext(), HumanActivity.class);
        activity.startActivity(intent);
    }

}
