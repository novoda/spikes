package com.novoda.tpbot.bot;

import com.novoda.tpbot.Direction;
import com.novoda.tpbot.Result;
import com.novoda.tpbot.support.Observable;
import com.novoda.tpbot.support.Observer;

class BotPresenter {

    private final BotTpService tpService;
    private final BotView botView;

    private Observable<Result> connectionObservable;
    private Observable<Direction> directionObservable;

    BotPresenter(BotTpService tpService, BotView botView) {
        this.tpService = tpService;
        this.botView = botView;
    }

    void startPresenting() {
        connectionObservable = tpService.connect()
                .attach(new ConnectionObserver())
                .start();
    }

    void stopPresenting() {
        if (connectionObservable != null) {
            connectionObservable.detachObservers();
        }

        if (directionObservable != null) {
            directionObservable.detachObservers();
        }

        tpService.disconnect();
    }

    void startListeningForDirection() {
        directionObservable = tpService.listen()
                .attach(new DirectionObserver())
                .start();
    }

    private class ConnectionObserver implements Observer<Result> {

        @Override
        public void update(Result result) {
            if (result.isError()) {
                botView.onError(result.exception().get().getMessage());
            } else {
                botView.onConnect(result.message().get());
            }
        }

    }

    private class DirectionObserver implements Observer<Direction> {

        @Override
        public void update(Direction direction) {
            botView.moveIn(direction);
        }

    }

}
