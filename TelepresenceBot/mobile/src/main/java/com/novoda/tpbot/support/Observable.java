package com.novoda.tpbot.support;

import com.novoda.notils.exception.DeveloperError;

import java.util.ArrayList;

public abstract class Observable<T> {

    private boolean hasChanged = false;
    private final ArrayList<Observer<T>> observers;

    protected Observable() {
        observers = new ArrayList<>();
    }

    public synchronized Observable<T> attach(Observer<T> observer) {
        if (observer == null) {
            throw new DeveloperError("Did you forget to add an observer for this Observable?");
        }
        if (!observers.contains(observer)) {
            observers.add(observer);
        }

        return this;
    }

    public synchronized void detach(Observer observer) {
        observers.remove(observer);
    }

    protected void notify(T arg) {
        Observer<T>[] observersCopy;

        synchronized (this) {
            if (hasNotChanged()) {
                return;
            }

            observersCopy = observers.toArray(new Observer[observers.size()]);
            clearChanged();
        }

        for (int i = observersCopy.length - 1; i >= 0; i--) {
            observersCopy[i].update(arg);
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
