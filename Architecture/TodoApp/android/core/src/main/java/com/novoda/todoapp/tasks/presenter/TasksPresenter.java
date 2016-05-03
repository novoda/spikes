package com.novoda.todoapp.tasks.presenter;

import com.novoda.event.DataObserver;
import com.novoda.event.Event;
import com.novoda.event.EventObserver;
import com.novoda.todoapp.loading.displayer.LoadingDisplayer;
import com.novoda.todoapp.loading.displayer.RetryActionListener;
import com.novoda.todoapp.navigation.Navigator;
import com.novoda.todoapp.task.data.model.Task;
import com.novoda.todoapp.tasks.data.model.Tasks;
import com.novoda.todoapp.tasks.displayer.TasksActionListener;
import com.novoda.todoapp.tasks.displayer.TasksDisplayer;
import com.novoda.todoapp.tasks.service.TasksService;

import rx.subscriptions.CompositeSubscription;

public class TasksPresenter {

    private final TasksService tasksService;
    private final LoadingDisplayer loadingDisplayer;
    private final TasksDisplayer tasksDisplayer;
    private final Navigator navigator;

    private CompositeSubscription subscriptions = new CompositeSubscription();

    public TasksPresenter(TasksService tasksService, TasksDisplayer tasksDisplayer, LoadingDisplayer loadingDisplayer, Navigator navigator) {
        this.tasksService = tasksService;
        this.loadingDisplayer = loadingDisplayer;
        this.tasksDisplayer = tasksDisplayer;
        this.navigator = navigator;
    }

    public void startPresenting() {
        loadingDisplayer.attach(retryActionListener);
        tasksDisplayer.attach(tasksActionListener);
        subscriptions.add(
                tasksService.getTasks()
                    .subscribe(tasksObserver)
        );
        subscriptions.add(
                tasksService.getTasksEvents()
                    .subscribe(tasksEventObserver)
        );
    }

    public void stopPresenting() {
        loadingDisplayer.detach(retryActionListener);
        tasksDisplayer.detach(tasksActionListener);
        subscriptions.clear();
        subscriptions = new CompositeSubscription();
    }

    final RetryActionListener retryActionListener = new RetryActionListener() {
        @Override
        public void onRetry() {
            tasksService.refreshTasks().call();
        }
    };

    final TasksActionListener tasksActionListener = new TasksActionListener() {
        @Override
        public void onTaskSelected(Task task) {
            navigator.toTaskDetail(task);
        }

        @Override
        public void toggleCompletion(Task task) {
            if (task.isCompleted()) {
                tasksService.activate(task).call();
            } else {
                tasksService.complete(task).call();
            }
        }
    };

    private final DataObserver<Tasks> tasksObserver = new DataObserver<Tasks>() {
        @Override
        public void onNext(Tasks tasks) {
            tasksDisplayer.display(tasks);
        }
    };

    private final EventObserver<Tasks> tasksEventObserver = new EventObserver<Tasks>() {
        @Override
        public void onLoading(Event<Tasks> event) {
            if (event.data().isPresent()) {
                loadingDisplayer.showLoadingIndicator();
            } else {
                loadingDisplayer.showLoadingScreen();
            }
        }

        @Override
        public void onIdle(Event<Tasks> event) {
            if (event.data().isPresent()) {
                loadingDisplayer.showData();
            } else {
                loadingDisplayer.showEmptyScreen();
            }
        }

        @Override
        public void onError(Event<Tasks> event) {
            if (event.data().isPresent()) {
                loadingDisplayer.showErrorIndicator();
            } else {
                loadingDisplayer.showErrorScreen();
            }
        }
    };

}
