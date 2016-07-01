package com.novoda.todoapp.task;

import com.google.common.base.Optional;
import com.novoda.todoapp.navigation.Navigator;
import com.novoda.todoapp.task.data.model.Task;
import com.novoda.todoapp.task.presenter.IdGenerator;
import com.novoda.todoapp.tasks.service.TasksService;

public class TaskActionListener {
    private final IdGenerator idGenerator;
    private final TasksService tasksService;
    private final Navigator navigator;
    private final TaskActionDisplayer taskDisplayer;

    public TaskActionListener(TasksService tasksService, TaskActionDisplayer taskDisplayer, Navigator navigator, IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
        this.tasksService = tasksService;
        this.navigator = navigator;
        this.taskDisplayer = taskDisplayer;
    }

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
}
