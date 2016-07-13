package com.novoda.todoapp.tasks.service;

import com.google.common.base.Function;
import com.google.common.base.Optional;
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
import rx.Scheduler;
import rx.functions.Action0;
import rx.functions.Func0;
import rx.functions.Func1;

import static com.novoda.data.SyncFunctions.asOrchestratedAction;
import static com.novoda.event.EventFunctions.asData;
import static com.novoda.event.EventFunctions.isNotInitialised;
import static com.novoda.todoapp.rx.RxFunctions.ifThenMap;

public class PersistedTasksService implements TasksService {

    private final LocalTasksDataSource localDataSource;
    private final RemoteTasksDataSource remoteDataSource;
    private final TasksDataFreshnessChecker tasksDataFreshnessChecker;
    private final Clock clock;
    private final Scheduler scheduler;

    private final BehaviorRelay<Event<Tasks>> taskRelay = BehaviorRelay.create(Event.idle(noEmptyTasks()));

    private static Predicate<Tasks> noEmptyTasks() {
        return new NoEmptyTasksPredicate();
    }

    public PersistedTasksService(
            LocalTasksDataSource localDataSource,
            RemoteTasksDataSource remoteDataSource,
            TasksDataFreshnessChecker tasksDataFreshnessChecker,
            Clock clock,
            Scheduler scheduler) {
        this.localDataSource = localDataSource;
        this.remoteDataSource = remoteDataSource;
        this.tasksDataFreshnessChecker = tasksDataFreshnessChecker;
        this.clock = clock;
        this.scheduler = scheduler;
    }

