package com.novoda.todoapp.task.newtask.displayer;

public interface NewTaskDisplayer {
    void attach(NewTaskActionListener newTaskActionListener);

    void detach(NewTaskActionListener newTaskActionListener);

    void showEmptyTaskError();
}
