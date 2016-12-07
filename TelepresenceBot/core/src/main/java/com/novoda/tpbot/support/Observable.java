package com.novoda.tpbot.support;

import java.util.ArrayList;

public abstract class Observable<T> {

    private boolean changed = false;
    private final ArrayList<Observer<T>> observers;

    public Observable() {
        observers = new ArrayList<>();
    }

    public synchronized Observable<T> addObserver(Observer<T> o) {
        if (o == null) {
            throw new IllegalArgumentException("Did you forget to add an observer for this observable?");
        }
        if (!observers.contains(o)) {
            observers.add(o);
        }
        start();
        return this;
    }

    public synchronized void deleteObserver(Observer o) {
        observers.remove(o);
    }

    public void notifyObservers(T arg) {
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

    public synchronized boolean hasChanged() {
        return changed;
    }

    public synchronized void deleteObservers() {
        observers.clear();
    }

    public synchronized int countObservers() {
        return observers.size();
    }

    private synchronized void setChanged() {
        changed = true;
    }

    private synchronized void clearChanged() {
        changed = false;
    }

    private synchronized boolean hasNotChanged() {
        return !changed;
    }

    public abstract void start();

}
