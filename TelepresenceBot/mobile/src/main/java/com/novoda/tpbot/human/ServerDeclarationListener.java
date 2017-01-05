package com.novoda.tpbot.human;

import com.novoda.notils.logger.simple.Log;

interface ServerDeclarationListener {

    ServerDeclarationListener NO_OP = new ServerDeclarationListener() {
        @Override
        public void onConnect(String serverAddress) {
            Log.w("onConnect() but no listener was set");
        }
    };

    void onConnect(String serverAddress);
}
