package com.novoda.todoapp.tasks.data.model;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.novoda.data.ImmutableMapWithCopy;
import com.novoda.data.SyncState;
import com.novoda.data.SyncedData;
import com.novoda.todoapp.task.data.model.Id;
import com.novoda.todoapp.task.data.model.Task;

import java.util.Collection;

@AutoValue
public abstract class Tasks {

    public static Tasks empty() {
        return from(ImmutableMapWithCopy.<Id, SyncedData<Task>>empty());
    }

    public static Tasks asSynced(Collection<Task> tasks, long syncActionTimestamp) {
        ImmutableMap.Builder<Id, SyncedData<Task>> builder = ImmutableMap.builder();
        for (Task task : tasks) {
            builder.put(task.id(), SyncedData.from(task, SyncState.IN_SYNC, syncActionTimestamp));
        }
        return from(ImmutableMapWithCopy.from(builder.build()));
    }

    static Tasks from(ImmutableMapWithCopy<Id, SyncedData<Task>> map) {
        return new AutoValue_Tasks(map);
    }

    Tasks() {
        // AutoValue best practices https://github.com/google/auto/blob/master/value/userguide/practices.md
    }

    abstract ImmutableMapWithCopy<Id, SyncedData<Task>> internalMap();

    public boolean isEmpty() {
        return internalMap().isEmpty();
    }

    public boolean isMostRecentAction(SyncedData<Task> syncedData) {
        SyncedData<Task> currentSyncData = internalMap().get(syncedData.data().id());
        return currentSyncData == null || syncedData.lastSyncAction() >= currentSyncData.lastSyncAction();
    }

    public Tasks save(SyncedData<Task> taskSyncedData) {
        return from(internalMap().put(taskSyncedData.data().id(), taskSyncedData));
    }

    public ImmutableList<SyncedData<Task>> all() {
        return ImmutableList.copyOf(internalMap().values());
    }

    public int size() {
        return internalMap().size();
    }

    public SyncedData<Task> syncedDataFor(Id taskId) {
        return internalMap().get(taskId);
    }

    public boolean containsTask(Id taskId) {
        return internalMap().containsKey(taskId);
    }
}
