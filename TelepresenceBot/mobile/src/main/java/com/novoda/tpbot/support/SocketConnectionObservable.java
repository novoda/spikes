package com.novoda.tpbot.support;

import android.os.Handler;

import com.novoda.tpbot.Result;

import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.client.SocketIOException;
import io.socket.emitter.Emitter;

import static io.socket.client.Socket.EVENT_CONNECT_ERROR;

public class SocketConnectionObservable extends Observable<Result> {

    private final Socket socket;
    private final Handler handler;
    private final Event event;

    public SocketConnectionObservable(Socket socket, Handler handler, Event event) {
        this.socket = socket;
        this.handler = handler;
        this.event = event;
    }

    @Override
    public Observable<Result> start() {
        if (socket.connected()) {
            notifyOf(Result.from("Already connected"));
            return this;
        }

        socket.emit(event.rawEvent(), "", joinSuccessAcknowledgementListener);
        socket.on(EVENT_CONNECT_ERROR, connectionTimeoutListener);
        socket.connect();
        return this;
    }

    private final Ack joinSuccessAcknowledgementListener = new Ack() {
        @Override
        public void call(final Object... args) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (args[0] != null && args[0] instanceof String) {
                        String acknowledgment = (String) args[0];
                        notifyOf(Result.from(acknowledgment));
                    }
                }
            });
        }
    };

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

}
