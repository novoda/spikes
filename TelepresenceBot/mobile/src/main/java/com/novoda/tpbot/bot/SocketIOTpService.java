package com.novoda.tpbot.bot;

import android.os.Handler;
import android.os.Looper;

import com.novoda.tpbot.Direction;
import com.novoda.tpbot.Result;
import com.novoda.tpbot.support.ClientType;
import com.novoda.tpbot.support.Event;
import com.novoda.tpbot.support.MalformedServerAddressException;
import com.novoda.tpbot.support.Observable;
import com.novoda.tpbot.support.SocketConnectionObservable;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

class SocketIOTpService implements BotTpService {

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
            IO.Options options = new IO.Options();
            options.query = ClientType.BOT.rawClientType();
            socket = IO.socket(url.toExternalForm(), options);
        } catch (MalformedURLException | URISyntaxException exception) {
            MalformedServerAddressException exceptionWithUserFacingMessage = new MalformedServerAddressException(exception);
            return Observable.just(Result.from(exceptionWithUserFacingMessage));
        }
        return new SocketConnectionObservable(socket, handler);
    }

    @Override
    public Observable<Direction> listen() {
        return new DirectionObservable();
    }

    private class DirectionObservable extends Observable<Direction> {

        @Override
        public Observable<Direction> start() {
            if (!socket.connected()) {
                return this;
            }

            socket.on(Event.DIRECTION.rawEvent(), directionListener);
            return this;
        }

        private final Emitter.Listener directionListener = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (args[0] != null && args[0] instanceof String) {
                            String rawDirection = (String) args[0];
                            notifyOf(Direction.from(rawDirection));
                        }
                    }
                });
            }
        };
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
