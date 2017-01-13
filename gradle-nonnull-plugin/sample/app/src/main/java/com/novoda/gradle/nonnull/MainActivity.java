package com.novoda.gradle.nonnull;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // this should result in a warning in the IDEA
        setValue(null);
    }

    private void setValue(Object value){
        // no-op
    }
}
