package com.novoda.todoapp.task.newtask.presenter;

import com.google.common.base.Optional;
import com.novoda.todoapp.navigation.Navigator;
import com.novoda.todoapp.task.data.model.Task;
import com.novoda.todoapp.task.newtask.displayer.NewTaskActionListener;
import com.novoda.todoapp.task.newtask.displayer.NewTaskDisplayer;
import com.novoda.todoapp.tasks.service.TasksService;

public class NewTaskPresenter {

    private final TasksService tasksService;
    private final NewTaskDisplayer taskDisplayer;
    private final Navigator navigator;
    private final IdGenerator idGenerator;

    public NewTaskPresenter(TasksService tasksService, NewTaskDisplayer taskDisplayer, Navigator navigator, IdGenerator idGenerator) {
        this.tasksService = tasksService;
        this.taskDisplayer = taskDisplayer;
        this.navigator = navigator;
        this.idGenerator = idGenerator;
    }

    public void startPresenting() {
        taskDisplayer.attach(newTaskActionListener);
    }

    public void stopPresenting() {
        taskDisplayer.detach(newTaskActionListener);
    }

    final NewTaskActionListener newTaskActionListener = new NewTaskActionListener() {
        @Override
        public void save(Optional<String> title, Optional<String> description) {
            if (title.or(description).isPresent()) {
                Task task = Task.builder()
                        .id(idGenerator.generate())
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
