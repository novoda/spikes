package com.novoda.todoapp.task.edit.displayer;

import com.novoda.data.SyncedData;
import com.novoda.todoapp.task.data.model.Task;

public interface EditTaskDisplayer {

    void attach(EditTaskActionListener editTaskActionListener);

    void detach(EditTaskActionListener editTaskActionListener);

    void display(SyncedData<Task> syncedData);

    void showEmptyTaskError();
}
