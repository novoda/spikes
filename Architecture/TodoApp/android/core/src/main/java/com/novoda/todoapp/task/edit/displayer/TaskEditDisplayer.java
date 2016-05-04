package com.novoda.todoapp.task.edit.displayer;

import com.novoda.data.SyncedData;
import com.novoda.todoapp.task.data.model.Task;

public interface TaskEditDisplayer {

    void attach(TaskEditActionListener taskEditActionListener);

    void detach(TaskEditActionListener taskEditActionListener);

    void display(SyncedData<Task> syncedData);

    void showEmptyTaskError();

}
