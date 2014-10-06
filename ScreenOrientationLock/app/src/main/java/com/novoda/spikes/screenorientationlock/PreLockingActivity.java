package com.novoda.spikes.screenorientationlock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class PreLockingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_locking);
    }

    public void navigateToLocking(View view) {
        startActivity(new Intent(this, LockingActivity.class));
    }

}
