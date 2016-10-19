package com.novoda.wallpaper.droidcon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

class TimeOfDayAssetRefresher extends BroadcastReceiver {

    private final Calculator calculator;
    private final LondonParallaxWallpaperRenderer renderer;

    private TimeOfDay currentTime;

    TimeOfDayAssetRefresher(LondonParallaxWallpaperRenderer renderer, Calculator calculator) {
        this.renderer = renderer;
        this.calculator = calculator;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        TimeOfDay timeOfDay = calculator.currentTimeOfDay();
        if (currentTime != timeOfDay) {
            renderer.loadAssetsFor(timeOfDay);
            currentTime = timeOfDay;
        }
    }

}
