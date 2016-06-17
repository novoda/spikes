package com.novoda.todoapp.tasks.data.source;

import com.novoda.data.SyncedData;
import com.novoda.todoapp.task.data.model.Task;
import com.novoda.todoapp.tasks.data.model.Tasks;

import rx.Observable;

public interface LocalTasksDataSource {

    Observable<Tasks> getTasks();

    Observable<Tasks> saveTasks(Tasks tasks);

    Observable<SyncedData<Task>> saveTask(SyncedData<Task> taskSyncedData);

    Observable<Void> deleteAllTasks();

}
