package com.novoda.tpbot.bot;

import com.novoda.tpbot.observe.Observable;
import com.novoda.tpbot.model.Direction;
import com.novoda.tpbot.model.Result;

interface BotTelepresenceService {

    Observable<Result> connectTo(String serverAddress);

    Observable<Direction> listen();

    void disconnect();

}
