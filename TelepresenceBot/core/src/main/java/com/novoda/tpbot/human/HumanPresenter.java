package com.novoda.tpbot.human;

import com.novoda.tpbot.Direction;
import com.novoda.tpbot.Result;
import com.novoda.support.Observable;
import com.novoda.support.Observer;

import static com.novoda.support.Observable.unsubscribe;

class HumanPresenter {

    private final HumanTelepresenceService humanTelepresenceService;
    private final HumanView humanView;

    private Observable<Result> observable;

    HumanPresenter(HumanTelepresenceService humanTelepresenceService, HumanView humanView) {
        this.humanTelepresenceService = humanTelepresenceService;
        this.humanView = humanView;
    }

    void startPresenting(String serverAddress) {
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
                humanView.onConnect(result.message().get());
            }
        }

    }

}
