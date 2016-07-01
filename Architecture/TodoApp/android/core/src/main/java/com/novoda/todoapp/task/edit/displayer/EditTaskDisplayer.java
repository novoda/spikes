package com.novoda.todoapp.task.edit.displayer;

import com.novoda.data.SyncedData;
import com.novoda.todoapp.task.TaskActionDisplayer;
import com.novoda.todoapp.task.data.model.Task;

public interface EditTaskDisplayer extends TaskActionDisplayer {
    void display(SyncedData<Task> syncedData);
}
