package com.novoda.tpbot.bot;

import com.novoda.tpbot.Direction;
import com.novoda.tpbot.Result;
import com.novoda.tpbot.support.Observable;
import com.novoda.tpbot.support.Observer;

import static com.novoda.tpbot.support.Observable.unsubscribe;

class BotPresenter {

    private final BotTelepresenceService tpService;
    private final BotView botView;

    private Observable<Result> connectionObservable;
    private Observable<Direction> directionObservable;

    BotPresenter(BotTelepresenceService tpService, BotView botView) {
        this.tpService = tpService;
        this.botView = botView;
    }

    void startPresenting(String serverAddress) {
        connectionObservable = tpService.connectTo(serverAddress)
                .attach(new ConnectionObserver())
                .start();
    }

    void stopPresenting() {
        unsubscribe(connectionObservable);
        unsubscribe(directionObservable);
        tpService.disconnect();
        botView.onDisconnect();
    }

    private class ConnectionObserver implements Observer<Result> {

        @Override
        public void update(Result result) {
            if (result.isError()) {
                botView.onError(result.exception().get().getMessage());
            } else {
                botView.onConnect(result.message().get());

                directionObservable = tpService.listen()
                        .attach(new DirectionObserver())
                        .start();
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
