package com.novoda.tpbot;

public interface ServerDeclarationListener {

    ServerDeclarationListener NO_OP = new ServerDeclarationListener() {
        @Override
        public void onConnect(String serverAddress) {
            // no-op.
        }
    };

    void onConnect(String serverAddress);
}
