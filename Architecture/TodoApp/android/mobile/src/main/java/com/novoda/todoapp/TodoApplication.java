package com.novoda.todoapp;

import android.app.Application;

import com.novoda.todoapp.statistics.service.StatisticsService;
import com.novoda.todoapp.statistics.service.TasksStatisticsService;
import com.novoda.todoapp.tasks.data.AlwaysOutOfDateTasksFreshnessChecker;
import com.novoda.todoapp.tasks.data.source.InMemoryLocalTaskDataSource;
import com.novoda.todoapp.tasks.data.source.InMemoryRemoteTaskDataSource;
import com.novoda.todoapp.tasks.service.Clock;
import com.novoda.todoapp.tasks.service.PersistedTasksService;
import com.novoda.todoapp.tasks.service.TasksService;
import com.novoda.todoapp.tasks.service.TasksServiceObserveOn;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TodoApplication extends Application {

    //TODO use proper dependency injection
    public static final TasksService TASKS_SERVICE = new TasksServiceObserveOn(
            new PersistedTasksService(
                    InMemoryLocalTaskDataSource.newInstance(),
                    InMemoryRemoteTaskDataSource.newInstance(),
                    new AlwaysOutOfDateTasksFreshnessChecker(),
                    new Clock(),
                    Schedulers.io()
            ),
            AndroidSchedulers.mainThread()
    );

    //TODO use proper dependency injection
    public static final StatisticsService STATISTICS_SERVICE = new TasksStatisticsService(TASKS_SERVICE);

}
