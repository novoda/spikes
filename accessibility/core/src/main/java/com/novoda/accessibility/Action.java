package com.novoda.accessibility;

import android.support.annotation.IdRes;
import android.support.annotation.StringRes;

public class Action {

    @IdRes
    private final int id;

    @StringRes
    private final int labelRes;

    private final Runnable action;

    public Action(@IdRes int id, @StringRes int label, Runnable action) {
        this.id = id;
        this.labelRes = label;
        this.action = action;
    }

    @IdRes
    public int getId() {
        return id;
    }

    @StringRes
    public int getLabel() {
        return labelRes;
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

        Action action = (Action) o;

        if (id != action.id) {
            return false;
        }
        if (labelRes != action.labelRes) {
            return false;
        }
        return this.action.equals(action.action);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + labelRes;
        result = 31 * result + action.hashCode();
        return result;
    }

}
