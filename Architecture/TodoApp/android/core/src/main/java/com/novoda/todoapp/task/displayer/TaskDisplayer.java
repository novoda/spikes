package com.novoda.todoapp.task.displayer;

import com.novoda.data.SyncedData;
import com.novoda.todoapp.task.data.model.Task;

public interface TaskDisplayer {

    void attach(TaskActionListener taskActionListener);

    void detach(TaskActionListener taskActionListener);

    void display(SyncedData<Task> syncedData);

}
