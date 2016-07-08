package com.novoda.todoapp.navigation;

import com.novoda.todoapp.task.data.model.Task;

public interface Navigator {

    void toTaskDetail(Task task);

    void toEditTask(Task task);

    void back();

    void toAddTask();

    void toStatistics();

}
