package com.novoda.todoapp.task.displayer;

import com.novoda.todoapp.task.data.model.Task;

public interface TaskActionListener {

    void toggleCompletion(Task task);

    void onEditSelected(Task task);

}
