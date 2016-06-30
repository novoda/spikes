package com.novoda.todoapp.task.newtask.presenter;

import com.google.common.base.Optional;
import com.novoda.todoapp.navigation.Navigator;
import com.novoda.todoapp.task.data.model.Id;
import com.novoda.todoapp.task.data.model.Task;
import com.novoda.todoapp.task.newtask.displayer.NewTaskActionListener;
import com.novoda.todoapp.task.newtask.displayer.NewTaskDisplayer;
import com.novoda.todoapp.tasks.service.TasksService;

import java.util.UUID;

public class NewTaskPresenter {

    private final TasksService tasksService;
    private final NewTaskDisplayer taskDisplayer;
    private final Navigator navigator;

    public NewTaskPresenter(TasksService tasksService, NewTaskDisplayer taskDisplayer, Navigator navigator) {
        this.tasksService = tasksService;
        this.taskDisplayer = taskDisplayer;
        this.navigator = navigator;
    }

    public void startPresenting() {
        taskDisplayer.attach(editTaskActionListener);
    }

    public void stopPresenting() {
        taskDisplayer.detach(editTaskActionListener);
    }

    final NewTaskActionListener editTaskActionListener = new NewTaskActionListener() {
        @Override
        public void save(Optional<String> title, Optional<String> description) {
            if (title.or(description).isPresent()) {
                Task task = Task.builder()
                        .id(Id.from(UUID.randomUUID().toString()))
                        .title(title)
                        .description(description)
                        .build();
                tasksService.save(task).call();
                navigator.back();
            } else {
                taskDisplayer.showEmptyTaskError();
            }
        }
    };
}
