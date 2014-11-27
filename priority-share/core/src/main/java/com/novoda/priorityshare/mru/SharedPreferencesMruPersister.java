package com.novoda.priorityshare.mru;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesMruPersister implements MruPersister {

    static final String KEY_LAST_USED_TARGET = "last_used_target_package";

    private static final String MRU_PREFERENCES = "priority_share_mru";

    private final SharedPreferences preferences;

    public static SharedPreferencesMruPersister newInstance(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(MRU_PREFERENCES, Context.MODE_PRIVATE);
        return new SharedPreferencesMruPersister(preferences);
    }

    SharedPreferencesMruPersister(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public void storeLastUsedTarget(String packageName) {
        preferences.edit()
                .putString(KEY_LAST_USED_TARGET, packageName)
                .commit();
    }

    @Override
    public String getLastUsedTarget() {
        return preferences.getString(KEY_LAST_USED_TARGET, PACKAGE_NONE);
    }

    @Override
    public void resetLastUsedTarget() {
        preferences.edit()
                .remove(KEY_LAST_USED_TARGET)
                .commit();
    }

}
