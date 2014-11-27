package com.novoda.priorityshare;

import android.content.Intent;

/**
 * A simple listener that performs a no-op transformation on the intent.
 */
public class SimpleOnPrepareSharingIntentListener implements OnPrepareSharingIntentListener {

    @Override
    public Intent onPrepareSharingIntent(Intent intent) {
        return intent;
    }

}
