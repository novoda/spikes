package com.novoda.tpbot.human;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

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

import static io.socket.client.Socket.*;

public class HumanSocketIOTpService implements HumanTpService {

    private Socket socket;
    private final Handler handler;

    public static HumanSocketIOTpService getInstance() {
        return LazySingleton.INSTANCE;
    }

    private HumanSocketIOTpService() {
        this.handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public Observable<Result> connectTo(String serverAddress) {
        try {
            URL url = new URL(serverAddress);
            socket = IO.socket(url.toExternalForm());
        } catch (MalformedURLException | URISyntaxException exception) {
            return Observable.just(Result.from(exception));
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

            socket.emit("join_as_human", "", new Ack() {
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
            });

            socket.on(EVENT_CONNECT, genericEmitterListener)
                    .on(EVENT_CONNECTING, genericEmitterListener)
                    .on(EVENT_CONNECT_ERROR, new Listener() {
                        @Override
                        public void call(Object... args) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    notifyOf(Result.from(new SocketIOException("connection timeout")));
                                }
                            });
                        }
                    });

            socket.connect();
            return this;
        }
    }

    private final Emitter.Listener genericEmitterListener = new Listener() {
        @Override
        public void call(Object... args) {
            Log.d(HumanSocketIOTpService.class.getSimpleName(), " logging");
        }
    };

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
        private static final HumanSocketIOTpService INSTANCE = new HumanSocketIOTpService();
    }
}
