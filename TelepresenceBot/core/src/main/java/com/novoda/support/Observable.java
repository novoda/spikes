package com.novoda.support;

import java.util.HashSet;
import java.util.Set;

public abstract class Observable<T> {

    private final Set<Observer<T>> observers;

    public static <T> Observable<T> just(T toEmit) {
        return new SingleEmissionObservable<>(toEmit);
    }

    protected Observable() {
        observers = new HashSet<>();
    }

    public abstract Observable<T> start();

    public synchronized Observable<T> attach(Observer<T> observer) {
        if (observer == null) {
            throw new NullPointerException("You cannot attach a null observer");
        }

        if (!observers.contains(observer)) {
            observers.add(observer);
        }

        return this;
    }

    synchronized void detach(Observer observer) {
        observers.remove(observer);
    }

    protected synchronized void notifyOf(T newValue) {
        for (Observer<T> observer : observers) {
            observer.update(newValue);
        }
    }

    public synchronized void detachObservers() {
        observers.clear();
    }

    public static void unsubscribe(Observable observable) {
        if (observable != null) {
            observable.detachObservers();
        }
    }

    private static class SingleEmissionObservable<T> extends Observable<T> {

        private final T toEmit;

        private SingleEmissionObservable(T toEmit) {
            this.toEmit = toEmit;
        }

        @Override
        public Observable<T> start() {
            notifyOf(toEmit);
            return this;
        }

    }

}
