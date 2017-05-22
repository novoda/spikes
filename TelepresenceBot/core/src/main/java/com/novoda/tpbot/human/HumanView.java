package com.novoda.tpbot.human;

interface HumanView {

    void onConnect(String message, String serverAddress);

    void onDisconnect();

    void onError(String message);

}
