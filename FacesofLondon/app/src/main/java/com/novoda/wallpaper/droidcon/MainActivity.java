package com.novoda.wallpaper.droidcon;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startSetLiveWallpaperIntent();
        finish();
    }

    private void startSetLiveWallpaperIntent() {
        Intent intent = new Intent();
        intent.setAction(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        ComponentName wallpaper = new ComponentName(getPackageName(), LondonParallaxWallpaper.class.getCanonicalName());
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, wallpaper);
        startActivityForResult(intent, 0);
    }
}
