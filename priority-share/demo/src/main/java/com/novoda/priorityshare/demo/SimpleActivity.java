package com.novoda.priorityshare.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.novoda.priorityshare.PrioritySharer;

public class SimpleActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);

        findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShareDialog();
            }
        });
    }

    private void showShareDialog() {
        EditText text = (EditText) findViewById(R.id.text);

        new PrioritySharer.Builder()
                .setText(text.getText().toString())
                .show(this);
    }

}
