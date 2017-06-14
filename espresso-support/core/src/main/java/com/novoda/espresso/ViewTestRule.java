package com.novoda.espresso;

import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.test.espresso.IdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

public class ViewTestRule<T extends View> extends ActivityTestRule<ViewActivity> implements IdlingResource {

    @LayoutRes
    private final int id;

    private boolean isIdle = true;
    private ResourceCallback callback;

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

    public void bindViewUsing(final Binder<T> binder) {
        isIdle = false;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                T view = getView();
                binder.bind(view);
                callback.onTransitionToIdle();
                isIdle = true;
            }
        });
    }

    public T getView() {
        return (T) getActivity().getView();
    }

    @Override
    public String getName() {
        return ViewTestRule.class.getSimpleName() + "::IdlingResource";
    }

    @Override
    public boolean isIdleNow() {
        return isIdle;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.callback = callback;
    }

    public interface Binder<T> {

        void bind(T view);

    }

}
