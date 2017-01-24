package com.novoda.tpbot.human;

import android.os.Handler;
import android.os.Looper;

import com.novoda.tpbot.Direction;
import com.novoda.tpbot.Result;
import com.novoda.tpbot.support.Observable;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.client.SocketIOException;
import io.socket.emitter.Emitter;

import static io.socket.client.Socket.EVENT_CONNECT_ERROR;
import static io.socket.emitter.Emitter.Listener;

class SocketIOTpService implements HumanTpService {

    private static final String EVENT_CONNECT = "join_as_human";

    private Socket socket;
    private final Handler handler;

    static SocketIOTpService getInstance() {
        return LazySingleton.INSTANCE;
    }

    private SocketIOTpService() {
        this.handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public Observable<Result> connectTo(String serverAddress) {
        try {
            URL url = new URL(serverAddress);
            socket = IO.socket(url.toExternalForm());
        } catch (MalformedURLException | URISyntaxException exception) {
            String message = "Address should be in the format `http://[ip_address]:[port_number]`";
            HumanSocketIOConnectionException exceptionWithUserFacingMessage = new HumanSocketIOConnectionException(message, exception);
            return Observable.just(Result.from(exceptionWithUserFacingMessage));
        }
        return new SocketConnectionObservable();
    }

    private class SocketConnectionObservable extends Observable<Result> {

        @Override
        public Observable<Result> start() {
            if (socket.connected()) {
                notifyOf(Result.from("Already connected"));
                return this;
            }

            socket.emit(EVENT_CONNECT, "", joinSuccessAcknowledgementListener);
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
                        if (args[0] != null) {
                            Object object = args[0];
                            notifyOf(Result.from(object.toString()));
                        }
                    }
                });
            }
        };

        private final Emitter.Listener connectionTimeoutListener = new Listener() {
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

    @Override
    public void moveIn(Direction direction) {
        socket.emit("move_in", direction);
    }

    @Override
    public void disconnect() {
        if (socket != null) {
            socket.disconnect();
            socket.off();
        }
    }

    private static class LazySingleton {
        private static final SocketIOTpService INSTANCE = new SocketIOTpService();
    }

    private class HumanSocketIOConnectionException extends IllegalArgumentException {

        private HumanSocketIOConnectionException(String message, Throwable throwable) {
            super(message, throwable);
        }

    }

}
