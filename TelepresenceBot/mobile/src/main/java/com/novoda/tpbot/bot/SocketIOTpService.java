package com.novoda.tpbot.bot;

import android.os.Handler;
import android.os.Looper;

import com.novoda.tpbot.Direction;
import com.novoda.tpbot.Result;
import com.novoda.tpbot.support.MalformedServerAddressException;
import com.novoda.tpbot.support.Observable;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.client.SocketIOException;

import static io.socket.client.Socket.EVENT_CONNECT_ERROR;
import static io.socket.emitter.Emitter.Listener;

class SocketIOTpService implements BotTpService {

    private static final String EVENT_CONNECT = "join_as_human";
    private static final String EVENT_MOVE = "move_in";

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
            MalformedServerAddressException exceptionWithUserFacingMessage = new MalformedServerAddressException(exception);
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
                        if (args[0] != null && args[0] instanceof String) {
                            String acknowledgment = (String) args[0];
                            notifyOf(Result.from(acknowledgment));
                        }
                    }
                });
            }
        };

        private final Listener connectionTimeoutListener = new Listener() {
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
    public Observable<Direction> listen() {
        return null;
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

}
