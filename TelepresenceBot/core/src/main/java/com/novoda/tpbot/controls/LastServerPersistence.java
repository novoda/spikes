package com.novoda.tpbot.controls;

public interface LastServerPersistence {

    void saveLastConnectedServer(String serverAddress);

    boolean containsLastConnectedServer();

    String getLastConnectedServer();
}
