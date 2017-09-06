package com.novoda.tpbot;

public interface ServiceDeclarationListener {

    ServiceDeclarationListener NO_OP = new ServiceDeclarationListener() {
        @Override
        public void onServiceConnected(String serverAddress) {
            // no-op.
        }
    };

    void onServiceConnected(String serverAddress);
}