    @Override
    public Observable<Event<Tasks>> getTasksEvent() {
        return taskRelay.asObservable()
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        if (isNotInitialised(taskRelay)) {
                            initialiseRelay();
                        }
                    }
                });
    }

    private void initialiseRelay() {
        long actionTimeInMillis = clock.timeInMillis();
        localDataSource.getTasks()
                .flatMap(fetchFromRemoteIfOutOfDate(actionTimeInMillis))
                .switchIfEmpty(fetchFromRemote(actionTimeInMillis))
                .compose(asValidatedEvent(taskRelay.getValue()))
                .subscribeOn(scheduler)
                .subscribe(taskRelay);
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

    private Func1<Tasks, Observable<Tasks>> fetchFromRemoteIfOutOfDate(final long actionTimeInMillis) {
        return new Func1<Tasks, Observable<Tasks>>() {
            @Override
            public Observable<Tasks> call(Tasks tasks) {
                if (tasksDataFreshnessChecker.isFresh(tasks)) {
                    return Observable.just(tasks);
                }
                return fetchFromRemote(actionTimeInMillis).startWith(tasks);
            }
        };
    }

    private Observable<Tasks> fetchFromRemote(long actionTimestamp) {
        return remoteDataSource.getTasks()
                .map(asSyncedTasks(actionTimestamp))
                .map(updateOnlyMoreRecent(actionTimestamp))
                .flatMap(persistTasksLocally());
    }

    private static Func1<List<Task>, Tasks> asSyncedTasks(final long actionTimestamp) {
        return new Func1<List<Task>, Tasks>() {
            @Override
            public Tasks call(List<Task> tasks) {
                return Tasks.asSynced(tasks, actionTimestamp);
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
    public void refreshTasks() {
        fetchRemote()
                .compose(asValidatedEvent(taskRelay.getValue()))
                .subscribeOn(scheduler)
                .subscribe(taskRelay);
    }

    private Observable<Tasks> fetchRemote() {
        return Observable.defer(new Func0<Observable<Tasks>>() {
            @Override
            public Observable<Tasks> call() {
                return fetchFromRemote(clock.timeInMillis());
            }
        });
    }

    @Override
    public void clearCompletedTasks() {
        long actionTimestamp = clock.timeInMillis();

        remoteDataSource.clearCompletedTasks()
                .compose(confirmLocalDeletionsOrRevert(taskRelay, actionTimestamp))
                .map(updateOnlyMoreRecent(actionTimestamp))
                .compose(asValidatedEvent(taskRelay.getValue()))
                .doOnNext(taskRelay)
                .flatMap(persistEventTasksLocally())
                .subscribeOn(scheduler)
                .subscribe();
    }

    private static Observable.Transformer<List<Task>, Tasks> confirmLocalDeletionsOrRevert(final BehaviorRelay<Event<Tasks>> taskRelay, final long actionTimestamp) {
        return asOrchestratedAction(new DataOrchestrator<List<Task>, Tasks>() {
            @Override
            public Tasks startWith() {
                Event<Tasks> currentState = taskRelay.getValue();
                Tasks allLocalTasks = currentState.data().or(Tasks.empty());
                return mapFunctionToTasks(allLocalTasks, markCompletedAsDeleted(actionTimestamp));
            }

            @Override
            public Tasks onConfirmed(List<Task> tasks) {
                return Tasks.asSynced(tasks, actionTimestamp);
            }

            @Override
            public Tasks onConfirmedWithoutData() {
                return onError();
            }

            @Override
            public Tasks onError() {
                Event<Tasks> currentState = taskRelay.getValue();
                Tasks allLocalTasks = currentState.data().or(Tasks.empty());
                return mapFunctionToTasks(allLocalTasks, markAsSyncError());
            }
        });
    }

    private Func1<Tasks, Tasks> updateOnlyMoreRecent(final long actionTimestamp) {
        return new Func1<Tasks, Tasks>() {
            @Override
            public Tasks call(Tasks tasks) {
                Tasks currentTasks = taskRelay.getValue().data().or(Tasks.empty());
                Tasks moreRecentTask = currentTasks.filter(isMoreRecentAction(tasks, actionTimestamp));
                return tasks.insertOrUpdate(moreRecentTask);
            }
        };
    }

    private static Predicate<SyncedData<Task>> isMoreRecentAction(final Tasks tasks, final long actionTimestamp) {
        return new Predicate<SyncedData<Task>>() {
            @Override
            public boolean apply(SyncedData<Task> input) {
                return input.lastSyncAction() > actionTimestamp || actionExistsAndIsOutdatedComparedTo(tasks, input);
            }
        };
    }

    private static boolean actionExistsAndIsOutdatedComparedTo(Tasks tasks, SyncedData<Task> syncedData) {
        Optional<SyncedData<Task>> existingAction = tasks.get(syncedData.data().id());
        return existingAction.isPresent() && existingAction.get().lastSyncAction() < syncedData.lastSyncAction();
    }

    private static Tasks mapFunctionToTasks(Tasks tasks, Function<SyncedData<Task>, SyncedData<Task>> function) {
        return Tasks.from(ImmutableList.copyOf(Iterables.transform(tasks.all(), function)));
    }

    private static Function<SyncedData<Task>, SyncedData<Task>> markCompletedAsDeleted(final long actionTimestamp) {
        return new Function<SyncedData<Task>, SyncedData<Task>>() {
            @Override
            public SyncedData<Task> apply(SyncedData<Task> input) {
                if (input.data().isCompleted()) {
                    return input.toBuilder().syncState(SyncState.DELETED_LOCALLY).lastSyncAction(actionTimestamp).build();
                } else {
                    return input.toBuilder().syncState(SyncState.AHEAD).lastSyncAction(actionTimestamp).build();
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

    private Func1<Event<Tasks>, Observable<Tasks>> persistEventTasksLocally() {
        return new Func1<Event<Tasks>, Observable<Tasks>>() {
            @Override
            public Observable<Tasks> call(Event<Tasks> tasksEvent) {
                return localDataSource.saveTasks(tasksEvent.data().or(Tasks.empty()));
            }
        };
    }

    @Override
    public void complete(final Task originalTask) {
        save(originalTask.complete());
    }

    @Override
    public void activate(final Task originalTask) {
        save(originalTask.activate());
    }

    @Override
    public void save(final Task updatedTask) {
        final long actionTimestamp = clock.timeInMillis();
        remoteDataSource.saveTask(updatedTask)
                .compose(startAheadThenConfirmOrMarkAsError(updatedTask, actionTimestamp))
                .compose(persistIfMostRecentAction())
                .subscribeOn(scheduler)
                .subscribe();
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
            public SyncedData<Task> onConfirmedWithoutData() {
                return onError();
            }

            @Override
            public SyncedData<Task> onError() {
                return SyncedData.from(updatedTask, SyncState.SYNC_ERROR, actionTimestamp); //TODO add a retry logic for all Sync_Error states
            }
        });
    }

    private Observable.Transformer<SyncedData<Task>, SyncedData<Task>> persistIfMostRecentAction() {
        return new Observable.Transformer<SyncedData<Task>, SyncedData<Task>>() {
            @Override
            public Observable<SyncedData<Task>> call(Observable<SyncedData<Task>> observable) {
                return observable.flatMap(ifThenMap(new IfThenFlatMap<SyncedData<Task>, SyncedData<Task>>() {
                    @Override
                    public boolean ifMatches(SyncedData<Task> value) {
                        Tasks tasks = taskRelay.getValue().data().or(Tasks.empty());
                        return actionIsAbsentOrNoMoreRecentThan(tasks, value);
                    }

                    @Override
                    public Observable<SyncedData<Task>> thenMap(final SyncedData<Task> value) {
                        Event<Tasks> event = taskRelay.getValue();
                        Tasks tasks = event.data().or(Tasks.empty());
                        Tasks updatedTasks = tasks.save(value);
                        return Observable.just(event.updateData(updatedTasks))
                                .doOnNext(taskRelay)
                                .flatMap(new Func1<Event<Tasks>, Observable<SyncedData<Task>>>() {
                                    @Override
                                    public Observable<SyncedData<Task>> call(Event<Tasks> tasksEvent) {
                                        return localDataSource.saveTask(value);
                                    }
                                });
                    }

                    @Override
                    public Observable<SyncedData<Task>> elseMap(SyncedData<Task> value) {
                        return Observable.empty();
                    }
                }));
            }
        };
    }

    @Override
    public void delete(final Task task) {
        final long deleteActionTimestamp = clock.timeInMillis();
        remoteDataSource.deleteTask(task.id())
                .compose(deleteLocallyThenConfirmOrMarkAsError(task, deleteActionTimestamp))
                .flatMap(updateAndPersistIfDeleteActionIsMostRecent(task, deleteActionTimestamp))
                .subscribeOn(scheduler)
                .subscribe();
    }

    private Observable.Transformer<Void, Event<Tasks>> deleteLocallyThenConfirmOrMarkAsError(final Task task, final long deleteActionTimestamp) {
        return asOrchestratedAction(new DataOrchestrator<Void, Event<Tasks>>() {
            @Override
            public Event<Tasks> startWith() {
                Event<Tasks> currentState = taskRelay.getValue();
                return currentState.asLoadingWithData(markTaskAsDeletedLocallyAt(currentState, task, deleteActionTimestamp));
            }

            @Override
            public Event<Tasks> onConfirmed(Void value) {
                return onError();
            }

            @Override
            public Event<Tasks> onConfirmedWithoutData() {
                Event<Tasks> currentState = taskRelay.getValue();
                Tasks currentTasks = currentState.data().or(Tasks.empty());
                return currentState.updateData(currentTasks.remove(task)).asIdle();
            }

            @Override
            public Event<Tasks> onError() {
                Event<Tasks> currentState = taskRelay.getValue();
                Tasks currentTasks = currentState.data().or(Tasks.empty());
                Tasks markDeletedTaskAsError = mapFunctionToTasks(currentTasks, markTaskAsSyncErrorAt(task, deleteActionTimestamp));
                return currentState.updateData(markDeletedTaskAsError).asError(new SyncError());
            }
        });
    }

    private Func1<Event<Tasks>, Observable<Tasks>> updateAndPersistIfDeleteActionIsMostRecent(final Task task,
                                                                                              final long deleteActionTimestamp) {
        return ifThenMap(new IfThenFlatMap<Event<Tasks>, Tasks>() {
            @Override
            public boolean ifMatches(Event<Tasks> value) {
                Tasks currentTasks = taskRelay.getValue().data().or(Tasks.empty());
                return actionIsAbsentOrNoMoreRecentThan(currentTasks, task.id(), deleteActionTimestamp);
            }

            @Override
            public Observable<Tasks> thenMap(final Event<Tasks> tasksEvent) {
                Tasks tasks = tasksEvent.data().or(Tasks.empty());
                return Observable.just(tasksEvent.updateData(tasks))
                        .doOnNext(taskRelay)
                        .flatMap(new Func1<Event<Tasks>, Observable<Tasks>>() {
                            @Override
                            public Observable<Tasks> call(Event<Tasks> tasksEvent) {
                                return localDataSource.saveTasks(tasksEvent.data().or(Tasks.empty()));
                            }
                        });
            }

            @Override
            public Observable<Tasks> elseMap(Event<Tasks> value) {
                return Observable.empty();
            }
        });
    }

    private static boolean actionIsAbsentOrNoMoreRecentThan(Tasks currentTasks, Id id, long deleteActionTimestamp) {
        Optional<SyncedData<Task>> syncedDataOptional = currentTasks.get(id);
        return !syncedDataOptional.isPresent() || syncedDataOptional.get().lastSyncAction() <= deleteActionTimestamp;
    }

    private static Function<SyncedData<Task>, SyncedData<Task>> markTaskAsSyncErrorAt(final Task task, final long timeInMillis) {
        return new Function<SyncedData<Task>, SyncedData<Task>>() {
            @Override
            public SyncedData<Task> apply(SyncedData<Task> input) {
                if (input.data().id().equals(task.id())) {
                    return SyncedData.from(task, SyncState.SYNC_ERROR, timeInMillis);
                } else {
                    return input;
                }
            }
        };
    }

    private static Tasks markTaskAsDeletedLocallyAt(Event<Tasks> currentState, Task task, long timeInMillis) {
        if (currentState.data().isPresent()) {
            return currentState.data().get().save(SyncedData.from(task, SyncState.DELETED_LOCALLY, timeInMillis));
        } else {
            throw new RuntimeException("Trying to delete a task from empty data");
        }
    }

    private static boolean actionIsAbsentOrNoMoreRecentThan(Tasks currentTasks, SyncedData<Task> syncedData) {
        return actionIsAbsentOrNoMoreRecentThan(currentTasks, syncedData.data().id(), syncedData.lastSyncAction());
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
                        ).distinctUntilChanged();
            }
        };
    }

}
