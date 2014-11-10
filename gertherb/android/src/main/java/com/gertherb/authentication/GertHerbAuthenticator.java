package com.gertherb.authentication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.gertherb.login.LoginActivity;

public class GertHerbAuthenticator extends BetterAuthenticator {

    public GertHerbAuthenticator(Context context) {
        super(context);
    }

    @Override
    protected Intent createLoginIntent(Context context, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) {
        return new Intent(context, LoginActivity.class);
    }
}
