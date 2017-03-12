package com.novoda.espresso;

import android.support.annotation.LayoutRes;
import android.view.View;

public class TalkBackViewTestRule<T extends View> extends ViewTestRule<T> {

    private final TalkBackStateSettingRequester talkBackStateSettingRequester;

    public TalkBackViewTestRule(@LayoutRes int id) {
        super(id);
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
