package com.novoda.accessibility;

import android.support.annotation.IdRes;
import android.support.annotation.StringRes;

public class Action {

    @IdRes
    private final int id;

    @StringRes
    private final int label;

    private final Runnable action;

    public Action(@IdRes int id, @StringRes int label, Runnable action) {
        this.id = id;
        this.label = label;
        this.action = action;
    }

    @IdRes
    public int getId() {
        return id;
    }

    @StringRes
    public int getLabel() {
        return label;
    }

    public void run() {
        action.run();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Action otherAction = (Action) o;

        if (id != otherAction.id) {
            return false;
        }
        if (label != otherAction.label) {
            return false;
        }
        return this.action.equals(otherAction.action);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + label;
        result = 31 * result + action.hashCode();
        return result;
    }

}
