package com.novoda.toggletalkback;

import android.app.Activity;
import android.content.Intent;
import android.os.SystemClock;
import android.support.test.rule.ActivityTestRule;

import static com.novoda.toggletalkback.TalkBackStateSettingActivity.ACTION_DISABLE_TALKBACK;
import static com.novoda.toggletalkback.TalkBackStateSettingActivity.ACTION_ENABLE_TALKBACK;

public class TalkBackActivityTestRule<T extends Activity> extends ActivityTestRule<T> {

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
        String action = enable ? ACTION_ENABLE_TALKBACK : ACTION_DISABLE_TALKBACK;
        getActivity().startActivity(new Intent(action));
        sleepToAllowTalkBackServiceToChangeState();
    }

    private void sleepToAllowTalkBackServiceToChangeState() {
        SystemClock.sleep(1500);
    }
}
