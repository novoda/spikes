package com.novoda.todoapp.tasks.service;

import com.novoda.data.SyncedData;
import com.novoda.event.Event;
import com.novoda.todoapp.task.data.model.Id;
import com.novoda.todoapp.task.data.model.Task;
import com.novoda.todoapp.tasks.data.model.Tasks;

import rx.Observable;

public interface TasksService {

    Observable<Event<Tasks>> getTasksEvent();

    Observable<Event<Tasks>> getCompletedTasksEvent();

    Observable<Event<Tasks>> getActiveTasksEvent();

    Observable<SyncedData<Task>> getTask(Id taskId);

    void refreshTasks();

    void clearCompletedTasks();

    void complete(Task task);

    void activate(Task task);

    void save(Task task);

    void delete(Task task);

    void deleteAllTasks();

}
