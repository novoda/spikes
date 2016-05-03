package com.novoda.event;

import rx.Observer;

public abstract class DataObserver<T> implements Observer<T> {

    @Override
    public void onError(Throwable e) {
        throw new IllegalStateException("Data Pipeline failed, this should never happen", e);
    }

    @Override
    public void onCompleted() {
        throw new IllegalStateException("Data Pipeline completed, this should never happen");
    }

}
