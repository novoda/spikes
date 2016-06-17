package com.novoda.todoapp.tasks.data.source;

import com.novoda.todoapp.task.data.model.Id;
import com.novoda.todoapp.task.data.model.Task;

import java.util.List;

import rx.Observable;

public interface RemoteTasksDataSource {

    Observable<List<Task>> getTasks();

    Observable<Task> getTask(Id taskId);

    Observable<Task> saveTask(Task task);

    Observable<List<Task>> saveTasks(List<Task> tasks);

    Observable<List<Task>> clearCompletedTasks();

    Observable<Void> deleteAllTasks();

    Observable<Void> deleteTask(Id taskId);

}
