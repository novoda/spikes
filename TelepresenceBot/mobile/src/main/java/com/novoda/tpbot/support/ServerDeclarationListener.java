package com.novoda.tpbot.support;

import com.novoda.notils.logger.simple.Log;

public interface ServerDeclarationListener {

    ServerDeclarationListener NO_OP = new ServerDeclarationListener() {
        @Override
        public void onConnect(String serverAddress) {
            Log.w("onConnect() but no listener was set");
        }
    };

    void onConnect(String serverAddress);
}
