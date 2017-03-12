package com.novoda.espresso;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import static com.novoda.espresso.TalkBackStateSettingActivity.ACTION_DISABLE_TALKBACK;
import static com.novoda.espresso.TalkBackStateSettingActivity.ACTION_ENABLE_TALKBACK;

/**
 * Send intents to TalkBackStateSettingActivity to start and stop TalkBack.
 *
 * By default, includes a delay after sending the intent to allow the state to change. This is customizable
 * via the constructor {@link TalkBackStateSettingRequester#TalkBackStateSettingRequester(long)}.
 */
public class TalkBackStateSettingRequester {

    private static final int DEFAULT_DELAY_MILLIS = 1500;

    private final long delayMillis;

    public TalkBackStateSettingRequester() {
        this(DEFAULT_DELAY_MILLIS);
    }

    /**
     * @param delayMillis time to sleep after toggling TalkBack state
     */
    public TalkBackStateSettingRequester(long delayMillis) {
        this.delayMillis = delayMillis;
    }

    /**
     * Send intent to start TalkBack.
     */
    public void requestEnableTalkBack(Context context) {
        sendIntent(context, ACTION_ENABLE_TALKBACK);
    }

    /**
     * Send intent to stop TalkBack.
     */
    public void requestDisableTalkBack(Context context) {
        sendIntent(context, ACTION_DISABLE_TALKBACK);
    }

    private void sendIntent(Context context, String action) {
        context.startActivity(new Intent(action));
        sleepToAllowTalkBackServiceToChangeState();
    }

    private void sleepToAllowTalkBackServiceToChangeState() {
        SystemClock.sleep(delayMillis);
    }

}
