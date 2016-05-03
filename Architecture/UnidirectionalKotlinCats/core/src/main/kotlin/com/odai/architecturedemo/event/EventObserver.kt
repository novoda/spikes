package com.odai.architecturedemo.event

import rx.Observer

abstract class EventObserver<T>: Observer<Event<T>> {

    override fun onError(p0: Throwable?) {
        throw UnsupportedOperationException("Error on event pipeline. This should never happen", p0)
    }

    override fun onCompleted() {
        throw UnsupportedOperationException("Completion on event pipeline. This should never happen")
    }

    override fun onNext(p0: Event<T>) {
        when (p0.status) {
            Status.LOADING -> onLoading(p0)
            Status.IDLE -> onIdle(p0)
            Status.ERROR -> onError(p0)
        }
    }

    abstract fun onLoading(event: Event<T>);

    abstract fun onIdle(event: Event<T>);

    abstract fun onError(event: Event<T>);
}
