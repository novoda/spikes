package com.novoda.todoapp.tasks.presenter;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.novoda.data.SyncState;
import com.novoda.data.SyncedData;
import com.novoda.event.DataObserver;
import com.novoda.event.Event;
import com.novoda.event.EventObserver;
import com.novoda.todoapp.navigation.Navigator;
import com.novoda.todoapp.task.data.model.Task;
import com.novoda.todoapp.tasks.data.model.Tasks;
import com.novoda.todoapp.tasks.displayer.TasksActionListener;
import com.novoda.todoapp.tasks.displayer.TasksDisplayer;
import com.novoda.todoapp.tasks.loading.displayer.RetryActionListener;
import com.novoda.todoapp.tasks.loading.displayer.TasksLoadingDisplayer;
import com.novoda.todoapp.tasks.service.TasksService;

import rx.Observable;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

import static com.novoda.event.EventFunctions.asData;

public class TasksPresenter {

    private final TasksService tasksService;
    private final TasksLoadingDisplayer loadingDisplayer;
    private final TasksDisplayer tasksDisplayer;
    private final Navigator navigator;

    private CompositeSubscription subscriptions = new CompositeSubscription();
    private TasksActionListener.Filter currentFilter = TasksActionListener.Filter.ALL;

    public TasksPresenter(TasksService tasksService, TasksDisplayer tasksDisplayer, TasksLoadingDisplayer loadingDisplayer, Navigator navigator) {
        this.tasksService = tasksService;
        this.loadingDisplayer = loadingDisplayer;
        this.tasksDisplayer = tasksDisplayer;
        this.navigator = navigator;
    }

    public void setInitialFilterTo(TasksActionListener.Filter filter) {
        currentFilter = filter;
    }

    public TasksActionListener.Filter getCurrentFilter() {
        return currentFilter;
    }

    public void startPresenting() {
        loadingDisplayer.attach(retryActionListener);
        tasksDisplayer.attach(tasksActionListener);
        subscribeToSourcesFilteredWith(currentFilter);
    }

    private void subscribeToSourcesFilteredWith(TasksActionListener.Filter filter) {
        subscriptions.add(
                getValidatedTasksEventObservable(filter)
                        .compose(asData(Tasks.class))
                        .subscribe(tasksObserver)
        );
        subscriptions.add(
                getValidatedTasksEventObservable(filter)
                        .subscribe(tasksEventObserver)
        );
    }

    private Observable<Event<Tasks>> getValidatedTasksEventObservable(TasksActionListener.Filter filter) {
        return getTasksEventObservableFor(filter)
                .map(new Func1<Event<Tasks>, Event<Tasks>>() {
                    @Override
                    public Event<Tasks> call(Event<Tasks> tasksEvent) {
                        Iterable<SyncedData<Task>> nonDeletedTasks = Iterables.filter(tasksEvent.data().or(Tasks.empty()).all(), shouldDisplayTask());
                        return tasksEvent.updateData(Tasks.from(ImmutableList.copyOf(nonDeletedTasks)));
                    }
                });
    }

    private Observable<Event<Tasks>> getTasksEventObservableFor(TasksActionListener.Filter filter) {
        switch (filter) {
            case ACTIVE:
                return tasksService.getActiveTasksEvent();
            case COMPLETED:
                return tasksService.getCompletedTasksEvent();
            case ALL:
                return tasksService.getTasksEvent();
            default:
                throw new IllegalArgumentException("Unknown filter type " + filter);
        }
    }

    private static Predicate<SyncedData<Task>> shouldDisplayTask() {
        return new Predicate<SyncedData<Task>>() {
            @Override
            public boolean apply(SyncedData<Task> input) {
                return input.syncState() != SyncState.DELETED_LOCALLY;
            }
        };
    }

    public void stopPresenting() {
        loadingDisplayer.detach(retryActionListener);
        tasksDisplayer.detach(tasksActionListener);
        clearSubscriptions();
    }

    private void clearSubscriptions() {
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

        @Override
        public void onRefreshSelected() {
            tasksService.refreshTasks().call();
        }

        @Override
        public void onClearCompletedSelected() {
            tasksService.clearCompletedTasks().call();
        }

        @Override
        public void onFilterSelected(Filter filter) {
            currentFilter = filter;
            clearSubscriptions();
            subscribeToSourcesFilteredWith(filter);
        }

        @Override
        public void onAddTaskSelected() {
            navigator.toAddTask();
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
                showEmptyScreen();
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

    private void showEmptyScreen() {
        switch (currentFilter) {
            case ALL:
                loadingDisplayer.showEmptyTasksScreen();
                break;
            case ACTIVE:
                loadingDisplayer.showEmptyActiveTasksScreen();
                break;
            case COMPLETED:
                loadingDisplayer.showEmptyCompletedTasksScreen();
                break;
        }
    }

}
