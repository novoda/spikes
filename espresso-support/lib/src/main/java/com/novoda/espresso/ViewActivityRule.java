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
        Intent intent = new Intent("com.novoda.espresso.SHOW_VIEW");
        intent.putExtra(ViewActivity.EXTRA_LAYOUT_ID, id);
        return intent;
    }

    public void bindViewUsing(final Binder<T> binder) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                T view = getView();
                binder.bind(view);
            }
        });
    }

    public T getView() {
        return (T) getActivity().getView();
    }

    public interface Binder<T> {

        void bind(T view);

    }

}
