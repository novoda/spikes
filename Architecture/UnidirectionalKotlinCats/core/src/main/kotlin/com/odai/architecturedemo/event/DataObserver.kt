package com.odai.architecturedemo.event

import rx.Observer

interface  DataObserver<T>: Observer<T> {

    override fun onError(p0: Throwable?) {
        throw UnsupportedOperationException("Error on event pipeline. This should never happen", p0)
    }

    override fun onCompleted() {
        throw UnsupportedOperationException("Completion on event pipeline. This should never happen")
    }

}
