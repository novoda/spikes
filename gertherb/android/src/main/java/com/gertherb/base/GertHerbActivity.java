package com.gertherb.base;

import android.app.Activity;
import android.os.Bundle;

import com.novoda.notils.logger.toast.Toaster;

public abstract class GertHerbActivity extends Activity {

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

}
