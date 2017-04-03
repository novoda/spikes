package com.novoda.espresso;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;

public class TalkBackActivityTestRule<T extends Activity> extends ActivityTestRule<T> {

    private final TalkBackStateSettingRequester talkBackStateSettingRequester;

    public TalkBackActivityTestRule(Class<T> activityClass) {
        super(activityClass);
        talkBackStateSettingRequester = new TalkBackStateSettingRequester();
    }

    @Override
    protected void beforeActivityLaunched() {
        super.beforeActivityLaunched();
        talkBackStateSettingRequester.requestEnableTalkBack();
    }

    @Override
    protected void afterActivityFinished() {
        super.afterActivityFinished();
        talkBackStateSettingRequester.requestDisableTalkBack();
    }

}
