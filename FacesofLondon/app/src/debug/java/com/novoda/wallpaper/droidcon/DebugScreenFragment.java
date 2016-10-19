package com.novoda.wallpaper.droidcon;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class DebugScreenFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_debug);
    }
}
