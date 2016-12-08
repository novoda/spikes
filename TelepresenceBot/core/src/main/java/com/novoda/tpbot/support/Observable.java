package com.novoda.tpbot.support;

import java.util.ArrayList;

public abstract class Observable<T> {

    private boolean hasChanged = false;
    private final ArrayList<Observer<T>> observers;

    protected Observable() {
        observers = new ArrayList<>();
    }

    public synchronized Observable<T> attach(Observer<T> observer) {
        if (observer == null) {
            throw new IllegalArgumentException("You cannot attach a null observer");
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

    protected synchronized void detachObservers() {
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

}
