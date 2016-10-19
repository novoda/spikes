package com.novoda.wallpaper.droidcon;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;

final class PhaseMethodToggler {

    private final PackageManager packageManager;
    private final Context context;

    PhaseMethodToggler(Context context, PackageManager packageManager) {
        this.context = context;
        this.packageManager = packageManager;
    }

    void toggleInSequence() {
        setNextTimeOfDayMethod(true);
        setTimeMethodEnabled(false);
    }

    void toggleInProgressOfDay() {
        setNextTimeOfDayMethod(false);
        setTimeMethodEnabled(true);
    }

    private void setTimeMethodEnabled(boolean enabled) {
        packageManager.setComponentEnabledSetting(
                new ComponentName(context, LondonParallaxWallpaper.class),
                enabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
        );
    }

    private void setNextTimeOfDayMethod(boolean enabled) {
        packageManager.setComponentEnabledSetting(
                new ComponentName(context, NextTimeOfDayLondonParallaxWallpaper.class),
                enabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
        );
    }
}
