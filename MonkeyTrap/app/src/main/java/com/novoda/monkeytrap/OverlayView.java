package com.novoda.monkeytrap;

import static android.graphics.PixelFormat.TRANSLUCENT;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
import static android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;

import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;

class OverlayView extends ImageView {

    private static final int SAFETY_MARGIN = 20;

    static WindowManager.LayoutParams createLayoutParams(Resources resources) {
        final WindowManager.LayoutParams params =
                new WindowManager.LayoutParams(MATCH_PARENT, retrieveStatusBarHeight(resources) + SAFETY_MARGIN,
                        TYPE_SYSTEM_ERROR, FLAG_NOT_FOCUSABLE | FLAG_LAYOUT_IN_SCREEN | FLAG_LAYOUT_NO_LIMITS
                        | FLAG_NOT_TOUCH_MODAL | FLAG_LAYOUT_INSET_DECOR, TRANSLUCENT);
        params.gravity = Gravity.TOP;
        return params;
    }


    private static int retrieveStatusBarHeight(Resources resources) {
        int result = 0;
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public OverlayView(Context context) {
        super(context);
    }

}
