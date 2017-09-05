package com.novoda.tpbot;

public interface LastServerPersistence {

    void saveLastConnectedServer(String serverAddress);

    boolean containsLastConnectedServer();

    String getLastConnectedServer();
}
