package com.novoda.todoapp.tasks.service;

import com.novoda.data.SyncedData;
import com.novoda.event.Event;
import com.novoda.todoapp.task.data.model.Id;
import com.novoda.todoapp.task.data.model.Task;
import com.novoda.todoapp.tasks.data.model.Tasks;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class TasksServiceAsync implements TasksService {

    private final TasksService tasksService;

    public TasksServiceAsync(TasksService tasksService) {
        this.tasksService = tasksService;
    }

    @Override
    public Observable<Event<Tasks>> getTasksEvent() {
        return tasksService.getTasksEvent()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Event<Tasks>> getCompletedTasksEvent() {
        return tasksService.getCompletedTasksEvent()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Event<Tasks>> getActiveTasksEvent() {
        return tasksService.getActiveTasksEvent()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<SyncedData<Task>> getTask(Id taskId) {
        return tasksService.getTask(taskId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Action0 refreshTasks() {
        return asAsyncAction(tasksService.refreshTasks());
    }

    @Override
    public Action0 clearCompletedTasks() {
        return asAsyncAction(tasksService.clearCompletedTasks());
    }

    @Override
    public Action0 complete(Task task) {
        return asAsyncAction(tasksService.complete(task));
    }

    @Override
    public Action0 activate(Task task) {
        return asAsyncAction(tasksService.activate(task));
    }

    @Override
    public Action0 save(Task task) {
        return asAsyncAction(tasksService.save(task));
    }

    @Override
    public void delete(Task task) {
        tasksService.delete(task);
    }

    private static Action0 asAsyncAction(final Action0 action0) {
        return new Action0() {
            @Override
            public void call() {
                asObservable(action0)
                        .subscribeOn(Schedulers.io())
                        .subscribe();
            }
        };
    }

    private static Observable<Void> asObservable(final Action0 action0) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                action0.call();
                subscriber.onCompleted();
            }
        });
    }

}
