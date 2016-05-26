package com.novoda.bonfire.navigation;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.novoda.bonfire.BaseActivity;
import com.novoda.bonfire.R;
import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.notils.logger.simple.Log;

public class AndroidLoginNavigator implements LoginNavigator {

    private static final int RC_SIGN_IN = 242;

    private final BaseActivity activity;
    private final GoogleApiClient googleApiClient;
    private final Navigator navigator;
    private LoginResultListener loginResultListener;

    public AndroidLoginNavigator(BaseActivity activity, Navigator navigator) {
        this.activity = activity;
        this.navigator = navigator;
        this.googleApiClient = setupGoogleApiClient(); //TODO improve this creation
    }

    private GoogleApiClient setupGoogleApiClient() {
        String string = activity.getString(R.string.default_web_client_id);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(string)
                .requestEmail()
                .build();
        return new GoogleApiClient.Builder(activity)
                .enableAutoManage(activity, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d("FireChat", "Failed to connect to GMS");
                        //TODO handle error
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(AppInvite.API)
                .build();
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
    public void toNewChannel() {
        navigator.toNewChannel();
    }

    @Override
    public void toAddUsersFor(Channel channel) {
        navigator.toAddUsersFor(channel);
    }

    @Override
    public void toLogin() {
        //No op
    }

    @Override
    public void toGooglePlusLogin() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
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
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            loginResultListener.onGooglePlusLoginSuccess(account.getIdToken());
        } else {
            Log.e("Failed to authenticate GooglePlus", result.getStatus().getStatusCode());
            loginResultListener.onGooglePlusLoginFailed(result.getStatus().getStatusMessage());
        }
        return true;
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }
}
