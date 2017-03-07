package com.novoda.espresso;

import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.test.rule.ActivityTestRule;

public class ViewActivityRule<T> extends ActivityTestRule<ViewActivity> {

    @LayoutRes
    private final int id;

    public ViewActivityRule(@LayoutRes int id) {
        super(ViewActivity.class);
        this.id = id;
    }

    @Override
    protected Intent getActivityIntent() {
        Intent activityIntent = super.getActivityIntent();
        activityIntent.putExtra(ViewActivity.EXTRA_LAYOUT_ID, id);
        return activityIntent;
    }

    public void bindView(final Binder<T> binder) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                T view = getView();
                binder.bind(view);
            }
        });
    }

    private T getView() {
        return (T) getActivity().getView();
    }

    public interface Binder<T> {

        void bind(T view);

    }

}
