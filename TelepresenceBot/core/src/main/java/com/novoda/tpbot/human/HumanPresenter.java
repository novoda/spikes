package com.novoda.tpbot.human;

import com.novoda.tpbot.Result;
import com.novoda.tpbot.support.Observable;
import com.novoda.tpbot.support.Observer;

class HumanPresenter {

    private final HumanTpService humanTpService;
    private final HumanView humanView;

    private Observable<Result> observable;

    HumanPresenter(HumanTpService humanTpService, HumanView humanView) {
        this.humanTpService = humanTpService;
        this.humanView = humanView;
    }

    void startPresenting(String serverAddress) {
        observable = humanTpService.connectTo(serverAddress)
                .attach(new ConnectionObserver())
                .start();
    }

    void stopPresenting() {
        observable.detachObservers();
        humanTpService.disconnect();
    }

    private class ConnectionObserver implements Observer<Result> {

        @Override
        public void update(Result result) {
            if (result.isError()) {
                humanView.onError(result.exception().get().getMessage());
                humanView.onDisconnect();
            } else {
                humanView.onConnect(result.message().get());
            }
        }

    }

}
