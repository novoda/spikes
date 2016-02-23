package com.novoda.accessibility;

import android.support.annotation.IdRes;
import android.support.annotation.Nullable;

import java.util.Iterator;
import java.util.List;

public class Actions implements Iterable<Action> {

    private final List<Action> actions;

    public Actions(List<Action> actions) {
        this.actions = actions;
    }

    public int getCount() {
        return actions.size();
    }

    public Action getAction(int position) {
        return actions.get(position);
    }

    @Nullable
    public Action findActionById(@IdRes int id) {
        for (Action actionItem : actions) {
            if (actionItem.getId() == id) {
                return actionItem;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Actions otherActions = (Actions) o;
        return actions.equals(otherActions.actions);
    }

    @Override
    public int hashCode() {
        return actions.hashCode();
    }

    @Override
    public Iterator<Action> iterator() {
        return actions.iterator();
    }

}
