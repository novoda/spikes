package com.novoda.talkbacktoggle;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;

public class TalkBackActivityTestRule<T extends Activity> extends ActivityTestRule<T> {

    private final TalkBackStateSettingRequester talkBackStateSettingRequester;

    public TalkBackActivityTestRule(Class<T> activityClass) {
        super(activityClass);
        talkBackStateSettingRequester = new TalkBackStateSettingRequester();
    }

    @Override
    protected void afterActivityLaunched() {
        super.afterActivityLaunched();
        talkBackStateSettingRequester.requestEnableTalkBack(getActivity());
    }

    @Override
    protected void afterActivityFinished() {
        super.afterActivityFinished();
        talkBackStateSettingRequester.requestDisableTalkBack(getActivity());
    }

}
