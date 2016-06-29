package com.novoda.magicmirror.settings;

import android.preference.PreferenceActivity;

import com.novoda.magicmirror.R;

import java.util.Arrays;
import java.util.List;


public class SettingsActivity extends PreferenceActivity {

    private static final List<String> PREFERENCE_FRAGMENTS = Arrays.asList(
            TwitterPreferences.class.getName()
    );

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return PREFERENCE_FRAGMENTS.contains(fragmentName);
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }

}
