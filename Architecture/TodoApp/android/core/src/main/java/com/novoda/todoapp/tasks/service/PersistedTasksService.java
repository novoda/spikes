package com.novoda.todoapp.tasks.service;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.jakewharton.rxrelay.BehaviorRelay;
import com.novoda.data.DataOrchestrator;
import com.novoda.data.SyncState;
import com.novoda.data.SyncedData;
import com.novoda.event.Event;
import com.novoda.event.EventFunctions;
import com.novoda.todoapp.rx.IfThenFlatMap;
import com.novoda.todoapp.task.data.model.Id;
import com.novoda.todoapp.task.data.model.Task;
import com.novoda.todoapp.tasks.NoEmptyTasksPredicate;
import com.novoda.todoapp.tasks.data.TasksDataFreshnessChecker;
import com.novoda.todoapp.tasks.data.model.Tasks;
import com.novoda.todoapp.tasks.data.source.LocalTasksDataSource;
import com.novoda.todoapp.tasks.data.source.RemoteTasksDataSource;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Func0;
import rx.functions.Func1;

import static com.novoda.data.SyncFunctions.asOrchestratedAction;
import static com.novoda.event.EventFunctions.asData;
import static com.novoda.event.EventFunctions.isInitialised;
import static com.novoda.todoapp.rx.RxFunctions.ifThenMap;

public class PersistedTasksService implements TasksService {

    private final LocalTasksDataSource localDataSource;
    private final RemoteTasksDataSource remoteDataSource;
    private final TasksDataFreshnessChecker tasksDataFreshnessChecker;
    private final Clock clock;

    private final BehaviorRelay<Event<Tasks>> taskRelay = BehaviorRelay.create(Event.idle(noEmptyTasks()));

    private static Predicate<Tasks> noEmptyTasks() {
        return new NoEmptyTasksPredicate();
    }

    public PersistedTasksService(
            LocalTasksDataSource localDataSource,
            RemoteTasksDataSource remoteDataSource,
            TasksDataFreshnessChecker tasksDataFreshnessChecker,
            Clock clock) {
        this.localDataSource = localDataSource;
        this.remoteDataSource = remoteDataSource;
        this.tasksDataFreshnessChecker = tasksDataFreshnessChecker;
        this.clock = clock;
    }

    @Override
    public Observable<Event<Tasks>> getTasksEvent() {
        return taskRelay.asObservable()
                .startWith(initialiseSubject())
                .distinctUntilChanged();
    }

    @Override
    public Observable<Event<Tasks>> getCompletedTasksEvent() {
        return getTasksEvent()
                .map(filterTasks(onlyCompleted()));
    }

    private static Func1<Event<Tasks>, Event<Tasks>> filterTasks(final Function<Tasks, Tasks> filter) {
        return new Func1<Event<Tasks>, Event<Tasks>>() {
            @Override
            public Event<Tasks> call(Event<Tasks> event) {
                return event.updateData(event.data().transform(filter));
            }
        };
    }

    private static Function<Tasks, Tasks> onlyCompleted() {
        return new Function<Tasks, Tasks>() {
            @Override
            public Tasks apply(Tasks input) {
                return input.onlyCompleted();
            }
        };
    }

    @Override
    public Observable<Event<Tasks>> getActiveTasksEvent() {
        return getTasksEvent()
                .map(filterTasks(onlyActives()));
    }

    private static Function<Tasks, Tasks> onlyActives() {
        return new Function<Tasks, Tasks>() {
            @Override
            public Tasks apply(Tasks input) {
                return input.onlyActives();
            }
        };
    }

    private Observable<Event<Tasks>> initialiseSubject() {
        return Observable.defer(new Func0<Observable<Event<Tasks>>>() {
            @Override
            public Observable<Event<Tasks>> call() {
                if (isInitialised(taskRelay)) {
                    return Observable.empty();
                }
                return localDataSource.getTasks()
                        .flatMap(fetchFromRemoteIfOutOfDate())
                        .switchIfEmpty(fetchFromRemote())
                        .compose(asValidatedEvent(taskRelay.getValue()))
                        .doOnNext(taskRelay)
                        .publish().autoConnect();
            }
        });
    }

