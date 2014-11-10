package com.gertherb.base;

import android.os.Bundle;

import com.gertherb.authentication.ReactiveAuthenticatorActivity;
import com.gertherb.login.UserCredentials;
import com.novoda.notils.logger.toast.Toaster;
import com.novoda.rx.core.ObservableVault;

import static com.gertherb.base.Utils.classOf;

public abstract class GertHerbAuthenticationActivity extends ReactiveAuthenticatorActivity<UserCredentials> {

    private Toaster toaster;
    private ViewServerManager viewServerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toaster = new Toaster(this);
        viewServerManager = new ViewServerManager(this);
        viewServerManager.onCreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewServerManager.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewServerManager.onDestroy();
    }

    protected void toast(int messageResId) {
        toaster.popToast(messageResId);
    }

    @Override
    protected ObservableVault getObservebleVault() {
        return getGertHerbApplication().getObservableVault();
    }

    private GertHerbApplication getGertHerbApplication() {
        return (GertHerbApplication) getApplication();
    }

    @Override
    public int getResumableId() {
        return classOf(this).getName().hashCode();
    }
}
