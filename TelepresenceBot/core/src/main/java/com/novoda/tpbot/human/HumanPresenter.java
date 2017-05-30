package com.novoda.tpbot.human;

import com.novoda.tpbot.Direction;
import com.novoda.tpbot.Result;
import com.novoda.support.Observable;
import com.novoda.support.Observer;
import com.novoda.tpbot.controls.LastServerPersistence;

import static com.novoda.support.Observable.unsubscribe;

class HumanPresenter {

    private final HumanTelepresenceService humanTelepresenceService;
    private final HumanView humanView;
    private final LastServerPersistence lastServerPersistence;

    private Observable<Result> observable;
    private String serverAddress;

    HumanPresenter(HumanTelepresenceService humanTelepresenceService, HumanView humanView, LastServerPersistence lastServerPersistence) {
        this.humanTelepresenceService = humanTelepresenceService;
        this.humanView = humanView;
        this.lastServerPersistence = lastServerPersistence;
    }

    void startPresenting(String serverAddress) {
        this.serverAddress = serverAddress;
        observable = humanTelepresenceService.connectTo(serverAddress)
                .attach(new ConnectionObserver())
                .start();
    }

    void moveIn(Direction direction) {
        humanTelepresenceService.moveIn(direction);
    }

    void stopPresenting() {
        unsubscribe(observable);
        humanTelepresenceService.disconnect();
        humanView.onDisconnect();
    }

    private class ConnectionObserver implements Observer<Result> {

        @Override
        public void update(Result result) {
            if (result.isError()) {
                humanView.onError(result.exception().get().getMessage());
            } else {
                lastServerPersistence.saveLastConnectedServer(serverAddress);
                humanView.onConnect(result.message().get());
            }
        }

    }

}
