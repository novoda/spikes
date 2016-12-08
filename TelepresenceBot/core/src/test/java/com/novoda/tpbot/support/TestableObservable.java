package com.novoda.tpbot.support;

public class TestableObservable<T> extends Observable<T> {

    private final T toEmit;

    public static <T> TestableObservable<T> just(T toEmit) {
        return new TestableObservable<>(toEmit);
    }

    TestableObservable(T toEmit) {
        this.toEmit = toEmit;
    }

    @Override
    public Observable<T> start() {
        notifyOf(toEmit);
        return this;
    }
}
