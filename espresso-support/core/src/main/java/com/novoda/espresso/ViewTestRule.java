package com.novoda.espresso;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

public class ViewTestRule<T extends View> extends ActivityTestRule<ViewActivity> {

    private final Instrumentation instrumentation;

    @LayoutRes
    private final int id;

    public ViewTestRule(@LayoutRes int id) {
        super(ViewActivity.class);
        this.id = id;
        instrumentation = InstrumentationRegistry.getInstrumentation();
    }

    @Override
    protected Intent getActivityIntent() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.putExtra(ViewActivity.EXTRA_LAYOUT_ID, id);
        return intent;
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
        return (T) getActivity().getView();
    }

    public interface Runner<T> {

        void run(T view);

    }

}
