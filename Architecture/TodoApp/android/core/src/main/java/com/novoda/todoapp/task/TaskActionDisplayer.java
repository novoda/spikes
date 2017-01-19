package com.novoda.todoapp.task;

public interface TaskActionDisplayer {
    void attach(TaskActionListener taskActionListener);

    void detach(TaskActionListener taskActionListener);

    void showEmptyTaskError();
}
