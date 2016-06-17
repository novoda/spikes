package com.novoda.todoapp.task.presenter;

import com.novoda.data.SyncedData;
import com.novoda.event.DataObserver;
import com.novoda.todoapp.navigation.Navigator;
import com.novoda.todoapp.task.data.model.Id;
import com.novoda.todoapp.task.data.model.Task;
import com.novoda.todoapp.task.displayer.TaskActionListener;
import com.novoda.todoapp.task.displayer.TaskDisplayer;
import com.novoda.todoapp.tasks.service.TasksService;

import rx.subscriptions.CompositeSubscription;

public class TaskPresenter {

    private final Id taskId;
    private final TasksService tasksService;
    private final TaskDisplayer taskDisplayer;
    private final Navigator navigator;

    private CompositeSubscription subscriptions = new CompositeSubscription();

    public TaskPresenter(
            Id taskId,
            TasksService tasksService,
            TaskDisplayer taskDisplayer,
            Navigator navigator
    ) {
        this.taskId = taskId;
        this.tasksService = tasksService;
        this.taskDisplayer = taskDisplayer;
        this.navigator = navigator;
    }

    public void startPresenting() {
        taskDisplayer.attach(taskActionListener);
        subscriptions.add(
                tasksService.getTask(taskId)
                    .subscribe(taskObserver)
        );
    }

    public void stopPresenting() {
        taskDisplayer.detach(taskActionListener);
        subscriptions.clear();
        subscriptions = new CompositeSubscription();
    }

    final TaskActionListener taskActionListener = new TaskActionListener() {
        @Override
        public void toggleCompletion(Task task) {
            if (task.isCompleted()) {
                tasksService.activate(task).call();
            } else {
                tasksService.complete(task).call();
            }
        }

        @Override
        public void onEditSelected(Task task) {
            navigator.toTaskEdit(task);
        }
    };

    private final DataObserver<SyncedData<Task>> taskObserver = new DataObserver<SyncedData<Task>>() {
        @Override
        public void onNext(SyncedData<Task> syncedData) {
            taskDisplayer.display(syncedData);
        }
    };

}
