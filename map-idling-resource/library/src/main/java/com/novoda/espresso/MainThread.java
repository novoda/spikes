package com.novoda.espresso;

import android.support.test.espresso.IdlingResource;

import com.google.android.gms.maps.SupportMapFragment;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

final class MainThread {

    static IdlingResource createOnMainThread(final SupportMapFragment mapFragment) {
        FutureTask<IdlingResource> task = new FutureTask<>(new Callable<IdlingResource>() {
            @Override
            public IdlingResource call() throws Exception {
                return new MapIdlingResource(mapFragment);
            }
        });
        getInstrumentation().runOnMainSync(task);
        try {
            return task.get();
        } catch (ExecutionException | InterruptedException e) {
            // Expose the original exception
            throw new RuntimeException(e.getCause());
        }
    }

    private MainThread() {
        //no instance
    }
}
