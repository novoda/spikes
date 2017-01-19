package com.novoda.todoapp.tasks.data.source;

import com.google.common.collect.ImmutableList;
import com.novoda.data.SyncedData;
import com.novoda.todoapp.task.data.model.Id;
import com.novoda.todoapp.task.data.model.Task;
import com.novoda.todoapp.tasks.data.model.Tasks;

import java.util.concurrent.TimeUnit;

import rx.Notification;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func0;

public final class InMemoryLocalTaskDataSource implements LocalTasksDataSource {

    private final int localDelay;
    private final TimeUnit localDelayUnit;

    private Tasks localTasks = Tasks.asSynced(
            ImmutableList.<Task>of(
                    Task.builder()
                        .id(Id.from("42"))
                        .title("First Task")
                        .description("Hardcoded stuff")
                        .build(),
                    Task.builder()
                            .id(Id.from("24"))
                            .title("Second Task")
                            .description("Hardcoded stuff again")
                            .build()
            ),
            System.currentTimeMillis()
    );

    public static LocalTasksDataSource newInstance() {
        return new InMemoryLocalTaskDataSource(1, TimeUnit.SECONDS);
    }

    private InMemoryLocalTaskDataSource(int localDelay, TimeUnit localDelayUnit) {
        this.localDelay = localDelay;
        this.localDelayUnit = localDelayUnit;
    }

    @Override
    public Observable<Tasks> getTasks() {
        return Observable.defer(new Func0<Observable<Tasks>>() {
            @Override
            public Observable<Tasks> call() {
                if (localTasks.isEmpty()) {
                    return Observable.empty();
                } else {
                    return Observable.just(localTasks);
                }
            }
        }).compose(this.<Tasks>delay());
    }

    @Override
    public Observable<Tasks> saveTasks(final Tasks tasks) {
        return Observable.create(new Observable.OnSubscribe<Tasks>() {
            @Override
            public void call(Subscriber<? super Tasks> subscriber) {
                localTasks = tasks;
                subscriber.onNext(localTasks);
                subscriber.onCompleted();
            }
        }).compose(this.<Tasks>delay());
    }

    @Override
    public Observable<SyncedData<Task>> saveTask(final SyncedData<Task> taskSyncedData) {
        return Observable.create(new Observable.OnSubscribe<SyncedData<Task>>() {
            @Override
            public void call(Subscriber<? super SyncedData<Task>> subscriber) {
                localTasks = localTasks.save(taskSyncedData);
                subscriber.onNext(taskSyncedData);
                subscriber.onCompleted();
            }
        }).compose(this.<SyncedData<Task>>delay());
    }

    @Override
    public Observable<Void> deleteAllTasks() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                localTasks = Tasks.empty();
                subscriber.onCompleted();
            }
        }).compose(this.<Void>delay());
    }

    private <T> Observable.Transformer<T, T> delay() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> observable) {
                return observable.doOnEach(new Action1<Notification<? super T>>() {
                    @Override
                    public void call(Notification<? super T> t) {
                        try {
                            Thread.sleep(localDelayUnit.toMillis(localDelay));
                        } catch (InterruptedException e) {
                            this.call(t);
                        }
                    }
                });
            }
        };
    }
}
