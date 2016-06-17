package com.novoda.bonfire.channel;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.novoda.bonfire.BaseActivity;
import com.novoda.bonfire.BuildConfig;
import com.novoda.bonfire.Dependencies;
import com.novoda.bonfire.R;
import com.novoda.bonfire.channel.displayer.ChannelsDisplayer;
import com.novoda.bonfire.channel.presenter.ChannelsPresenter;
import com.novoda.bonfire.link.FirebaseDynamicLinkFactory;
import com.novoda.bonfire.navigation.AndroidNavigator;

public class ChannelsActivity extends BaseActivity {

    private ChannelsPresenter channelsPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channels);
        Dependencies dependencies = Dependencies.INSTANCE;

        FirebaseDynamicLinkFactory firebaseDynamicLinkFactory = new FirebaseDynamicLinkFactory(
                getResources().getString(R.string.dynamicLinkDomain),
                getResources().getString(R.string.deepLinkBaseUrl),
                getResources().getString(R.string.iosBundleIdentifier),
                BuildConfig.APPLICATION_ID
        );
        channelsPresenter = new ChannelsPresenter(
                (ChannelsDisplayer) findViewById(R.id.channels_view),
                dependencies.getChannelService(),
                dependencies.getLoginService(),
                dependencies.getConfig(),
                new AndroidNavigator(this),
                firebaseDynamicLinkFactory,
                dependencies.getAnalytics()
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        channelsPresenter.startPresenting();
    }

    @Override
    protected void onStop() {
        channelsPresenter.stopPresenting();
        super.onStop();
    }
}
