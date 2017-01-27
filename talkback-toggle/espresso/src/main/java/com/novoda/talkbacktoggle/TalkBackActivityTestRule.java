package com.novoda.talkbacktoggle;

import android.app.Activity;
import android.content.Intent;
import android.os.SystemClock;
import android.support.test.rule.ActivityTestRule;

public class TalkBackActivityTestRule<T extends Activity> extends ActivityTestRule<T> {

    private static final String ENABLE_TALKBACK = "com.novoda.talkbacktoggle.ENABLE_TALKBACK";
    private static final String DISABLE_TALKBACK = "com.novoda.talkbacktoggle.DISABLE_TALKBACK";

    public TalkBackActivityTestRule(Class<T> activityClass) {
        super(activityClass);
    }

    @Override
    protected void afterActivityLaunched() {
        super.afterActivityLaunched();
        enableTalkBack(true);
    }

    @Override
    protected void afterActivityFinished() {
        super.afterActivityFinished();
        enableTalkBack(false);
    }

    private void enableTalkBack(boolean enable) {
        String action = enable ? ENABLE_TALKBACK : DISABLE_TALKBACK;
        getActivity().startActivity(new Intent(action));
        sleepToAllowTalkBackServiceToChangeState();
    }

    private void sleepToAllowTalkBackServiceToChangeState() {
        SystemClock.sleep(1500);
    }
}
