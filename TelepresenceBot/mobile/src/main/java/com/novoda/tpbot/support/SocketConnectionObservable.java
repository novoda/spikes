package com.novoda.tpbot.support;

import android.os.Handler;

import com.novoda.tpbot.Result;

import io.socket.client.Socket;
import io.socket.client.SocketIOException;
import io.socket.emitter.Emitter;

import static io.socket.client.Socket.EVENT_CONNECT;
import static io.socket.client.Socket.EVENT_CONNECT_ERROR;

public class SocketConnectionObservable extends Observable<Result> {

    private final Socket socket;
    private final Handler handler;

    public SocketConnectionObservable(Socket socket, Handler handler) {
        this.socket = socket;
        this.handler = handler;
    }

    @Override
    public Observable<Result> start() {
        socket.on(EVENT_CONNECT_ERROR, connectionTimeoutListener);
        socket.on(EVENT_CONNECT, connectionEstablishedListener);
        socket.connect();
        return this;
    }

    private final Emitter.Listener connectionTimeoutListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    notifyOf(Result.from(new SocketIOException("connection timeout")));
                }
            });
        }
    };

    private final Emitter.Listener connectionEstablishedListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            notifyOf(Result.from("Successful connection"));
        }
    };
}
