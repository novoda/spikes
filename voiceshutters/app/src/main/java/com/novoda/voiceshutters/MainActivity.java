package com.novoda.voiceshutters;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
    }

    public void onSelectClient(View v) {
        Intent intent = new Intent(this, ClientActivity.class);
        startActivity(intent);
    }

    public void onSelectServer(View v) {
        Intent intent = new Intent(this, ServerActivity.class);
        startActivity(intent);
    }
}
