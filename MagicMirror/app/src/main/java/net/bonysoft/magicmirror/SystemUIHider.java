package net.bonysoft.magicmirror;

import android.os.Build;
import android.view.View;

public final class SystemUIHider {

    private final View view;

    public SystemUIHider(View view) {
        this.view = view;
    }

    public void hideSystemUi() {
            view.setSystemUiVisibility(getFlags());
        view.invalidate();
    }

    private int getFlags() {
        int fullScreenFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            fullScreenFlags = fullScreenFlags | View.SYSTEM_UI_FLAG_IMMERSIVE;
        }
        return fullScreenFlags;
    }

    public void showSystemUi() {
        view.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
        view.invalidate();
    }

}
