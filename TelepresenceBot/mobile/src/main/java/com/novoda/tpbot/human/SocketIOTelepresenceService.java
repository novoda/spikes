package com.novoda.tpbot.human;

import com.novoda.tpbot.support.Observable;
import com.novoda.tpbot.model.ClientType;
import com.novoda.tpbot.model.Direction;
import com.novoda.tpbot.model.Event;
import com.novoda.tpbot.Executor;
import com.novoda.tpbot.MainLooperExecutor;
import com.novoda.tpbot.error.MalformedServerAddressException;
import com.novoda.tpbot.model.Result;
import com.novoda.tpbot.model.Room;
import com.novoda.tpbot.SocketConnectionObservable;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import io.socket.client.IO;
import io.socket.client.Socket;

class SocketIOTelepresenceService implements HumanTelepresenceService {

    private Socket socket;
    private final Executor executor;

    SocketIOTelepresenceService() {
        this.executor = MainLooperExecutor.newInstance();
    }

    @Override
    public Observable<Result> connectTo(String serverAddress) {
        try {
            URL url = new URL(serverAddress);
            IO.Options options = new IO.Options();
            options.query = ClientType.HUMAN.rawQuery() + "&" + Room.LONDON.rawQuery();
            socket = IO.socket(url.toExternalForm(), options);
        } catch (MalformedURLException | URISyntaxException exception) {
            MalformedServerAddressException exceptionWithUserFacingMessage = new MalformedServerAddressException(exception);
            return Observable.just(Result.from(exceptionWithUserFacingMessage));
        }
        return new SocketConnectionObservable(socket, executor);
    }

    @Override
    public void moveIn(Direction direction) {
        socket.emit(Event.MOVE_IN.rawEvent(), direction.rawDirection());
    }

    @Override
    public void disconnect() {
        if (socket != null) {
            socket.disconnect();
            socket.off();
        }
    }

}
