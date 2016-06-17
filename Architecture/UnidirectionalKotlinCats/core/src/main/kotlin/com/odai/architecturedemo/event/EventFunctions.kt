package com.odai.architecturedemo.event

import com.jakewharton.rxrelay.BehaviorRelay
import rx.Notification
import rx.Observable

fun <T> asEvent() = Observable.Transformer<T, Event<T>> { p0 ->
    p0.materialize()
            .scan(Event<T>(Status.LOADING, null, null)) { event, notification ->
                when (notification.kind) {
                    Notification.Kind.OnNext -> Event(Status.LOADING, notification.value, null)
                    Notification.Kind.OnCompleted -> Event(Status.IDLE, event.data, null)
                    Notification.Kind.OnError -> Event(Status.ERROR, event.data, notification.throwable)
                    null -> event
                }
            }.startWith(Event<T>(Status.LOADING, null, null))
}

fun <T> asData() = Observable.Transformer<Event<T>, T> { p0 ->
    p0.filter { it.data != null }
            .map { it.data }
            .distinctUntilChanged()
}

fun <T> isInitialised(subject: BehaviorRelay<Event<T>>) = subject.value.data != null || subject.value.status == Status.LOADING
