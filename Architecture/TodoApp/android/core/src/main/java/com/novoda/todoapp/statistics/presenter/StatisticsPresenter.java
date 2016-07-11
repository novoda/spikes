package com.novoda.todoapp.statistics.presenter;

import com.novoda.event.DataObserver;
import com.novoda.todoapp.navigation.TopLevelMenuActionListener;
import com.novoda.todoapp.navigation.TopLevelMenuDisplayer;
import com.novoda.todoapp.navigation.Navigator;
import com.novoda.todoapp.statistics.data.model.Statistics;
import com.novoda.todoapp.statistics.displayer.StatisticsDisplayer;
import com.novoda.todoapp.statistics.service.StatisticsService;

import rx.subscriptions.CompositeSubscription;

import static com.novoda.event.EventFunctions.asData;

public class StatisticsPresenter {

    private final StatisticsService statisticsService;
    private final StatisticsDisplayer statisticsDisplayer;
    private final TopLevelMenuDisplayer topLevelMenuDisplayer;
    private final Navigator navigator;

    private CompositeSubscription subscriptions = new CompositeSubscription();

    public StatisticsPresenter(StatisticsService statisticsService,
                               StatisticsDisplayer statisticsDisplayer,
                               TopLevelMenuDisplayer topLevelMenuDisplayer,
                               Navigator navigator) {
        this.statisticsService = statisticsService;
        this.statisticsDisplayer = statisticsDisplayer;
        this.topLevelMenuDisplayer = topLevelMenuDisplayer;
        this.navigator = navigator;
    }

    public void startPresenting() {
        subscriptions.add(
                statisticsService.getStatisticsEvent()
                        .compose(asData(Statistics.class))
                        .subscribe(new StatisticsObserver())
        );
        topLevelMenuDisplayer.attach(topLevelMenuActionListener);
    }

    public void stopPresenting() {
        subscriptions.clear();
        subscriptions = new CompositeSubscription();
        topLevelMenuDisplayer.detach();
    }

    private class StatisticsObserver extends DataObserver<Statistics> {
        @Override
        public void onNext(Statistics statistics) {
            statisticsDisplayer.display(statistics);
        }
    }

    final TopLevelMenuActionListener topLevelMenuActionListener = new TopLevelMenuActionListener() {
        @Override
        public void onToDoListItemSelected() {
            topLevelMenuDisplayer.closeMenu();
            navigator.toTasksList();
        }

        @Override
        public void onStatisticsItemSelected() {
            topLevelMenuDisplayer.closeMenu();
        }
    };
}
