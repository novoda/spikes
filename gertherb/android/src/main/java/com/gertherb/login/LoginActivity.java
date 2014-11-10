package com.gertherb.login;

import android.os.Bundle;

import com.gertherb.BuildConfig;
import com.gertherb.R;
import com.gertherb.api.GithubApi;
import com.gertherb.api.GithubApiFactory;
import com.gertherb.api.TokenRequest;
import com.gertherb.api.TokenResponse;
import com.gertherb.authentication.Token;
import com.gertherb.base.GertHerbAuthenticationActivity;
import com.novoda.notils.caster.Views;

import rx.Observable;
import rx.functions.Func1;

public class LoginActivity extends GertHerbAuthenticationActivity {

    private static final String CLIENT_ID = BuildConfig.GITHUB_CLIENT_ID;
    private static final String CLIENT_SECRET = BuildConfig.GITHUB_CLIENT_SECRET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected Observable<UserCredentials> getCredentialsObservable() {
        LoginView loginView = Views.findById(this, R.id.login_view);
        return loginView.newLoginObservable();
    }

    @Override
    protected Observable<Token> getTokenObservable(final UserCredentials credentials) {
        GithubApi authorizationApi = new GithubApiFactory().createAuthorizationApi(credentials.username, credentials.password);
        return authorizationApi.authorize(CLIENT_ID, TokenRequest.from(CLIENT_SECRET)).map(new Func1<TokenResponse, Token>() {
            @Override
            public Token call(TokenResponse tokenResponse) {
                return new Token(credentials.username, tokenResponse.getToken());
            }
        });
    }
}