    private Func1<Tasks, Observable<Tasks>> fetchFromRemoteIfOutOfDate() {
        return new Func1<Tasks, Observable<Tasks>>() {
            @Override
            public Observable<Tasks> call(Tasks tasks) {
                if (tasksDataFreshnessChecker.isFresh(tasks)) {
                    return Observable.just(tasks);
                }
                return fetchFromRemote().startWith(tasks);
            }
        };
    }

    private Observable<Tasks> fetchFromRemote() {
        return remoteDataSource.getTasks()
                .map(asSyncedTasksNow())
                .flatMap(persistTasksLocally());
    }

    private Func1<List<Task>, Tasks> asSyncedTasksNow() {
        return new Func1<List<Task>, Tasks>() {
            @Override
            public Tasks call(List<Task> tasks) {
                return Tasks.asSynced(tasks, clock.timeInMillis());
            }
        };
    }

    private Func1<Tasks, Observable<Tasks>> persistTasksLocally() {
        return new Func1<Tasks, Observable<Tasks>>() {
            @Override
            public Observable<Tasks> call(Tasks tasks) {
                return localDataSource.saveTasks(tasks);
            }
        };
    }

    @Override
    public Observable<SyncedData<Task>> getTask(final Id taskId) {
        return getTasksEvent()
                .compose(asData(Tasks.class))
                .flatMap(new Func1<Tasks, Observable<SyncedData<Task>>>() {
                    @Override
                    public Observable<SyncedData<Task>> call(Tasks tasks) {
                        if (tasks.containsTask(taskId)) {
                            return Observable.just(tasks.syncedDataFor(taskId));
                        }
                        return Observable.empty(); //TODO handle remote fetching as fallback.
                    }
                });
    }

    @Override
    public Action0 refreshTasks() {
        return new Action0() {
            @Override
            public void call() {
                fetchFromRemote()
                        .compose(asValidatedEvent(taskRelay.getValue()))
                        .subscribe(taskRelay);
            }
        };
    }

    @Override
    public Action0 clearCompletedTasks() {
        return new Action0() {
            @Override
            public void call() {
                long actionTimestamp = clock.timeInMillis();
                Event<Tasks> tasksEvent = taskRelay.getValue();
                Tasks allLocalTasks = tasksEvent.data().or(Tasks.empty());
                Tasks uncompletedLocalTasks = mapFunctionToTasks(allLocalTasks, markCompletedAsDeleted());

                remoteDataSource.clearCompletedTasks()
                        .compose(confirmLocalDeletionsOrRevert(allLocalTasks, uncompletedLocalTasks, actionTimestamp))
                        .flatMap(persistTasksLocally())
                        .compose(asValidatedEvent(tasksEvent.updateData(uncompletedLocalTasks)))
                        .subscribe(taskRelay);
            }
        };
    }

    private static Observable.Transformer<List<Task>, Tasks> confirmLocalDeletionsOrRevert(
            final Tasks allLocalTasks,
            final Tasks uncompletedLocalTasks,
            final long actionTimestamp
    ) {
        return asOrchestratedAction(new DataOrchestrator<List<Task>, Tasks>() {
            @Override
            public Tasks startWith() {
                return uncompletedLocalTasks;
            }

            @Override
            public Tasks onConfirmed(List<Task> tasks) {
                return Tasks.asSynced(tasks, actionTimestamp);
            }

            @Override
            public Tasks onError() {
                return mapFunctionToTasks(allLocalTasks, markAsSyncError());
            }
        });
    }

    private static Tasks mapFunctionToTasks(Tasks tasksFromEvent, Function<SyncedData<Task>, SyncedData<Task>> function) {
        return Tasks.from(ImmutableList.copyOf(Iterables.transform(tasksFromEvent.all(), function)));
    }

