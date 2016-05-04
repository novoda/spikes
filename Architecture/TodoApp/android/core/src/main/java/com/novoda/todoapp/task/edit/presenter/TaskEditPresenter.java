package com.novoda.todoapp.task.edit.presenter;

import com.google.common.base.Optional;
import com.novoda.data.SyncedData;
import com.novoda.event.DataObserver;
import com.novoda.todoapp.navigation.Navigator;
import com.novoda.todoapp.task.data.model.Id;
import com.novoda.todoapp.task.data.model.Task;
import com.novoda.todoapp.task.edit.displayer.TaskEditActionListener;
import com.novoda.todoapp.task.edit.displayer.TaskEditDisplayer;
import com.novoda.todoapp.tasks.service.TasksService;

import rx.subscriptions.CompositeSubscription;

public class TaskEditPresenter {

    private final Id taskId;
    private final TasksService tasksService;
    private final TaskEditDisplayer taskDisplayer;
    private final Navigator navigator;

    private CompositeSubscription subscriptions = new CompositeSubscription();

    public TaskEditPresenter(
            Id taskId,
            TasksService tasksService,
            TaskEditDisplayer taskDisplayer,
            Navigator navigator
    ) {
        this.taskId = taskId;
        this.tasksService = tasksService;
        this.taskDisplayer = taskDisplayer;
        this.navigator = navigator;
    }

    public void startPresenting() {
        taskDisplayer.attach(taskEditActionListener);
        subscriptions.add(
                tasksService.getTask(taskId)
                        .subscribe(taskObserver)
        );
    }

    public void stopPresenting() {
        taskDisplayer.detach(taskEditActionListener);
        subscriptions.clear();
        subscriptions = new CompositeSubscription();
    }

    final TaskEditActionListener taskEditActionListener = new TaskEditActionListener() {
        @Override
        public void save(Optional<String> title, Optional<String> description) {
            if (title.or(description).isPresent()) {
                Task task = Task.builder()
                        .id(taskId)
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

    private final DataObserver<SyncedData<Task>> taskObserver = new DataObserver<SyncedData<Task>>() {
        @Override
        public void onNext(SyncedData<Task> syncedData) {
            taskDisplayer.display(syncedData);
        }
    };

}
