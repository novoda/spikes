package com.novoda.todoapp.tasks.data;

import com.novoda.todoapp.tasks.data.model.Tasks;

public interface TasksDataFreshnessChecker {

    public boolean isFresh(Tasks tasks);

}