    private static Function<SyncedData<Task>, SyncedData<Task>> markCompletedAsDeleted() {
        return new Function<SyncedData<Task>, SyncedData<Task>>() {
            @Override
            public SyncedData<Task> apply(SyncedData<Task> input) {
                if (input.data().isCompleted()) {
                    return input.toBuilder().syncState(SyncState.DELETED_LOCALLY).build();
                } else {
                    return input.toBuilder().syncState(SyncState.AHEAD).build();
                }
            }
        };
    }

    private static Function<SyncedData<Task>, SyncedData<Task>> markAsSyncError() {
        return new Function<SyncedData<Task>, SyncedData<Task>>() {
            @Override
            public SyncedData<Task> apply(SyncedData<Task> input) {
                return input.toBuilder().syncState(SyncState.SYNC_ERROR).build();
            }
        };
    }

    @Override
    public Action0 deleteAllTasks() {
        return new Action0() {
            @Override
            public void call() {
                remoteDataSource.deleteAllTasks()
                        .flatMap(deleteAllLocalTasks())
                        .compose(asValidatedEvent(taskRelay.getValue()))
                        .subscribe(taskRelay);
            }
        };
    }

    private Func1<Void, Observable<Tasks>> deleteAllLocalTasks() {
        return new Func1<Void, Observable<Tasks>>() {
            @Override
            public Observable<Tasks> call(Void aVoid) {
                return localDataSource.deleteAllTasks()
                        .map(new Func1<Void, Tasks>() {
                            @Override
                            public Tasks call(Void aVoid) {
                                return Tasks.empty();
                            }
                        });
            }
        };
    }

    @Override
    public Action0 complete(final Task originalTask) {
        return save(originalTask.complete());
    }

    @Override
    public Action0 activate(final Task originalTask) {
        return save(originalTask.activate());
    }

    @Override
    public Action0 save(final Task updatedTask) {
        return new Action0() {
            @Override
            public void call() {
                final long actionTimestamp = clock.timeInMillis();
                remoteDataSource.saveTask(updatedTask)
                        .compose(startAheadThenConfirmOrMarkAsError(updatedTask, actionTimestamp))
                        .compose(updateAndPersistIfMostRecentAction())
                        .subscribe();
            }
        };
    }

    @Override
    public void delete(final Task task) {

        markTaskAsDeletedLocally(taskRelay.getValue(), task);

        remoteDataSource.deleteTask(task.id())
                .subscribe(new Subscriber<Void>() {
                    @Override
                    public void onCompleted() {
                        Event<Tasks> currentState = taskRelay.getValue();
                        Tasks tasks = tasksWithout(currentState.data().or(Tasks.empty()), task);
                        localDataSource.saveTasks(tasks);
                        taskRelay.call(currentState.updateData(tasks).asIdle());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Event<Tasks> currentState = taskRelay.getValue();
                        Tasks tasks = mapFunctionToTasks(currentState.data().or(Tasks.empty()), new Function<SyncedData<Task>, SyncedData<Task>>() {
                            @Override
                            public SyncedData<Task> apply(SyncedData<Task> input) {
                                if (input.data().id().equals(task.id())) {
                                    return SyncedData.from(task, SyncState.SYNC_ERROR, clock.timeInMillis());
                                } else {
                                    return input;
                                }
                            }
                        });
                        localDataSource.saveTasks(tasks);
                        taskRelay.call(currentState.updateData(tasks).asError(new SyncError()));
                    }

                    @Override
                    public void onNext(Void aVoid) {
                        // void type, should never be called
                    }
                });
    }

    private Tasks tasksWithout(Tasks tasks, Task task) {
        return tasks.withoutTask(task);
    }

