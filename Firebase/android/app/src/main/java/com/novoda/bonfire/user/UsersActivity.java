package com.novoda.bonfire.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.novoda.bonfire.BaseActivity;
import com.novoda.bonfire.Dependencies;
import com.novoda.bonfire.R;
import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.navigation.AndroidNavigator;
import com.novoda.bonfire.user.displayer.UsersDisplayer;
import com.novoda.bonfire.user.presenter.UsersPresenter;

public class UsersActivity extends BaseActivity {

    private static final String CHANNEL_EXTRA = "CHANNEL_EXTRA";
    private UsersPresenter presenter;

    public static Intent createIntentFor(Context context, Channel channel) {
        Intent intent = new Intent(context, UsersActivity.class);

        intent.putExtra(CHANNEL_EXTRA, channel);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        UsersDisplayer usersDisplayer = (UsersDisplayer) findViewById(R.id.usersView);
        Channel channel = (Channel) getIntent().getSerializableExtra(CHANNEL_EXTRA);
        presenter = new UsersPresenter(
                Dependencies.INSTANCE.getUserService(),
                Dependencies.INSTANCE.getChannelService(),
                usersDisplayer,
                channel,
                new AndroidNavigator(this)
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.startPresenting();
    }

    @Override
    protected void onStop() {
        presenter.stopPresenting();
        super.onStop();
    }
}
