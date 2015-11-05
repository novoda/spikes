package com.novoda.easycustomtabs.connection;

interface ServiceConnectionCallback {

    void onServiceConnected(ConnectedClient client);

    void onServiceDisconnected();

}
