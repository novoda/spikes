package com.novoda.tpbot.human;

interface HumanView {

    void onConnect(String message);

    void onDisconnect();

    void onError(String message);

}
