package com.novoda.tpbot.human;

import com.novoda.tpbot.Result;
import com.novoda.tpbot.support.Observable;
import com.novoda.tpbot.support.Observer;

public class HumanPresenter {

    private final HumanTpService humanTpService;
    private final HumanView humanView;

    private Observable<Result> observable;

    public HumanPresenter(HumanTpService humanTpService, HumanView humanView) {
        this.humanTpService = humanTpService;
        this.humanView = humanView;
    }

    public void startPresenting(String serverAddress) {
        observable = humanTpService.connectTo(serverAddress)
                .attach(new ConnectionObserver())
                .start();
    }

    public void stopPresenting() {
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
