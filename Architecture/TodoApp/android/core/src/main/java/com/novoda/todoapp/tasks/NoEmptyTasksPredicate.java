package com.novoda.todoapp.tasks;

import com.google.common.base.Predicate;
import com.novoda.todoapp.tasks.data.model.Tasks;

public class NoEmptyTasksPredicate implements Predicate<Tasks> {

    @Override
    public boolean apply(Tasks input) {
        return !input.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o != null && getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
        return 0;
    }

}
