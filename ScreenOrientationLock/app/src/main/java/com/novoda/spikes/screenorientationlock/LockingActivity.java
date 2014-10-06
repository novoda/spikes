package com.novoda.spikes.screenorientationlock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LockingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locking);
    }

    public void navigateToPostLocking(View view) {
        startActivity(new Intent(this, PostLockingActivity.class));
    }

}
