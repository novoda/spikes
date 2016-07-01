package com.novoda.todoapp.task.newtask.presenter;

import com.novoda.todoapp.navigation.Navigator;
import com.novoda.todoapp.task.TaskActionDisplayer;
import com.novoda.todoapp.task.TaskActionListener;
import com.novoda.todoapp.task.presenter.IdProducer;
import com.novoda.todoapp.tasks.service.TasksService;

public class NewTaskPresenter {

    private final TaskActionDisplayer taskDisplayer;

    final TaskActionListener taskActionListener;

    public NewTaskPresenter(TasksService tasksService, TaskActionDisplayer taskDisplayer, Navigator navigator, IdProducer idProducer) {
        this.taskDisplayer = taskDisplayer;
        taskActionListener = new TaskActionListener(tasksService, taskDisplayer, navigator, idProducer);
    }

    public void startPresenting() {
        taskDisplayer.attach(taskActionListener);
    }

    public void stopPresenting() {
        taskDisplayer.detach(taskActionListener);
    }
}
