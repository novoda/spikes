package com.novoda.wallpaper.droidcon;

import android.content.Intent;
import android.content.IntentFilter;

public class NextTimeOfDayLondonParallaxWallpaper extends LondonParallaxWallpaper {

    private TimeOfDayAssetRefresher refresher;

    @Override
    public void onCreate() {
        super.onCreate();

        NextTimeOfDayCalculator calculator = new NextTimeOfDayCalculator();
        refresher = new TimeOfDayAssetRefresher(new LondonParallaxWallpaperRenderer(getAssets()), calculator);
        registerReceiver(refresher, new IntentFilter(Intent.ACTION_SCREEN_ON));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(refresher);
    }
}
