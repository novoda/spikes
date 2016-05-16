package com.novoda.bonfire.navigation;

import android.app.Activity;
import android.content.Intent;

import com.novoda.bonfire.channel.ChannelsActivity;
import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.chat.ChatActivity;

public class AndroidNavigator implements Navigator {

    private final Activity activity;

    public AndroidNavigator(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void toChannel(Channel channel) {
        activity.startActivity(ChatActivity.createIntentFor(activity, channel));
    }

    @Override
    public void toChannels() {
        activity.startActivity(new Intent(activity, ChannelsActivity.class));
    }

}
