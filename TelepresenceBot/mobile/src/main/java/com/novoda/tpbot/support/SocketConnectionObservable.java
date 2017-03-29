package com.novoda.tpbot.support;

import android.os.Handler;

import com.novoda.tpbot.Result;
import com.novoda.tpbot.Room;

import io.socket.client.Socket;
import io.socket.client.SocketIOException;
import io.socket.emitter.Emitter;

import static io.socket.client.Socket.*;

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
        socket.on(EVENT_ERROR, connectionErrorListener);
        socket.on(EVENT_CONNECT, connectionEstablishedListener);
        socket.on(EVENT_DISCONNECT, disconnectionListener);
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

    private final Emitter.Listener connectionErrorListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (args[0] != null && args[0] instanceof String) {
                        String errorMessage = String.valueOf(args[0]);
                        notifyOf(Result.from(new SocketIOException(errorMessage)));
                    }
                }
            });
        }
    };

    private final Emitter.Listener connectionEstablishedListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    notifyOf(Result.from(Room.LONDON.name().toLowerCase()));
                }
            });
        }
    };

    private final Emitter.Listener disconnectionListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    notifyOf(Result.from(new SocketIOException("Disconnected")));
                }
            });
        }
    };

}
