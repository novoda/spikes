package com.novoda.tpbot.bot.service;

import com.novoda.tpbot.support.Observable;
import com.novoda.tpbot.ClientType;
import com.novoda.tpbot.Direction;
import com.novoda.tpbot.Event;
import com.novoda.tpbot.Executor;
import com.novoda.tpbot.MainLooperExecutor;
import com.novoda.tpbot.MalformedServerAddressException;
import com.novoda.tpbot.Result;
import com.novoda.tpbot.Room;
import com.novoda.tpbot.SocketConnectionObservable;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

class SocketIOTelepresenceService implements BotTelepresenceService {

    private Socket socket;
    private final Executor executor;

    static SocketIOTelepresenceService getInstance() {
        return LazySingleton.INSTANCE;
    }

    private SocketIOTelepresenceService() {
        this.executor = MainLooperExecutor.newInstance();
    }

    @Override
    public Observable<Result> connectTo(String serverAddress) {
        try {
            URL url = new URL(serverAddress);
            IO.Options options = new IO.Options();
            options.query = ClientType.BOT.rawQuery() + "&" + Room.LONDON.rawQuery();
            socket = IO.socket(url.toExternalForm(), options);
        } catch (MalformedURLException | URISyntaxException exception) {
            MalformedServerAddressException exceptionWithUserFacingMessage = new MalformedServerAddressException(exception);
            return Observable.just(Result.from(exceptionWithUserFacingMessage));
        }
        return new SocketConnectionObservable(socket, executor);
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
                executor.execute(new Executor.Action() {
                    @Override
                    public void perform() {
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
        private static final SocketIOTelepresenceService INSTANCE = new SocketIOTelepresenceService();
    }

}
