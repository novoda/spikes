package com.novoda.bonfire.navigation;

import android.app.Activity;
import android.content.Intent;

import com.novoda.bonfire.channel.ChannelsActivity;
import com.novoda.bonfire.channel.NewChannelActivity;
import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.chat.ChatActivity;
import com.novoda.bonfire.user.UsersActivity;

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

    @Override
    public void toNewChannel() {
        activity.startActivity(new Intent(activity, NewChannelActivity.class));
    }

    @Override
    public void toAddUsersFor(Channel channel) {
        activity.startActivity(UsersActivity.createIntentFor(activity, channel));
    }

    @Override
    public void toParent() {
        activity.finish();
    }

}
