package com.novoda.todoapp.tasks.service;

import com.novoda.data.SyncedData;
import com.novoda.event.Event;
import com.novoda.todoapp.task.data.model.Id;
import com.novoda.todoapp.task.data.model.Task;
import com.novoda.todoapp.tasks.data.model.Tasks;

import rx.Observable;
import rx.functions.Action0;

public interface TasksService {

    Observable<Tasks> getTasks();

    Observable<Event<Tasks>> getTasksEvents();

    Observable<Tasks> getCompletedTasks();

    Observable<Event<Tasks>> getCompletedTasksEvents();

    Observable<Tasks> getActiveTasks();

    Observable<Event<Tasks>> getActiveTasksEvents();

    Observable<SyncedData<Task>> getTask(Id taskId);

    Action0 refreshTasks();

    Action0 clearCompletedTasks();

    Action0 deleteAllTasks();

    Action0 complete(Task task);

    Action0 activate(Task task);

    Action0 save(Task task);

}
