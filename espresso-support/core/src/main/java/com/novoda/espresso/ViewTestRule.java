package com.novoda.espresso;

import android.app.Instrumentation;
import android.support.annotation.LayoutRes;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ViewTestRule<T extends View> extends ActivityTestRule<ViewActivity> {

    private final Instrumentation instrumentation;

    @LayoutRes
    private final int id;
    private T view;

    public ViewTestRule(@LayoutRes int id) {
        super(ViewActivity.class);
        this.id = id;
        instrumentation = InstrumentationRegistry.getInstrumentation();
    }

    @Override
    protected void afterActivityLaunched() {
        instrumentation.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                launchView();
            }
        });
    }

    protected void launchView() {
        final ViewActivity activity = getActivity();
        final ViewGroup parent = (ViewGroup) activity.findViewById(android.R.id.content);
        view = createView(activity.getLayoutInflater(), parent);
        activity.setContentView(view);
    }

    protected T createView(final LayoutInflater inflater, ViewGroup parent) {
        return (T) inflater.inflate(id, parent, false);
    }

    public void runOnMainSynchronously(final Runner<T> runner) {
        instrumentation.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                T view = getView();
                runner.run(view);
            }
        });
    }

    public T getView() {
        return view;
    }

    public interface Runner<T> {

        void run(T view);

    }

}
