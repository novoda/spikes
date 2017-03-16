package com.novoda.tpbot.human;

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

class SocketIOTpService implements HumanTpService {

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
            options.query = ClientType.HUMAN.rawQuery();
            socket = IO.socket(url.toExternalForm(), options);
        } catch (MalformedURLException | URISyntaxException exception) {
            MalformedServerAddressException exceptionWithUserFacingMessage = new MalformedServerAddressException(exception);
            return Observable.just(Result.from(exceptionWithUserFacingMessage));
        }
        return new SocketConnectionObservable(socket, handler);
    }

    @Override
    public void moveIn(Direction direction) {
        socket.emit(Event.MOVE_IN.rawEvent(), direction);
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
