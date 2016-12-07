package com.novoda.tpbot.human;

interface HumanView {

    void onConnection(String message);

    void onDisconnect();

    void onError(String message);

}
