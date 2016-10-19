package com.novoda.wallpaper.droidcon;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.StringRes;

public class DebugScreenFragment extends PreferenceFragment {

    private PhaseMethodToggler phaseMethodToggler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        phaseMethodToggler = new PhaseMethodToggler(getActivity(), getActivity().getPackageManager());
        addPreferencesFromResource(R.xml.preferences_debug);

        findPreference(R.string.key_debug_random_phases).setOnPreferenceChangeListener(onPhaseMethodChangeListener());
    }

    private Preference.OnPreferenceChangeListener onPhaseMethodChangeListener() {
        return new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (((Boolean) newValue)) {
                    phaseMethodToggler.toggleInSequence();
                } else {
                    phaseMethodToggler.toggleInProgressOfDay();
                }
                return true;
            }
        };
    }

    private Preference findPreference(@StringRes int preferenceKey) {
        return findPreference(getString(preferenceKey));
    }
}
