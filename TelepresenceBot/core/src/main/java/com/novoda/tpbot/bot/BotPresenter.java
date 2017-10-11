package com.novoda.tpbot.bot;

import com.novoda.tpbot.support.Observable;
import com.novoda.tpbot.support.Observer;
import com.novoda.tpbot.Direction;
import com.novoda.tpbot.LastServerPersistence;
import com.novoda.tpbot.Result;

import static com.novoda.tpbot.support.Observable.unsubscribe;

class BotPresenter {

    private final BotTelepresenceService tpService;
    private final BotView botView;
    private final LastServerPersistence lastServerPersistence;
    private final String serverAddress;

    private Observable<Result> connectionObservable;
    private Observable<Direction> directionObservable;

    BotPresenter(BotTelepresenceService tpService, BotView botView, LastServerPersistence lastServerPersistence, String serverAddress) {
        this.tpService = tpService;
        this.botView = botView;
        this.lastServerPersistence = lastServerPersistence;
        this.serverAddress = serverAddress;
    }

    void startPresenting() {
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
                lastServerPersistence.saveLastConnectedServer(serverAddress);
                botView.onConnect(result.message().get(), serverAddress);

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
