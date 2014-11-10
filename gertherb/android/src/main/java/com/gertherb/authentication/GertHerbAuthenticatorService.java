package com.gertherb.authentication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class GertHerbAuthenticatorService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        GertHerbAuthenticator authenticator = new GertHerbAuthenticator(this);
        return authenticator.getIBinder();
    }
}