    private void markTaskAsDeletedLocally(Event<Tasks> currentState, Task task) {
        if (currentState.data().isPresent()) {
            Tasks updatedTasks = currentState.data().get().save(SyncedData.from(task, SyncState.DELETED_LOCALLY, clock.timeInMillis()));
            localDataSource.saveTasks(updatedTasks).subscribe();
            taskRelay.call(currentState.asLoadingWithData(updatedTasks));
        } else {
            throw new RuntimeException("Trying to delete a task from empty data");
        }
    }

    private static Observable.Transformer<Task, SyncedData<Task>> startAheadThenConfirmOrMarkAsError(
            final Task updatedTask,
            final long actionTimestamp
    ) {
        return asOrchestratedAction(new DataOrchestrator<Task, SyncedData<Task>>() {
            @Override
            public SyncedData<Task> startWith() {
                return SyncedData.from(updatedTask, SyncState.AHEAD, actionTimestamp);
            }

            @Override
            public SyncedData<Task> onConfirmed(Task taskFromRemote) {
                return SyncedData.from(taskFromRemote, SyncState.IN_SYNC, actionTimestamp);
            }

            @Override
            public SyncedData<Task> onError() {
                return SyncedData.from(updatedTask, SyncState.SYNC_ERROR, actionTimestamp); //TODO add a retry logic for all Sync_Error states
            }
        });
    }

    private Observable.Transformer<SyncedData<Task>, SyncedData<Task>> updateAndPersistIfMostRecentAction() {
        return new Observable.Transformer<SyncedData<Task>, SyncedData<Task>>() {
            @Override
            public Observable<SyncedData<Task>> call(Observable<SyncedData<Task>> observable) {
                return observable.flatMap(ifThenMap(new IfThenFlatMap<SyncedData<Task>, SyncedData<Task>>() {
                    @Override
                    public boolean ifMatches(SyncedData<Task> value) {
                        Tasks tasks = taskRelay.getValue().data().or(Tasks.empty());
                        return tasks.isMostRecentAction(value);
                    }

                    @Override
                    public Observable<SyncedData<Task>> thenMap(final SyncedData<Task> value) {
                        Event<Tasks> event = taskRelay.getValue();
                        Tasks tasks = event.data().or(Tasks.empty());
                        return Observable.just(value)
                                .map(asUpdated(event, tasks))
                                .doOnNext(taskRelay)
                                .flatMap(persistSyncedData(value));
                    }

                    @Override
                    public Observable<SyncedData<Task>> elseMap(SyncedData<Task> value) {
                        return Observable.empty();
                    }
                }));
            }
        };
    }

    private static Func1<SyncedData<Task>, Event<Tasks>> asUpdated(final Event<Tasks> event, final Tasks tasks) {
        return new Func1<SyncedData<Task>, Event<Tasks>>() {
            @Override
            public Event<Tasks> call(SyncedData<Task> syncedData) {
                Tasks updatedTasks = tasks.save(syncedData);
                return event.updateData(updatedTasks);
            }
        };
    }

    private Func1<Event<Tasks>, Observable<SyncedData<Task>>> persistSyncedData(final SyncedData<Task> value) {
        return new Func1<Event<Tasks>, Observable<SyncedData<Task>>>() {
            @Override
            public Observable<SyncedData<Task>> call(Event<Tasks> event) {
                return localDataSource.saveTask(value);
            }
        };
    }

    private static Observable.Transformer<Tasks, Event<Tasks>> asValidatedEvent(final Event<Tasks> value) {
        return new Observable.Transformer<Tasks, Event<Tasks>>() {
            @Override
            public Observable<Event<Tasks>> call(Observable<Tasks> tasksObservable) {
                return tasksObservable
                        .compose(EventFunctions.asEvent(value))
                        .map(new Func1<Event<Tasks>, Event<Tasks>>() {
                                 @Override
                                 public Event<Tasks> call(Event<Tasks> tasksEvent) {
                                     if (tasksEvent.data().or(Tasks.empty()).hasSyncError()) {
                                         return tasksEvent.asError(new SyncError());
                                     } else {
                                         return tasksEvent;
                                     }
                                 }
                             }
                        );
            }
        };
    }

}
