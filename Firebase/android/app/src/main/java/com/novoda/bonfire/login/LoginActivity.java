package com.novoda.bonfire.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.api.ResultCallback;
import com.novoda.bonfire.BaseActivity;
import com.novoda.bonfire.Dependencies;
import com.novoda.bonfire.R;
import com.novoda.bonfire.login.displayer.LoginDisplayer;
import com.novoda.bonfire.login.presenter.LoginPresenter;
import com.novoda.bonfire.navigation.AndroidLoginNavigator;
import com.novoda.bonfire.navigation.AndroidNavigator;
import com.novoda.notils.logger.simple.Log;

public class LoginActivity extends BaseActivity {

    private static final int RC_SIGN_IN = 42;

    private LoginPresenter presenter;
    private AndroidLoginNavigator navigator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LoginDisplayer loginDisplayer = (LoginDisplayer) findViewById(R.id.loginView);
        AndroidNavigator navigator = new AndroidNavigator(this);
        AndroidLoginNavigator androidLoginNavigator = new AndroidLoginNavigator(this, navigator);
        this.navigator = androidLoginNavigator;
        presenter = new LoginPresenter(Dependencies.INSTANCE.getLoginService(), loginDisplayer, this.navigator);

        AppInvite.AppInviteApi.getInvitation(androidLoginNavigator.getGoogleApiClient(), this, false)
                .setResultCallback(
                        new ResultCallback<AppInviteInvitationResult>() {
                            @Override
                            public void onResult(@NonNull AppInviteInvitationResult result) {
                                if (result.getStatus().isSuccess()) {
                                    // Extract deep link from Intent
                                    Intent intent = result.getInvitationIntent();
                                    String deepLink = AppInviteReferral.getDeepLink(intent);
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(deepLink)));
                                    finish();
                                } else {
                                    Log.d("getInvitation: no deep link found.");
                                }
                            }
                        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!navigator.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.startPresenting();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.stopPresenting();
    }

}
