package com.novoda.event;

import rx.Observer;

public abstract class EventObserver<T> implements Observer<Event<T>> {

    @Override
    public void onNext(Event<T> event) {
        switch (event.state()) {
            case IDLE:
                onIdle(event);
                break;
            case LOADING:
                onLoading(event);
                break;
            case ERROR:
                onError(event);
                break;
            default:
                break;
        }
    }

    @Override
    public void onError(Throwable e) {
        throw new IllegalStateException("Event Pipeline failed, this should never happen", e);
    }

    @Override
    public void onCompleted() {
        throw new IllegalStateException("Event Pipeline completed, this should never happen");
    }

    public abstract void onLoading(Event<T> event);

    public abstract void onIdle(Event<T> event);

    public abstract void onError(Event<T> event);

}
