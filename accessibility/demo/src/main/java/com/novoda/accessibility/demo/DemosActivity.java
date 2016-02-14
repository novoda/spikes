package com.novoda.accessibility.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.novoda.accessibility.demo.base.Demo;
import com.novoda.accessibility.demo.base.DemoAdapter;

import java.util.Arrays;
import java.util.List;

public class DemosActivity extends AppCompatActivity {

    private final List<Demo> demos = Arrays.asList(
            new Demo("Accessibility checker", AccessibilityCheckerActivity.class)
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demos);

        ListView listView = ((ListView) findViewById(R.id.list));
        listView.setAdapter(new DemoAdapter(demos));
    }

}
