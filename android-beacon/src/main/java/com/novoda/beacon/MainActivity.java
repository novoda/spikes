package com.novoda.beacon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final int RESOLVE_PERMISSIONS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startMessageService();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESOLVE_PERMISSIONS && resultCode == RESULT_OK) {
            startMessageService();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void startMessageService() {
        startService(new Intent(MainActivity.this, MessageService.class));
    }
}
