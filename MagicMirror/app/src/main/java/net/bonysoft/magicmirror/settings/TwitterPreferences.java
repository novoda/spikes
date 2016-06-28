package net.bonysoft.magicmirror.settings;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import net.bonysoft.magicmirror.R;

public class TwitterPreferences extends PreferenceFragment {

    private static final String DEFAULT_QUERY = "#droidconde";

    private EditTextPreference filterPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.twitter_preference);

        String filterPrefKey = getString(R.string.preference_key_tweet_filter);
        filterPreference = (EditTextPreference) findPreference(filterPrefKey);
        filterPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                updateFilterSummary((String) newValue);
                return true;
            }
        });
        updateFilterSummary(getFilterFromPreferences());
    }

    private void updateFilterSummary(String value) {
        String staticSummary = getString(R.string.preference_twitter_filter_summary);
        filterPreference.setSummary(staticSummary + '\n' + value);
    }

    private String getFilterFromPreferences() {
        String preferencesKey = getString(R.string.preference_key_tweet_filter);

        return  getPreferenceManager().getSharedPreferences().getString(preferencesKey, DEFAULT_QUERY);
    }
}
