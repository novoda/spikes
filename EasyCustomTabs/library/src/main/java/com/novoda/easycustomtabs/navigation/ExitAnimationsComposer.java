package com.novoda.easycustomtabs.navigation;

import android.content.Context;
import android.support.annotation.AnimRes;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;

class ExitAnimationsComposer implements Composer {

    private final Context context;
    @AnimRes
    private final int enterAnimationResId;
    @AnimRes
    private final int exitAnimationResId;

    public ExitAnimationsComposer(@NonNull Context context, @AnimRes int enterAnimationResId, @AnimRes int exitAnimationResId) {
        this.context = context;
        this.enterAnimationResId = enterAnimationResId;
        this.exitAnimationResId = exitAnimationResId;
    }

    @Override
    public CustomTabsIntent.Builder compose(CustomTabsIntent.Builder builder) {
        return builder.setExitAnimations(context, enterAnimationResId, exitAnimationResId);
    }

}
