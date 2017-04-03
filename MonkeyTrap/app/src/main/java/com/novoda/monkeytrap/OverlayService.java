package com.novoda.monkeytrap;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.view.WindowManager;

import static android.view.View.VISIBLE;

public class OverlayService extends Service {

    private static final String ACTION = "com.novoda.monkeytrap.SHOW_OVERLAY";
    private static final String EXTRA_SHOW = "show";

    private static OverlayService instance;

    private WindowManager windowManager;
    private OverlayView overlayView;

    static Intent show() {
        return createIntent(true);
    }

    static Intent hide() {
        return createIntent(false);
    }

    private static Intent createIntent(boolean show) {
        return new Intent(ACTION)
                .setPackage(BuildConfig.APPLICATION_ID)
                .putExtra(EXTRA_SHOW, show);
    }

    static boolean isOverlayShowing() {
        return instance != null
                && instance.overlayView != null
                && instance.overlayView.getVisibility() == VISIBLE;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Not used
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (shouldShow(intent)) {
            addOverlay();
        } else {
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    private boolean shouldShow(Intent intent) {
        return intent.getBooleanExtra(EXTRA_SHOW, true);
    }

    private void addOverlay() {
        overlayView = new OverlayView(this);
        overlayView.setBackgroundColor(Color.parseColor("#30A4D9"));
        overlayView.setImageResource(R.drawable.novoda);
        windowManager.addView(overlayView, OverlayView.createLayoutParams(getResources()));
    }

    @Override
    public void onDestroy() {
        instance = null;
        if (overlayView != null) {
            windowManager.removeView(overlayView);
        }
        super.onDestroy();
    }

}
