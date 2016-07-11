package com.novoda.todoapp.statistics.presenter;

import com.novoda.event.DataObserver;
import com.novoda.todoapp.navigation.NavDrawerActionListener;
import com.novoda.todoapp.navigation.NavDrawerDisplayer;
import com.novoda.todoapp.navigation.Navigator;
import com.novoda.todoapp.statistics.data.model.Statistics;
import com.novoda.todoapp.statistics.displayer.StatisticsDisplayer;
import com.novoda.todoapp.statistics.service.StatisticsService;

import rx.subscriptions.CompositeSubscription;

import static com.novoda.event.EventFunctions.asData;

public class StatisticsPresenter {

    private final StatisticsService statisticsService;
    private final StatisticsDisplayer statisticsDisplayer;
    private final NavDrawerDisplayer navDrawerDisplayer;
    private final Navigator navigator;

    private CompositeSubscription subscriptions = new CompositeSubscription();

    public StatisticsPresenter(StatisticsService statisticsService,
                               StatisticsDisplayer statisticsDisplayer,
                               NavDrawerDisplayer navDrawerDisplayer,
                               Navigator navigator) {
        this.statisticsService = statisticsService;
        this.statisticsDisplayer = statisticsDisplayer;
        this.navDrawerDisplayer = navDrawerDisplayer;
        this.navigator = navigator;
    }

    public void startPresenting() {
        subscriptions.add(
                statisticsService.getStatisticsEvent()
                        .compose(asData(Statistics.class))
                        .subscribe(new StatisticsObserver())
        );
        navDrawerDisplayer.attach(navDrawerActionListener);
    }

    public void stopPresenting() {
        subscriptions.clear();
        subscriptions = new CompositeSubscription();
        navDrawerDisplayer.detach();
    }

    private class StatisticsObserver extends DataObserver<Statistics> {
        @Override
        public void onNext(Statistics statistics) {
            statisticsDisplayer.display(statistics);
        }
    }

    final NavDrawerActionListener navDrawerActionListener = new NavDrawerActionListener() {
        @Override
        public void onToDoListNavDrawerItemSelected() {
            navDrawerDisplayer.closeNavDrawer();
            navigator.toTasksList();
        }

        @Override
        public void onStatisticsNavDrawerItemSelected() {
            navDrawerDisplayer.closeNavDrawer();
        }
    };
}
