package com.novoda.tpbot.support;

import java.util.ArrayList;

public abstract class Observable<T> {

    private boolean hasChanged = false;
    private final ArrayList<Observer<T>> observers;

    public static <T> SingleEmissionObservable<T> just(T toEmit) {
        return new SingleEmissionObservable<>(toEmit);
    }

    protected Observable() {
        observers = new ArrayList<>();
    }

    public synchronized Observable<T> attach(Observer<T> observer) {
        if (observer == null) {
            throw new NullPointerException("You cannot attach a null observer");
        }

        if (!observers.contains(observer)) {
            observers.add(observer);
        }

        return this;
    }

    public synchronized void detach(Observer observer) {
        observers.remove(observer);
    }

    protected synchronized void notifyOf(T newValue) {
        for (int i = observers.size() - 1; i >= 0; i--) {
            observers.get(i).update(newValue);
        }
    }

    public synchronized void detachObservers() {
        observers.clear();
    }

    protected synchronized void setChanged() {
        hasChanged = true;
    }

    private synchronized void clearChanged() {
        hasChanged = false;
    }

    private synchronized boolean hasNotChanged() {
        return !hasChanged;
    }

    public abstract Observable<T> start();

    public static class SingleEmissionObservable<T> extends Observable<T> {

        private final T toEmit;

        SingleEmissionObservable(T toEmit) {
            this.toEmit = toEmit;
        }

        @Override
        public Observable<T> start() {
            notifyOf(toEmit);
            return this;
        }

    }

}
