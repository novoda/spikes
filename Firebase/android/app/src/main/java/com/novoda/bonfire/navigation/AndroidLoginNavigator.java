package com.novoda.bonfire.navigation;

import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.novoda.bonfire.BaseActivity;
import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.login.LoginGoogleApiClient;
import com.novoda.notils.logger.simple.Log;

public class AndroidLoginNavigator implements LoginNavigator {

    private static final int RC_SIGN_IN = 242;

    private final BaseActivity activity;
    private final LoginGoogleApiClient googleApiClient;
    private final Navigator navigator;
    private LoginResultListener loginResultListener;

    public AndroidLoginNavigator(BaseActivity activity, LoginGoogleApiClient googleApiClient, Navigator navigator) {
        this.activity = activity;
        this.googleApiClient = googleApiClient;
        this.navigator = navigator;
    }

    @Override
    public void toChannel(Channel channel) {
        navigator.toChannel(channel);
    }

    @Override
    public void toChannels() {
        navigator.toChannels();
        activity.finish();
    }

    @Override
    public void toCreateChannel() {
        navigator.toCreateChannel();
    }

    @Override
    public void toMembersOf(Channel channel) {
        navigator.toMembersOf(channel);
    }

    @Override
    public void toParent() {
        navigator.toParent();
    }

    @Override
    public void toChannelWithClearedHistory(Channel channel) {
        navigator.toChannelWithClearedHistory(channel);
    }

    @Override
    public void toShareInvite(String sharingLink) {
        navigator.toShareInvite(sharingLink);
    }

    @Override
    public void toLogin() {
        //No op
    }

    @Override
    public void toGooglePlusLogin() {
        Intent signInIntent = googleApiClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void attach(LoginResultListener loginResultListener) {
        this.loginResultListener = loginResultListener;
    }

    @Override
    public void detach(LoginResultListener loginResultListener) {
        this.loginResultListener = null;
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != RC_SIGN_IN) {
            return false;
        }
        GoogleSignInResult result = googleApiClient.getSignInResultFromIntent(data);
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            loginResultListener.onGooglePlusLoginSuccess(account.getIdToken());
        } else {
            Log.e("Failed to authenticate GooglePlus", result.getStatus().getStatusCode());
            loginResultListener.onGooglePlusLoginFailed(result.getStatus().getStatusMessage());
        }
        return true;
    }
}
