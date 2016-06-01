package com.novoda.bonfire.login;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.novoda.bonfire.BaseActivity;
import com.novoda.bonfire.R;
import com.novoda.notils.logger.simple.Log;

public class LoginGoogleApiClient {

    private final BaseActivity activity;

    private GoogleApiClient apiClient;

    public LoginGoogleApiClient(BaseActivity activity) {
        this.activity = activity;
    }

    public void setupGoogleApiClient() {
        String string = activity.getString(R.string.default_web_client_id);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(string)
                .requestEmail()
                .build();
        apiClient = new GoogleApiClient.Builder(activity)
                .enableAutoManage(
                        activity, new GoogleApiClient.OnConnectionFailedListener() {
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

    public Intent getSignInIntent() {
        return Auth.GoogleSignInApi.getSignInIntent(apiClient);
    }

    public GoogleSignInResult getSignInResultFromIntent(Intent data) {
        return Auth.GoogleSignInApi.getSignInResultFromIntent(data);
    }

    public void checkForInvites(final AppInviteDeepLinkListener listener) {
        AppInvite.AppInviteApi.getInvitation(apiClient, activity, false)
                .setResultCallback(
                        new ResultCallback<AppInviteInvitationResult>() {
                            @Override
                            public void onResult(@NonNull AppInviteInvitationResult result) {
                                if (result.getStatus().isSuccess()) {
                                    Intent intent = result.getInvitationIntent();
                                    listener.onDeepLinkFound(AppInviteReferral.getDeepLink(intent));
                                } else {
                                    Log.d("getInvitation: no deep link found.");
                                }
                            }
                        });
    }

    public interface AppInviteDeepLinkListener {

        void onDeepLinkFound(String deepLinkUri);

    }

}
