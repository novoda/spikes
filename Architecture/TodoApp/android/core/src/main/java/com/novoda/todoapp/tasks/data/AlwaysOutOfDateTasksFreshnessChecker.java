package com.novoda.todoapp.tasks.data;

import com.novoda.todoapp.tasks.data.model.Tasks;

public class AlwaysOutOfDateTasksFreshnessChecker implements TasksDataFreshnessChecker {

    @Override
    public boolean isFresh(Tasks tasks) {
        return false;
    }

}
