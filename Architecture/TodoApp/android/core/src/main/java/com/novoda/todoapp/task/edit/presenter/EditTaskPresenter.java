package com.novoda.todoapp.task.edit.presenter;

import com.novoda.data.SyncedData;
import com.novoda.event.DataObserver;
import com.novoda.todoapp.navigation.Navigator;
import com.novoda.todoapp.task.TaskActionListener;
import com.novoda.todoapp.task.data.model.Task;
import com.novoda.todoapp.task.edit.displayer.EditTaskDisplayer;
import com.novoda.todoapp.task.presenter.IdGenerator;
import com.novoda.todoapp.tasks.service.TasksService;

import rx.subscriptions.CompositeSubscription;

public class EditTaskPresenter {

    private final TasksService tasksService;
    private final EditTaskDisplayer taskDisplayer;
    private final IdGenerator idGenerator;

    final TaskActionListener taskActionListener;

    private CompositeSubscription subscriptions = new CompositeSubscription();

    public EditTaskPresenter(
            TasksService tasksService,
            EditTaskDisplayer taskDisplayer,
            Navigator navigator,
            IdGenerator idGenerator
    ) {
        this.tasksService = tasksService;
        this.taskDisplayer = taskDisplayer;
        this.idGenerator = idGenerator;
        taskActionListener = new TaskActionListener(this.tasksService, this.taskDisplayer, navigator, this.idGenerator);
    }

    public void startPresenting() {
        taskDisplayer.attach(taskActionListener);
        subscriptions.add(
                tasksService.getTask(idGenerator.generate())
                        .subscribe(taskObserver)
        );
    }

    public void stopPresenting() {
        taskDisplayer.detach(taskActionListener);
        subscriptions.clear();
        subscriptions = new CompositeSubscription();
    }

    private final DataObserver<SyncedData<Task>> taskObserver = new DataObserver<SyncedData<Task>>() {
        @Override
        public void onNext(SyncedData<Task> syncedData) {
            taskDisplayer.display(syncedData);
        }
    };

}
