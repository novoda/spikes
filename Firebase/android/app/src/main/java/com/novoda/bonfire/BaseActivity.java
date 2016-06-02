package com.novoda.bonfire;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.novoda.notils.logger.simple.Log;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.setShowLogs(BuildConfig.DEBUG);
        Dependencies.INSTANCE.init(this);
    }
}
