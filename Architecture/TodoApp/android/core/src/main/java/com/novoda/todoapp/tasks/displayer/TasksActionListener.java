package com.novoda.todoapp.tasks.displayer;

import com.novoda.todoapp.task.data.model.Task;

public interface TasksActionListener {

    void onTaskSelected(Task task);

    void toggleCompletion(Task task);

    void onRefreshSelected();

    void onClearCompletedSelected();

    void onFilterSelected(Filter filter);

    void onAddTaskSelected();

    void onStatisticsSelected();

    enum Filter {
        ALL,
        ACTIVE,
        COMPLETED
    }

}
