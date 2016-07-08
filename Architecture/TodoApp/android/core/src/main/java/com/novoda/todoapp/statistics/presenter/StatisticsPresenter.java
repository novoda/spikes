package com.novoda.todoapp.statistics.presenter;

import com.novoda.event.DataObserver;
import com.novoda.todoapp.statistics.data.model.Statistics;
import com.novoda.todoapp.statistics.displayer.StatisticsDisplayer;
import com.novoda.todoapp.statistics.service.StatisticsService;

import rx.subscriptions.CompositeSubscription;

import static com.novoda.event.EventFunctions.asData;

public class StatisticsPresenter {

    private final StatisticsService statisticsService;
    private final StatisticsDisplayer statisticsDisplayer;

    private CompositeSubscription subscriptions = new CompositeSubscription();

    public StatisticsPresenter(StatisticsService statisticsService, StatisticsDisplayer statisticsDisplayer) {
        this.statisticsService = statisticsService;
        this.statisticsDisplayer = statisticsDisplayer;
    }

    public void startPresenting() {
        subscriptions.add(
                statisticsService.getStatisticsEvent()
                        .compose(asData(Statistics.class))
                        .subscribe(new StatisticsObserver())
        );
    }

    public void stopPresenting() {
        subscriptions.clear();
        subscriptions = new CompositeSubscription();
    }

    private class StatisticsObserver extends DataObserver<Statistics> {
        @Override
        public void onNext(Statistics statistics) {
            statisticsDisplayer.display(statistics);
        }
    }

}
