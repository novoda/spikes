package com.novoda.todoapp.tasks.data.source;

import com.google.common.collect.ImmutableList;
import com.novoda.todoapp.task.data.model.Id;
import com.novoda.todoapp.task.data.model.Task;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Notification;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func0;

public final class InMemoryRemoteTaskDataSource implements RemoteTasksDataSource {

    private final int remoteDelay;
    private final TimeUnit remoteDelayUnit;
    private final Map<Id, Task> dataSourceTasks = new HashMap<>();

    public static RemoteTasksDataSource newInstance() {
        InMemoryRemoteTaskDataSource dataSource = new InMemoryRemoteTaskDataSource(2, TimeUnit.SECONDS);
        populateFakeTasks(dataSource);
        return dataSource;
    }

    private static void populateFakeTasks(InMemoryRemoteTaskDataSource dataSource) {
        ImmutableList<Task> remoteTasks = ImmutableList.<Task>of(
                Task.builder()
                        .id(Id.from("24"))
                        .title("First Task")
                        .description("Hardcoded stuff")
                        .build(),
                Task.builder()
                        .id(Id.from("42"))
                        .title("Second Task")
                        .description("Hardcoded stuff again")
                        .build(),
                Task.builder()
                        .id(Id.from("424"))
                        .title("Third Task")
                        .description("Hardcoded stuff once more")
                        .build()
        );
        for (Task task : remoteTasks) {
            dataSource.dataSourceTasks.put(task.id(), task);
        }
    }

    private InMemoryRemoteTaskDataSource(int remoteDelay, TimeUnit remoteDelayUnit) {
        this.remoteDelay = remoteDelay;
        this.remoteDelayUnit = remoteDelayUnit;
    }

    @Override
    public Observable<List<Task>> getTasks() {
        return Observable.defer(new Func0<Observable<List<Task>>>() {
            @Override
            public Observable<List<Task>> call() {
                List<Task> tasks = ImmutableList.copyOf(dataSourceTasks.values());
                if (tasks.isEmpty()) {
                    return Observable.empty();
                } else {
                    return Observable.just(tasks);
                }
            }
        }).compose(this.<List<Task>>delay());
    }

    @Override
    public Observable<Task> getTask(Id taskId) {
        if (dataSourceTasks.containsKey(taskId)) {
            return Observable.just(dataSourceTasks.get(taskId))
                    .compose(this.<Task>delay());
        } else {
            return Observable.<Task>empty()
                    .compose(this.<Task>delay());
        }
    }

    @Override
    public Observable<List<Task>> saveTasks(final List<Task> tasks) {
        return Observable.create(new Observable.OnSubscribe<List<Task>>() {
            @Override
            public void call(Subscriber<? super List<Task>> subscriber) {
                dataSourceTasks.clear();
                for (Task task : tasks) {
                    dataSourceTasks.put(task.id(), task);
                }
                subscriber.onNext(tasks);
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Task> saveTask(Task task) {
        return updateTask(task);
    }

    private Observable<Task> updateTask(Task task) {
        return Observable.just(task)
                .doOnNext(new Action1<Task>() {
                    @Override
                    public void call(Task task) {
                        dataSourceTasks.put(task.id(), task);
                    }
                })
                .compose(this.<Task>delay());
    }

    @Override
    public Observable<List<Task>> clearCompletedTasks() {
        return Observable.defer(new Func0<Observable<List<Task>>>() {
            @Override
            public Observable<List<Task>> call() {
                Iterator<Map.Entry<Id, Task>> it = dataSourceTasks.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<Id, Task> entry = it.next();
                    if (entry.getValue().isCompleted()) {
                        it.remove();
                    }
                }
                List<Task> tasks = ImmutableList.copyOf(dataSourceTasks.values());
                return Observable.just(tasks);
            }
        }).compose(this.<List<Task>>delay());
    }

    @Override
    public Observable<Void> deleteTask(final Id taskId) {
        return Observable.defer(new Func0<Observable<Void>>() {
            @Override
            public Observable<Void> call() {
                dataSourceTasks.remove(taskId);
                return Observable.empty();
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
                            Thread.sleep(remoteDelayUnit.toMillis(remoteDelay));
                        } catch (InterruptedException e) {
                            this.call(t);
                        }
                    }
                });
            }
        };
    }

}
