package com.novoda.todoapp.tasks.service;

import com.novoda.data.SyncedData;
import com.novoda.event.Event;
import com.novoda.todoapp.task.data.model.Id;
import com.novoda.todoapp.task.data.model.Task;
import com.novoda.todoapp.tasks.data.model.Tasks;

import rx.Observable;
import rx.Scheduler;

public class TasksServiceObserveOn implements TasksService {

    private final TasksService tasksService;
    private final Scheduler scheduler;

    public TasksServiceObserveOn(TasksService tasksService, Scheduler scheduler) {
        this.tasksService = tasksService;
        this.scheduler = scheduler;
    }

    @Override
    public Observable<Event<Tasks>> getTasksEvent() {
        return tasksService.getTasksEvent()
                .observeOn(scheduler);
    }

    @Override
    public Observable<Event<Tasks>> getCompletedTasksEvent() {
        return tasksService.getCompletedTasksEvent()
                .observeOn(scheduler);
    }

    @Override
    public Observable<Event<Tasks>> getActiveTasksEvent() {
        return tasksService.getActiveTasksEvent()
                .observeOn(scheduler);
    }

    @Override
    public Observable<SyncedData<Task>> getTask(Id taskId) {
        return tasksService.getTask(taskId)
                .observeOn(scheduler);
    }

    @Override
    public void refreshTasks() {
        tasksService.refreshTasks();
    }

    @Override
    public void clearCompletedTasks() {
        tasksService.clearCompletedTasks();
    }

    @Override
    public void complete(Task task) {
        tasksService.complete(task);
    }

    @Override
    public void activate(Task task) {
        tasksService.activate(task);
    }

    @Override
    public void save(Task task) {
        tasksService.save(task);
    }

    @Override
    public void delete(Task task) {
        tasksService.delete(task);
    }

    @Override
    public void deleteAllTasks() {
        tasksService.deleteAllTasks();
    }

}
