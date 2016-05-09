package com.novoda.firechat.navigation;

import android.app.Activity;
import android.content.Intent;

import com.novoda.firechat.chat.ChatActivity;
import com.novoda.firechat.chat.navigation.Navigator;

public class AndroidNavigator implements Navigator {

    private final Activity activity;

    public AndroidNavigator(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void toChat() {
        activity.startActivity(new Intent(activity, ChatActivity.class));
    }

}
