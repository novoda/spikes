package com.novoda.todoapp.tasks.displayer;

import com.novoda.todoapp.tasks.data.model.Tasks;

public interface TasksDisplayer {

    void attach(TasksActionListener tasksActionListener);

    void detach(TasksActionListener tasksActionListener);

    void display(Tasks tasks);

}
