package com.novoda.espresso;

import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

public class ViewTestRule<T extends View> extends ActivityTestRule<ViewActivity> {

    @LayoutRes
    private final int id;

    public ViewTestRule(@LayoutRes int id) {
        super(ViewActivity.class);
        this.id = id;
    }

    @Override
    protected Intent getActivityIntent() {
        Intent intent = super.getActivityIntent();
        intent.putExtra(ViewActivity.EXTRA_LAYOUT_ID, id);
        return intent;
    }

    public void runOnUiThread(final UiThreadAction<T> uiThreadAction) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                T view = getView();
                uiThreadAction.run(view);
            }
        });
    }

    public T getView() {
        return (T) getActivity().getView();
    }

    public interface UiThreadAction<T> {

        void run(T view);

    }

}
