package com.novoda.priorityshare.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.simple_demo).setOnClickListener(simpleDemoClickListener);
        findViewById(R.id.advanced_demo).setOnClickListener(advancedDemoClickListener);
    }

    private final View.OnClickListener simpleDemoClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(MainActivity.this, SimpleActivity.class));
        }
    };

    private final View.OnClickListener advancedDemoClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(MainActivity.this, AdvancedActivity.class));
        }
    };

}
