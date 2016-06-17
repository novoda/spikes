package com.novoda.todoapp;

import android.app.Application;

import com.novoda.todoapp.tasks.data.AlwaysOutOfDateTasksFreshnessChecker;
import com.novoda.todoapp.tasks.data.source.InMemoryLocalTaskDataSource;
import com.novoda.todoapp.tasks.data.source.InMemoryRemoteTaskDataSource;
import com.novoda.todoapp.tasks.service.Clock;
import com.novoda.todoapp.tasks.service.PersistedTasksService;
import com.novoda.todoapp.tasks.service.TasksService;
import com.novoda.todoapp.tasks.service.TasksServiceAsync;

public class TodoApplication extends Application {

    //TODO use proper dependency injection
    public static final TasksService TASKS_SERVICE = new TasksServiceAsync(
            new PersistedTasksService(
                    InMemoryLocalTaskDataSource.newInstance(),
                    InMemoryRemoteTaskDataSource.newInstance(),
                    new AlwaysOutOfDateTasksFreshnessChecker(),
                    new Clock()
            )
    );

}
