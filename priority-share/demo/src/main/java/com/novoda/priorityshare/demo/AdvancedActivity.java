package com.novoda.priorityshare.demo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import com.novoda.priorityshare.OnPrepareSharingIntentListener;
import com.novoda.priorityshare.PrioritySharer;
import com.novoda.priorityshare.TargetApps;

public class AdvancedActivity extends Activity {

    private static final TargetApps[] TARGET_APPS = {
            TargetApps.DEFAULTS,
            TargetApps.FACEBOOK,
            TargetApps.TWITTER,
            TargetApps.GOOGLE_PLUS,
            TargetApps.NONE
    };

    private Spinner targetApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced);
        findViewById(R.id.share).setOnClickListener(shareClickListener);
        populateTargetApps();
    }

    private final View.OnClickListener shareClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showShareDialog();
        }
    };

    private void populateTargetApps() {
        targetApps = (Spinner) findViewById(R.id.target_apps);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.target_apps, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        targetApps.setAdapter(adapter);
    }

    private void showShareDialog() {
        EditText text = (EditText) findViewById(R.id.text);
        EditText title = (EditText) findViewById(R.id.title);
        EditText mimeType = (EditText) findViewById(R.id.mime_type);
        EditText dataUri = (EditText) findViewById(R.id.data_uri);
        Switch showMostRecentlyUsed = (Switch) findViewById(R.id.show_most_recently_used);

        new PrioritySharer.Builder()
                .setTargets(TARGET_APPS[targetApps.getSelectedItemPosition()])
                .setText(text.getText().toString())
                .setTitle(title.getText().toString())
                .setMimeType(mimeType.getText().toString())
                .showMostRecentlyUsed(showMostRecentlyUsed.isChecked())
                .setDataUri(getDataUriOrNull(dataUri))
                .setOnPrepareSharingIntentListener(listener)
                .show(this);
    }

    private Uri getDataUriOrNull(EditText dataUri) {
        String uriString = dataUri.getText().toString();
        return TextUtils.isEmpty(uriString) ? null : Uri.parse(uriString);
    }

    private final OnPrepareSharingIntentListener listener = new OnPrepareSharingIntentListener() {
        @Override
        public Intent onPrepareSharingIntent(Intent intent) {
            // You can modify the Intent here...
            Log.i("Priority Share Demo", "onPrepareSharingIntent - Intent: " + intent);
            return intent;
        }
    };

}
