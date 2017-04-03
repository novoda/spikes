package com.novoda.espresso;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;

import static com.novoda.espresso.TalkBackStateSettingActivity.ACTION_DISABLE_TALKBACK;
import static com.novoda.espresso.TalkBackStateSettingActivity.ACTION_ENABLE_TALKBACK;

/**
 * Send intents to TalkBackStateSettingActivity to start and stop TalkBack.
 *
 * By default, includes a delay after sending the intent to allow the state to change. This is customizable
 * via the constructor {@link TalkBackStateSettingRequester#TalkBackStateSettingRequester(long, Context)}.
 */
public class TalkBackStateSettingRequester {

    private static final int DEFAULT_DELAY_MILLIS = 1500;

    private final long delayMillis;
    private final Context context;

    public TalkBackStateSettingRequester() {
        this(DEFAULT_DELAY_MILLIS, InstrumentationRegistry.getTargetContext());
    }

    /**
     * @param delayMillis time to sleep after toggling TalkBack state
     * @param context
     */
    public TalkBackStateSettingRequester(long delayMillis, Context context) {
        this.delayMillis = delayMillis;
        this.context = context;
    }

    /**
     * Send intent to start TalkBack.
     */
    public void requestEnableTalkBack() {
        sendIntent(ACTION_ENABLE_TALKBACK);
    }

    /**
     * Send intent to stop TalkBack.
     */
    public void requestDisableTalkBack() {
        sendIntent(ACTION_DISABLE_TALKBACK);
    }

    private void sendIntent(String action) {
        Intent intent = new Intent(action);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        sleepToAllowTalkBackServiceToChangeState();
    }

    private void sleepToAllowTalkBackServiceToChangeState() {
        SystemClock.sleep(delayMillis);
    }

}
