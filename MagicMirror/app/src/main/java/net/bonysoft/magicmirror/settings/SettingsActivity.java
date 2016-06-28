package net.bonysoft.magicmirror.settings;

import android.preference.PreferenceActivity;

import java.util.Arrays;
import java.util.List;

import net.bonysoft.magicmirror.R;

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
