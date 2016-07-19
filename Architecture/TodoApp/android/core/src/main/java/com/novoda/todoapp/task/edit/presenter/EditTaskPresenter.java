package com.novoda.todoapp.task.edit.presenter;

import com.novoda.data.SyncedData;
import com.novoda.event.SingleDataObserver;
import com.novoda.todoapp.navigation.Navigator;
import com.novoda.todoapp.task.TaskActionListener;
import com.novoda.todoapp.task.data.model.Id;
import com.novoda.todoapp.task.data.model.Task;
import com.novoda.todoapp.task.edit.displayer.EditTaskDisplayer;
import com.novoda.todoapp.task.presenter.IdProducer;
import com.novoda.todoapp.tasks.service.TasksService;

import rx.Observer;
import rx.subscriptions.CompositeSubscription;

public class EditTaskPresenter {

    private final Id id;
    private final TasksService tasksService;
    private final EditTaskDisplayer taskDisplayer;

    TaskActionListener taskActionListener;

    private CompositeSubscription subscriptions = new CompositeSubscription();

    public EditTaskPresenter(
            TasksService tasksService,
            EditTaskDisplayer taskDisplayer,
            Navigator navigator,
            Id id
    ) {
        this.tasksService = tasksService;
        this.taskDisplayer = taskDisplayer;
        this.id = id;
        taskActionListener = new TaskActionListener(tasksService, taskDisplayer, navigator, new ConstantIdProducer(id));
    }

    public void startPresenting() {
        taskDisplayer.attach(taskActionListener);
        subscriptions.add(
                tasksService.getTask(id)
                        .take(1)
                        .subscribe(taskObserver)
        );
    }

    public void stopPresenting() {
        taskDisplayer.detach(taskActionListener);
        clearSubscriptions();
    }

    private void clearSubscriptions() {
        subscriptions.clear();
        subscriptions = new CompositeSubscription();
    }

    private final Observer<SyncedData<Task>> taskObserver = new SingleDataObserver<SyncedData<Task>>() {
        @Override
        public void onNext(SyncedData<Task> syncedData) {
            taskDisplayer.display(syncedData);
        }
    };

    private static class ConstantIdProducer implements IdProducer {
        private final Id id;

        public ConstantIdProducer(Id id) {
            this.id = id;
        }

        @Override
        public Id produce() {
            return id;
        }
    }
}
