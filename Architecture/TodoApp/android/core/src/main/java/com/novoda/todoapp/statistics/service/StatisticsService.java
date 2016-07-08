package com.novoda.todoapp.statistics.service;

import com.novoda.event.Event;
import com.novoda.todoapp.statistics.data.model.Statistics;

import rx.Observable;

public interface StatisticsService {

    public Observable<Event<Statistics>> getStatisticsEvent();

}
