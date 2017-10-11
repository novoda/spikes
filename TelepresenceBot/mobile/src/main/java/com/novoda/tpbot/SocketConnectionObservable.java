package com.novoda.tpbot;

import com.novoda.tpbot.model.Result;
import com.novoda.tpbot.model.Room;
import com.novoda.tpbot.support.Observable;
import com.novoda.tpbot.threading.Executor;

import io.socket.client.Socket;
import io.socket.client.SocketIOException;
import io.socket.emitter.Emitter;

import static io.socket.client.Socket.*;

public class SocketConnectionObservable extends Observable<Result> {

    private final Socket socket;
    private final Executor executor;

    public SocketConnectionObservable(Socket socket, Executor executor) {
        this.socket = socket;
        this.executor = executor;
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
            executor.execute(new Executor.Action() {
                @Override
                public void perform() {
                    notifyOf(Result.from(new SocketIOException("connection timeout")));
                }
            });
        }
    };

    private final Emitter.Listener connectionErrorListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            executor.execute(new Executor.Action() {
                @Override
                public void perform() {
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
            executor.execute(new Executor.Action() {
                @Override
                public void perform() {
                    notifyOf(Result.from(Room.LONDON.name().toLowerCase()));
                }
            });
        }
    };

    private final Emitter.Listener disconnectionListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            executor.execute(new Executor.Action() {
                @Override
                public void perform() {
                    notifyOf(Result.from(new SocketIOException("Disconnected")));

                }
            });
        }
    };

}
