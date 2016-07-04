package com.novoda.todoapp.statistics.service;

import com.google.common.base.Optional;
import com.novoda.event.Event;
import com.novoda.todoapp.statistics.data.model.Statistics;
import com.novoda.todoapp.tasks.data.model.Tasks;
import com.novoda.todoapp.tasks.service.TasksService;

import rx.Observable;
import rx.functions.Func1;

public class TasksStatisticsService implements StatisticsService {

    private final TasksService tasksService;

    public TasksStatisticsService(TasksService tasksService) {
        this.tasksService = tasksService;
    }

    @Override
    public Observable<Event<Statistics>> getStatisticsEvent() {
        return tasksService.getTasksEvent().map(new Func1<Event<Tasks>, Event<Statistics>>() {
            @Override
            public Event<Statistics> call(Event<Tasks> tasksEvent) {
                return Event.<Statistics>builder()
                        .state(tasksEvent.state())
                        .error(tasksEvent.error())
                        .data(from(tasksEvent.data()))
                        .build();
            }
        });
    }

    private Optional<Statistics> from(Optional<Tasks> data) {
        Tasks tasks = data.or(Tasks.empty());
        return Optional.of(
                Statistics.builder()
                        .activeTasks(tasks.onlyActives().size())
                        .completedTasks(tasks.onlyCompleted().size())
                        .build()
        );
    }

}
